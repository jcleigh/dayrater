# Data Model: Historical Insights

**Feature**: 002-historical-insights  
**Date**: 2026-01-17  
**Source**: [spec.md](spec.md) Key Entities + [research.md](research.md) Design Decisions

## Overview

This feature adds **domain models for aggregated data** on top of the existing 001-daily-rating database schema. No new Room entities are needed—all insights are computed from existing `DailyRating`, `Category`, and `FamilyMember` tables.

## Existing Schema (from 001-daily-rating)

```
┌─────────────────────┐       ┌─────────────────────┐
│   FamilyMember      │       │      Category       │
├─────────────────────┤       ├─────────────────────┤
│ PK id: Long         │──┐    │ PK id: Long         │
│    name: String     │  │    │    name: String     │
│    relationshipType │  └───▶│ FK familyMemberId?  │
│    createdAt: Long  │       │    type: CategoryType│
└─────────────────────┘       │    displayOrder: Int │
                              │    isActive: Boolean │
                              └──────────┬──────────┘
                                         │ 1:N
                                         ▼
                              ┌─────────────────────┐
                              │    DailyRating      │
                              ├─────────────────────┤
                              │ PK id: Long         │
                              │    date: LocalDate  │◄── Indexed
                              │ FK categoryId: Long?│
                              │    ratingValue      │
                              │    updatedAt: Long  │
                              └─────────────────────┘
```

**No schema changes required** — existing index on `date` column supports all insight queries.

---

## New Domain Models (Computed, Not Persisted)

These models represent aggregated views of data, computed on-demand from Room queries.

### WeeklySummary

Aggregated data for a 7-day period.

| Field | Type | Description |
|-------|------|-------------|
| `startDate` | LocalDate | First day of the week (locale-aware) |
| `endDate` | LocalDate | Last day of the week |
| `categorySummaries` | List&lt;CategorySummary&gt; | Aggregation per category |
| `daysRatedCount` | Int | Number of days with at least one rating (0-7) |
| `totalDays` | Int | Always 7 |

```kotlin
data class WeeklySummary(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val categorySummaries: List<CategorySummary>,
    val daysRatedCount: Int,
    val totalDays: Int = 7
) {
    val completionPercent: Float
        get() = daysRatedCount.toFloat() / totalDays
}
```

---

### CategorySummary

Aggregation for one category over a time period.

| Field | Type | Description |
|-------|------|-------------|
| `categoryId` | Long | Reference to Category |
| `categoryName` | String | Display name (denormalized) |
| `positiveCount` | Int | Number of 😊 ratings |
| `neutralCount` | Int | Number of 😐 ratings |
| `negativeCount` | Int | Number of 😢 ratings |
| `totalRatings` | Int | Sum of all counts |

```kotlin
data class CategorySummary(
    val categoryId: Long,
    val categoryName: String,
    val positiveCount: Int,
    val neutralCount: Int,
    val negativeCount: Int
) {
    val totalRatings: Int
        get() = positiveCount + neutralCount + negativeCount
    
    // Average as numeric: NEGATIVE=1, NEUTRAL=2, POSITIVE=3
    val averageScore: Float?
        get() = if (totalRatings == 0) null else {
            (negativeCount * 1 + neutralCount * 2 + positiveCount * 3).toFloat() / totalRatings
        }
    
    // Dominant rating (most frequent)
    val dominantRating: RatingValue?
        get() = when {
            totalRatings == 0 -> null
            positiveCount >= neutralCount && positiveCount >= negativeCount -> RatingValue.POSITIVE
            neutralCount >= negativeCount -> RatingValue.NEUTRAL
            else -> RatingValue.NEGATIVE
        }
}
```

---

### MonthlyCalendarData

A month's worth of daily rating indicators for heat map display.

| Field | Type | Description |
|-------|------|-------------|
| `yearMonth` | YearMonth | The month being displayed |
| `dayRatings` | Map&lt;Int, DayIndicator&gt; | Day number (1-31) → indicator |
| `firstDayOfWeek` | DayOfWeek | Locale-aware first day |

```kotlin
data class MonthlyCalendarData(
    val yearMonth: YearMonth,
    val dayRatings: Map<Int, DayIndicator>,
    val firstDayOfWeek: DayOfWeek
) {
    val daysInMonth: Int
        get() = yearMonth.lengthOfMonth()
    
    // Offset for first day placement in grid (0 = starts on first weekday)
    val startOffset: Int
        get() {
            val firstOfMonth = yearMonth.atDay(1).dayOfWeek
            return (firstOfMonth.value - firstDayOfWeek.value + 7) % 7
        }
}

data class DayIndicator(
    val date: LocalDate,
    val overallRating: RatingValue?,  // null = no rating that day
    val ratingsCount: Int,            // How many categories rated
    val totalCategories: Int          // How many categories exist
) {
    val isComplete: Boolean
        get() = ratingsCount == totalCategories && totalCategories > 0
}
```

