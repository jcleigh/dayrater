# Quickstart: Historical Insights

**Feature**: 002-historical-insights  
**Date**: 2026-01-17

This feature extends the existing DayRater app from 001-daily-rating. Follow these steps to add historical insights functionality.

---

## Prerequisites

- ✅ Completed 001-daily-rating implementation (main branch)
- ✅ App builds and runs with `./gradlew assembleDebug`
- ✅ Have some rating data (at least a few days) for testing insights

---

## Getting Started

```bash
# 1. Ensure you're on the feature branch
git checkout 002-historical-insights

# 2. Verify base app still builds
./gradlew assembleDebug

# 3. Run existing tests
./gradlew test
```

---

## New Dependencies

Add the Vico charting library to your version catalog:

**gradle/libs.versions.toml** (add to existing file):
```toml
[versions]
# ... existing versions ...
vico = "2.4.1"

[libraries]
# ... existing libraries ...
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose", version.ref = "vico" }
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }
```

**app/build.gradle.kts** (add to dependencies):
```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Charting
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
}
```

Sync Gradle after adding.

---

## New Files to Create

### Domain Models
```
app/src/main/java/com/dayrater/domain/model/
├── WeeklySummary.kt       # NEW
├── CategorySummary.kt     # NEW
├── MonthlyCalendarData.kt # NEW
├── TrendDataPoint.kt      # NEW
├── TrendData.kt           # NEW
└── UserStatistics.kt      # NEW
```

### Repository Layer
```
app/src/main/java/com/dayrater/data/repository/
└── InsightsRepository.kt  # NEW (interface + impl)
```

### UI Screens
```
app/src/main/java/com/dayrater/ui/insights/
├── InsightsScreen.kt           # Hub screen
├── InsightsViewModel.kt
├── WeeklySummaryScreen.kt
├── WeeklySummaryViewModel.kt
├── CategoryWeekDetailScreen.kt
├── CategoryWeekDetailViewModel.kt
├── MonthlyCalendarScreen.kt
├── MonthlyCalendarViewModel.kt
├── TrendsScreen.kt
├── TrendsViewModel.kt
├── StatisticsScreen.kt
├── StatisticsViewModel.kt
└── components/
    ├── CalendarHeatMap.kt      # Custom Compose component
    ├── TrendLineChart.kt       # Vico wrapper
    ├── WeekNavigator.kt
    ├── MonthNavigator.kt
    ├── StatCard.kt
    └── EmojiDistributionBar.kt
```

### Tests
```
app/src/test/java/com/dayrater/
├── data/repository/
│   └── InsightsRepositoryTest.kt  # NEW
└── domain/
    └── InsightsCalculationsTest.kt # NEW
```

---

## Files to Modify

### Navigation
- `Screen.kt` — Add new insight routes
- `NavGraph.kt` — Add destinations for new screens
- `BottomNavBar.kt` — Replace "History" with "Insights"

### Data Layer
- `RatingDao.kt` — Add new queries for date ranges, aggregations
- `DatabaseModule.kt` — Provide InsightsRepository

### Resources
- `strings.xml` — Add insight-related strings

---

## Verification Steps

### 1. Dependencies Compile
```bash
./gradlew assembleDebug
# Should compile without errors after adding Vico
```

### 2. Navigation Works
After adding routes:
```
- App launches → Rating screen
- Tap "Insights" in bottom nav → Insights hub
- Navigate to Weekly Summary → back works
- Navigate to Monthly Calendar → tap day → DayDetail
```

### 3. Data Displays
```
- WeeklySummary shows correct date range
- Categories show emoji distribution
- MonthlyCalendar colors days correctly
- Trends graph renders with data points
- Statistics show accurate streak counts
```

### 4. Unit Tests Pass
```bash
./gradlew test
# All existing + new tests pass
```

---

## Testing Insights Locally

### Generate Test Data

If you need more rating data for testing:

```kotlin
// Temporary code in RatingRepositoryImpl for testing
// Remove before committing!
suspend fun seedTestData() {
    val categories = categoryDao.getAllCategories()
    val ratings = listOf(RatingValue.POSITIVE, RatingValue.NEUTRAL, RatingValue.NEGATIVE)
    
    // Generate 30 days of random ratings
    repeat(30) { daysAgo ->
        val date = LocalDate.now().minusDays(daysAgo.toLong())
        categories.forEach { category ->
            val rating = ratings.random()
            ratingDao.upsertRating(
                DailyRatingEntity(
                    date = date,
                    categoryId = category.id,
                    ratingValue = rating,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
```

### Verify Calculations

```kotlin
// Test streak calculation manually
val streaks = insightsRepository.observeStatistics().first()
println("Current streak: ${streaks.currentStreak}")
println("Longest streak: ${streaks.longestStreak}")
```

---

## Common Issues

### Vico chart not rendering
- Ensure both `vico-compose` AND `vico-compose-m3` are imported
- Chart needs explicit height: `Modifier.height(200.dp)`
- Data must have at least 2 points for a line

### Calendar days misaligned
- Check locale settings for week start day
- Use `WeekFields.of(Locale.getDefault())` consistently

### Statistics showing incorrect streak
- Streak counts consecutive days with ANY rating
- Today must be rated for current streak to include today

### Room query errors
- Ensure TypeConverters handle YearMonth if used
- Use String for dates in route arguments, parse in ViewModel

---

## Feature Completion Checklist

- [ ] Vico dependency added and compiles
- [ ] InsightsRepository interface implemented
- [ ] RatingDao extended with insight queries
- [ ] All 4 insight screens render correctly
- [ ] Navigation between screens works
- [ ] Bottom nav updated to show "Insights"
- [ ] Calendar heat map colors match rating values
- [ ] Trend graph shows correct data points
- [ ] Statistics calculate accurately
- [ ] Unit tests for streak/average calculations
- [ ] Empty states display when no data
- [ ] Previous/next navigation for weeks/months works

---

## Next Steps

After completing this feature:

1. **Manual Testing**
   - Rate a few days across a week
   - Verify weekly summary matches
   - Check calendar colors are correct
   - Confirm streak counts are accurate

2. **Code Review**
   - Self-review all changes
   - Run lint: `./gradlew lint`
   - Run tests: `./gradlew test`

3. **Merge**
   - Merge `002-historical-insights` → `main`
   - Tag release: `git tag v1.1.0`
   - Push: `git push --tags`

---

## Reference Documents

- [spec.md](spec.md) — Feature requirements
- [research.md](research.md) — Technical decisions (Vico, etc.)
- [data-model.md](data-model.md) — Domain models for insights
- [contracts/repositories.md](contracts/repositories.md) — InsightsRepository API
- [contracts/navigation.md](contracts/navigation.md) — New screen routes
