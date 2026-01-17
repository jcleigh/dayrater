# Research: Historical Insights

**Feature**: 002-historical-insights  
**Date**: 2026-01-17  
**Purpose**: Resolve technical unknowns before implementation

---

## Research Task 1: Charting Library Selection

**Unknown**: Which Jetpack Compose charting library to use for trend graphs (FR-011)?

### Options Evaluated

| Library | GitHub Stars | Last Update | M3 Support | License |
|---------|-------------|-------------|------------|---------|
| **Vico** | 2,900+ | Active (3 days ago) | Dedicated module ✅ | Apache 2.0 |
| YCharts | 682 | 3 years ago ⚠️ | Partial | Apache 2.0 |
| Compose Charts | 189 | 2 years ago ⚠️ | None | MIT |
| Custom Canvas | N/A | N/A | Full control | N/A |

### Decision: **Vico**

**Rationale**:
1. **Actively maintained** — 147 releases, sponsored by Software Mansion, updated within the week
2. **First-class Material 3 support** — `compose-m3` module ensures theming consistency
3. **Perfect feature match** — Line charts with tappable data points (satisfies FR-015), handles sparse data gracefully (satisfies FR-014)
4. **Lightweight** — Modular architecture, ~100-200KB for compose + m3 modules
5. **Future-proof** — Compose Multiplatform support if iOS ever needed
6. **Documentation** — Comprehensive docs and samples at https://guide.vico.patrykandpatrick.com/

**Alternatives Rejected**:
- **YCharts**: Abandoned (last commit 3 years ago, 32 open issues)
- **Compose Charts (bytebeats)**: Too basic, no tap interactions for date/value display
- **Custom Canvas**: Estimated 31-62 hours for equivalent features; not pragmatic per Constitution III

### Integration

```toml
# gradle/libs.versions.toml
[versions]
vico = "2.4.1"

[libraries]
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose", version.ref = "vico" }
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
}
```

---

## Research Task 2: Calendar Heat Map Implementation

**Unknown**: Use existing library or custom Compose Canvas?

### Options Evaluated

1. **Custom Compose Canvas** — Full control, matches app theme perfectly
2. **Kizitonwose Calendar** — Popular calendar library, but designed for date picking not heat maps
3. **Compose Calendar by Boguszpawlowski** — Feature-rich but overkill for simple heat map

### Decision: **Custom Compose Canvas**

**Rationale**:
1. Calendar heat map is visually simple — 7×5 grid of colored squares with day numbers
2. Custom implementation allows perfect Material 3 theme integration
3. Avoids additional dependency for straightforward use case
4. Estimated effort: 4-8 hours (acceptable per Constitution III)

**Implementation Approach**:
```kotlin
@Composable
fun CalendarHeatMap(
    yearMonth: YearMonth,
    dayRatings: Map<LocalDate, RatingValue?>, // null = no rating
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // LazyVerticalGrid with 7 columns (one per weekday)
    // Each cell: colored box + day number
    // Color mapping: NEGATIVE → red, NEUTRAL → yellow, POSITIVE → green, null → gray
}

// Color scheme using Material 3 semantic colors
val ratingColors = mapOf(
    RatingValue.POSITIVE to MaterialTheme.colorScheme.primaryContainer,
    RatingValue.NEUTRAL to MaterialTheme.colorScheme.secondaryContainer,
    RatingValue.NEGATIVE to MaterialTheme.colorScheme.errorContainer,
    null to MaterialTheme.colorScheme.surfaceVariant
)
```

---

## Research Task 3: Room Aggregation Queries

**Unknown**: Perform aggregations in Room SQL or Kotlin code?

### Decision: **Hybrid Approach**

**Rationale**:
- **Room SQL** for date-range filtering (efficient, indexed)
- **Kotlin** for complex aggregations (streak calculation, day-of-week analysis)

**Room Queries** (add to RatingDao):
```kotlin
@Query("""
    SELECT * FROM daily_ratings 
    WHERE date BETWEEN :startDate AND :endDate 
    ORDER BY date ASC
""")
fun getRatingsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyRatingEntity>>

@Query("""
    SELECT date, rating_value FROM daily_ratings 
    WHERE category_id = :categoryId 
    ORDER BY date DESC
""")
fun getRatingHistoryForCategory(categoryId: Long): Flow<List<DateRatingPair>>

@Query("""
    SELECT DISTINCT date FROM daily_ratings 
    ORDER BY date DESC
""")
fun getAllRatedDates(): Flow<List<LocalDate>>
```

**Kotlin Aggregation** (InsightsRepository):
```kotlin
fun calculateStreak(ratedDates: List<LocalDate>): Pair<Int, Int> {
    // Returns (currentStreak, longestStreak)
    // Count consecutive days from today backward for current
    // Track max run for longest
}

fun calculateCategoryAverages(
    ratings: List<DailyRatingEntity>
): Map<Long, Float> {
    // Group by category_id
    // Convert: NEGATIVE=1, NEUTRAL=2, POSITIVE=3
    // Return average per category
}

fun calculateDayOfWeekAverages(
    ratings: List<DailyRatingEntity>
): Map<DayOfWeek, Float> {
    // Group by dayOfWeek
    // Calculate average rating per weekday
}
```

---

## Research Task 4: Week Definition

**Unknown**: How to define week boundaries (FR-001)?

### Decision: **ISO 8601 (Monday start) with locale fallback**

**Rationale**:
- ISO 8601 is the international standard (week starts Monday)
- Android's `WeekFields.of(Locale.getDefault())` respects user's locale
- US users get Sunday start; most others get Monday start
- Consistent with system calendar apps

**Implementation**:
```kotlin
import java.time.temporal.WeekFields
import java.util.Locale

fun getWeekBounds(date: LocalDate): Pair<LocalDate, LocalDate> {
    val weekFields = WeekFields.of(Locale.getDefault())
    val startOfWeek = date.with(weekFields.dayOfWeek(), 1)
    val endOfWeek = startOfWeek.plusDays(6)
    return startOfWeek to endOfWeek
}

// Example usage:
// US locale (Sunday start): getWeekBounds(2026-01-17) → (2026-01-12, 2026-01-18)
// ISO locale (Monday start): getWeekBounds(2026-01-17) → (2026-01-13, 2026-01-19)
```

---

## Research Task 5: Performance Considerations

**Unknown**: How to ensure smooth performance with 1 year of data (SC-001, SC-002, SC-003)?

### Decision: **Lazy loading + Pagination**

**Approach**:
1. **Weekly summary**: Load only 7 days at a time (trivial)
2. **Monthly calendar**: Load only current month + adjacent months for smooth swiping
3. **Trend graphs**: 
   - 1 week/1 month: Load all points
   - 3 months/1 year: Downsample to ~90-365 points max (daily granularity is fine)
4. **Statistics**: Compute once on screen entry, cache in ViewModel

**Room Optimization**:
```kotlin
// Index on date column for fast range queries
@Entity(
    tableName = "daily_ratings",
    indices = [Index(value = ["date"])]
)
data class DailyRatingEntity(...)
```

---

## Summary of Decisions

| Topic | Decision | Confidence |
|-------|----------|------------|
| Charting Library | Vico (compose-m3) | High ✅ |
| Calendar Heat Map | Custom Compose Canvas | High ✅ |
| Aggregation Strategy | Room SQL + Kotlin hybrid | High ✅ |
| Week Definition | Locale-aware (WeekFields) | High ✅ |
| Performance | Lazy loading + date indexing | High ✅ |

**All NEEDS CLARIFICATION items resolved. Proceed to Phase 1.**