---

### TrendDataPoint

A single point on a trend graph.

| Field | Type | Description |
|-------|------|-------------|
| `date` | LocalDate | The date |
| `value` | Float? | Numeric rating (1-3 scale), null if not rated |
| `ratingValue` | RatingValue? | Original enum, null if not rated |

```kotlin
data class TrendDataPoint(
    val date: LocalDate,
    val value: Float?,           // 1.0, 2.0, or 3.0 (or null)
    val ratingValue: RatingValue? // Original enum for display
) {
    companion object {
        fun fromRating(date: LocalDate, rating: RatingValue?): TrendDataPoint {
            val numericValue = when (rating) {
                RatingValue.NEGATIVE -> 1f
                RatingValue.NEUTRAL -> 2f
                RatingValue.POSITIVE -> 3f
                null -> null
            }
            return TrendDataPoint(date, numericValue, rating)
        }
    }
}
```

---

### TrendData

Collection of data points for a trend graph with metadata.

| Field | Type | Description |
|-------|------|-------------|
| `categoryId` | Long | Which category |
| `categoryName` | String | Display name |
| `timeRange` | TrendTimeRange | Selected range |
| `dataPoints` | List&lt;TrendDataPoint&gt; | Ordered by date ascending |

```kotlin
data class TrendData(
    val categoryId: Long,
    val categoryName: String,
    val timeRange: TrendTimeRange,
    val dataPoints: List<TrendDataPoint>
) {
    val averageScore: Float?
        get() {
            val values = dataPoints.mapNotNull { it.value }
            return if (values.isEmpty()) null else values.average().toFloat()
        }
    
    val ratedDaysCount: Int
        get() = dataPoints.count { it.value != null }
}

enum class TrendTimeRange(val days: Int, val label: String) {
    WEEK(7, "1 Week"),
    MONTH(30, "1 Month"),
    THREE_MONTHS(90, "3 Months"),
    YEAR(365, "1 Year"),
    ALL_TIME(-1, "All Time")  // -1 means no limit
}
```

---

### UserStatistics

Computed statistics across all rating history.

| Field | Type | Description |
|-------|------|-------------|
| `currentStreak` | Int | Consecutive days rated ending today |
| `longestStreak` | Int | Best-ever consecutive days |
| `totalDaysRated` | Int | All-time count |
| `categoryAverages` | Map&lt;Long, CategoryAverage&gt; | Per-category stats |
| `dayOfWeekAverages` | Map&lt;DayOfWeek, Float&gt; | Average rating by weekday |
| `overallDistribution` | RatingDistribution | Total counts per rating type |

```kotlin
data class UserStatistics(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDaysRated: Int,
    val firstRatingDate: LocalDate?,
    val categoryAverages: Map<Long, CategoryAverage>,
    val dayOfWeekAverages: Map<DayOfWeek, Float>,
    val overallDistribution: RatingDistribution
)

data class CategoryAverage(
    val categoryId: Long,
    val categoryName: String,
    val averageScore: Float,      // 1.0 - 3.0
    val totalRatings: Int
)

data class RatingDistribution(
    val positiveCount: Int,
    val neutralCount: Int,
    val negativeCount: Int
) {
    val total: Int
        get() = positiveCount + neutralCount + negativeCount
    
    val positivePercent: Float
        get() = if (total == 0) 0f else positiveCount.toFloat() / total * 100
    
    val neutralPercent: Float
        get() = if (total == 0) 0f else neutralCount.toFloat() / total * 100
    
    val negativePercent: Float
        get() = if (total == 0) 0f else negativeCount.toFloat() / total * 100
}
```

---

## New Room Queries (Add to RatingDao)

