# Repository Contracts: Historical Insights

**Feature**: 002-historical-insights  
**Date**: 2026-01-17

This document defines the InsightsRepository interface that the insights UI depends on. Extends the existing repository layer from 001-daily-rating.

---

## InsightsRepository

Provides aggregated and computed views of rating data for insights screens.

```kotlin
interface InsightsRepository {
    
    // === Weekly Summary (US1) ===
    
    /**
     * Observe the summary for a specific week.
     * Week boundaries determined by device locale (Sunday or Monday start).
     * 
     * @param weekStart The first day of the week to summarize
     * @return Flow emitting WeeklySummary whenever underlying ratings change
     */
    fun observeWeeklySummary(weekStart: LocalDate): Flow<WeeklySummary>
    
    /**
     * Get the daily breakdown for a category within a week.
     * Used when user taps a category in the weekly summary view.
     * 
     * @param categoryId The category to expand
     * @param weekStart First day of the week
     * @return List of daily ratings for that category within the week
     */
    suspend fun getCategoryWeekBreakdown(
        categoryId: Long, 
        weekStart: LocalDate
    ): List<DayRating>
    
    // === Monthly Calendar (US2) ===
    
    /**
     * Observe the calendar heat map data for a specific month.
     * Returns the overall rating indicator for each day.
     * 
     * @param yearMonth The month to display
     * @return Flow emitting MonthlyCalendarData whenever ratings change
     */
    fun observeMonthlyCalendar(yearMonth: YearMonth): Flow<MonthlyCalendarData>
    
    // === Trend Graphs (US3) ===
    
    /**
     * Observe trend data for a specific category over a time range.
     * 
     * @param categoryId The category to graph (use null for "Overall Day")
     * @param timeRange The time range to display
     * @return Flow emitting TrendData whenever ratings change
     */
    fun observeTrendData(
        categoryId: Long?, 
        timeRange: TrendTimeRange
    ): Flow<TrendData>
    
    /**
     * Get all categories available for trend graphing.
     * Returns active categories plus "Overall Day" as a pseudo-option.
     */
    fun observeTrendCategories(): Flow<List<TrendCategoryOption>>
    
    // === Statistics (US4) ===
    
    /**
     * Observe computed statistics across all rating history.
     * Includes streaks, averages, distributions.
     * 
     * @return Flow emitting UserStatistics whenever ratings change
     */
    fun observeStatistics(): Flow<UserStatistics>
    
    // === Helpers ===
    
    /**
     * Get the bounds of the current week based on device locale.
     * 
     * @param referenceDate The date to determine the week for (default: today)
     * @return Pair of (weekStart, weekEnd) dates
     */
    fun getWeekBounds(referenceDate: LocalDate = LocalDate.now()): Pair<LocalDate, LocalDate>
    
    /**
     * Check if there is sufficient data for meaningful insights.
     * Returns false if fewer than 3 days of ratings exist.
     */
    suspend fun hasMinimumDataForInsights(): Boolean
}

// Supporting types

data class DayRating(
    val date: LocalDate,
    val rating: RatingValue?
)

data class TrendCategoryOption(
    val id: Long?,         // null = "Overall Day" pseudo-category
    val name: String,
    val isOverall: Boolean
)
```

---

## Implementation Notes

### InsightsRepositoryImpl Dependencies

```kotlin
@Singleton
class InsightsRepositoryImpl @Inject constructor(
    private val ratingDao: RatingDao,
    private val categoryDao: CategoryDao
) : InsightsRepository {
    // Implementation details in tasks
}
```

### Threading Model

All `Flow`-returning methods:
- Run Room queries on `Dispatchers.IO`
- Perform Kotlin aggregations on `Dispatchers.Default`
- Emit results to UI on `Dispatchers.Main`

### Caching Strategy

- **Weekly summary**: Recomputed on each emission (7 days of data is fast)
- **Monthly calendar**: Recomputed on each emission (31 days max)
- **Trend data**: Cached in ViewModel with time-based invalidation
- **Statistics**: Cached in ViewModel; recomputed on new rating

---

## Extended RatingDao Queries

Add these queries to the existing `RatingDao`:

```kotlin
@Dao
interface RatingDao {
    // ... existing queries from 001-daily-rating ...
    
    // ========== INSIGHTS QUERIES ==========
    
    /**
     * Get all ratings within a date range with category info.
     */
    @Query("""
        SELECT dr.id, dr.date, dr.categoryId, c.name as categoryName, 
               dr.ratingValue, dr.updatedAt
        FROM daily_ratings dr
        LEFT JOIN categories c ON dr.categoryId = c.id
        WHERE dr.date BETWEEN :startDate AND :endDate
        ORDER BY dr.date ASC, c.displayOrder ASC
    """)
    fun observeRatingsInRange(
        startDate: LocalDate, 
        endDate: LocalDate
    ): Flow<List<RatingWithCategory>>
    
    /**
     * Get all distinct dates with ratings (for streak calculation).
     */
    @Query("""
        SELECT DISTINCT date FROM daily_ratings 
        ORDER BY date DESC
    """)
    fun observeAllRatedDates(): Flow<List<LocalDate>>
    
    /**
     * Get rating history for one category (for trend graphs).
     */
    @Query("""
        SELECT date, ratingValue 
        FROM daily_ratings
        WHERE categoryId = :categoryId
        AND date >= :sinceDate
        ORDER BY date ASC
    """)
    fun observeCategoryHistory(
        categoryId: Long, 
        sinceDate: LocalDate
    ): Flow<List<DateRatingPair>>
    
    /**
     * Get "Overall Day" ratings for calendar heat map.
     */
    @Query("""
        SELECT dr.date, dr.ratingValue 
        FROM daily_ratings dr
        INNER JOIN categories c ON dr.categoryId = c.id
        WHERE c.name = 'Overall Day' 
        AND dr.date BETWEEN :startDate AND :endDate
    """)
    fun observeOverallRatingsInRange(
        startDate: LocalDate, 
        endDate: LocalDate
    ): Flow<List<DateRatingPair>>
    
    /**
     * Get rating distribution counts.
     */
    @Query("""
        SELECT ratingValue, COUNT(*) as count
        FROM daily_ratings
        GROUP BY ratingValue
    """)
    fun observeRatingDistribution(): Flow<List<RatingCount>>
    
    /**
     * Count total rated days.
     */
    @Query("SELECT COUNT(DISTINCT date) FROM daily_ratings")
    fun observeTotalRatedDays(): Flow<Int>
}

// Result types
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

## Data Flow Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                          UI Layer                                 │
├──────────────────────────────────────────────────────────────────┤
│  WeeklySummaryVM  │  MonthlyCalendarVM  │  TrendsVM  │  StatsVM  │
│        │                   │                  │            │      │
│        └───────────────────┴──────────────────┴────────────┘      │
│                                    │                              │
│                                    ▼                              │
│                          InsightsRepository                       │
│                                    │                              │
│              ┌─────────────────────┴─────────────────────┐        │
│              ▼                                           ▼        │
│     Room Queries (IO)                          Kotlin Aggregation │
│     ├─ getRatingsInRange()                     ├─ calculateStreak│
│     ├─ getOverallRatings()                     ├─ calculateAvg   │
│     └─ getRatedDates()                         └─ groupByWeekday │
│              │                                           │        │
│              └─────────────────────┬─────────────────────┘        │
│                                    ▼                              │
│                          DayRaterDatabase                         │
│                             (SQLite)                              │
└──────────────────────────────────────────────────────────────────┘
```