```kotlin
@Dao
interface RatingDao {
    // Existing queries...
    
    // ========== NEW QUERIES FOR INSIGHTS ==========
    
    /**
     * Get all ratings within a date range, ordered by date.
     * Used for weekly/monthly summaries.
     */
    @Query("""
        SELECT dr.*, c.name as categoryName
        FROM daily_ratings dr
        LEFT JOIN categories c ON dr.categoryId = c.id
        WHERE dr.date BETWEEN :startDate AND :endDate
        ORDER BY dr.date ASC, c.displayOrder ASC
    """)
    fun getRatingsInRange(
        startDate: LocalDate, 
        endDate: LocalDate
    ): Flow<List<RatingWithCategory>>
    
    /**
     * Get all distinct dates that have at least one rating.
     * Used for streak calculation.
     */
    @Query("""
        SELECT DISTINCT date FROM daily_ratings 
        ORDER BY date DESC
    """)
    fun getAllRatedDates(): Flow<List<LocalDate>>
    
    /**
     * Get rating history for a specific category.
     * Used for trend graphs.
     */
    @Query("""
        SELECT date, ratingValue FROM daily_ratings
        WHERE categoryId = :categoryId
        ORDER BY date ASC
    """)
    fun getCategoryRatingHistory(categoryId: Long): Flow<List<DateRatingPair>>
    
    /**
     * Get overall day rating for each date in a range.
     * Used for monthly calendar heat map (assumes "Overall Day" category).
     */
    @Query("""
        SELECT date, ratingValue FROM daily_ratings dr
        INNER JOIN categories c ON dr.categoryId = c.id
        WHERE c.name = 'Overall Day' 
        AND dr.date BETWEEN :startDate AND :endDate
    """)
    fun getOverallRatingsInRange(
        startDate: LocalDate, 
        endDate: LocalDate
    ): Flow<List<DateRatingPair>>
    
    /**
     * Count ratings grouped by rating value.
     * Used for distribution statistics.
     */
    @Query("""
        SELECT ratingValue, COUNT(*) as count
        FROM daily_ratings
        GROUP BY ratingValue
    """)
    fun getRatingDistribution(): Flow<List<RatingCount>>
}

// Helper data classes for query results
data class RatingWithCategory(
    val id: Long,
    val date: LocalDate,
    val categoryId: Long?,
    val categoryName: String?,
    val ratingValue: RatingValue,
    val updatedAt: Long
)

data class DateRatingPair(
    val date: LocalDate,
    val ratingValue: RatingValue
)

data class RatingCount(
    val ratingValue: RatingValue,
    val count: Int
)
```

---

## Aggregation Logic (InsightsRepository)

Key computation logic implemented in Kotlin:

### Streak Calculation

```kotlin
fun calculateStreaks(ratedDates: List<LocalDate>): Pair<Int, Int> {
    if (ratedDates.isEmpty()) return 0 to 0
    
    val sortedDates = ratedDates.sortedDescending()
    val today = LocalDate.now()
    
    // Current streak: count consecutive days from today backward
    var currentStreak = 0
    var expectedDate = today
    for (date in sortedDates) {
        if (date == expectedDate) {
            currentStreak++
            expectedDate = expectedDate.minusDays(1)
        } else if (date < expectedDate) {
            break // Gap found
        }
        // Skip future dates (shouldn't happen, but defensive)
    }
    
    // Longest streak: find max consecutive run
    var longestStreak = 0
    var runLength = 1
    for (i in 0 until sortedDates.size - 1) {
        if (sortedDates[i].minusDays(1) == sortedDates[i + 1]) {
            runLength++
        } else {
            longestStreak = maxOf(longestStreak, runLength)
            runLength = 1
        }
    }
    longestStreak = maxOf(longestStreak, runLength)
    
    return currentStreak to longestStreak
}
```

### Day-of-Week Analysis

```kotlin
fun calculateDayOfWeekAverages(
    ratings: List<RatingWithCategory>
): Map<DayOfWeek, Float> {
    return ratings
        .groupBy { it.date.dayOfWeek }
        .mapValues { (_, dayRatings) ->
            dayRatings.map { it.ratingValue.toNumeric() }.average().toFloat()
        }
}

private fun RatingValue.toNumeric(): Float = when (this) {
    RatingValue.NEGATIVE -> 1f
    RatingValue.NEUTRAL -> 2f
    RatingValue.POSITIVE -> 3f
}
```

---

## Performance Notes

| Operation | Data Size | Expected Time |
|-----------|-----------|---------------|
| Weekly summary | ~7 days × 10 categories = 70 rows | <50ms |
| Monthly calendar | ~31 days × 1 category = 31 rows | <50ms |
| Trend graph (1 year) | ~365 rows per category | <200ms |
| Statistics (all time) | ~365 × 10 = 3,650 rows/year | <500ms |

All computations happen on background threads via Kotlin Flow. ViewModel caches results to avoid recomputation on recomposition.
