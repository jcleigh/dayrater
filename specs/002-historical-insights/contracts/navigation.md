# Navigation Contract: Historical Insights

**Feature**: 002-historical-insights  
**Date**: 2026-01-17

Extends the navigation from 001-daily-rating with new insight screens.

---

## Updated Navigation Graph

```
                         ┌─────────────────┐
                         │   App Launch    │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │  Rating Screen  │◀──────────────────────┐
                         │    (Home)       │                       │
                         └────────┬────────┘                       │
                                  │                                │
         ┌────────────────────────┼────────────────────────┐       │
         │                        │                        │       │
         ▼                        ▼                        ▼       │
 ┌───────────────┐       ┌───────────────┐        ┌───────────────┐
 │   Insights    │       │   Settings    │        │    Export     │
 │    (NEW)      │       │    Screen     │        │    Screen     │
 └───────┬───────┘       └───────┬───────┘        └───────────────┘
         │                       │
         │                       ├──────────────┐
         │                       ▼              ▼
         │               ┌───────────────┐ ┌───────────────┐
         │               │ Family Setup  │ │   Custom      │
         │               │    Screen     │ │  Categories   │
         │               └───────────────┘ └───────────────┘
         │
         ├──────────────┬──────────────┬──────────────┐
         ▼              ▼              ▼              ▼
 ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
 │   Weekly     │ │   Monthly    │ │    Trends    │ │  Statistics  │
 │   Summary    │ │   Calendar   │ │    (Graphs)  │ │  Dashboard   │
 │   (NEW)      │ │    (NEW)     │ │    (NEW)     │ │    (NEW)     │
 └──────┬───────┘ └──────┬───────┘ └──────────────┘ └──────────────┘
        │                │
        ▼                ▼
 ┌──────────────┐ ┌──────────────┐
 │  Category    │ │  Day Detail  │
 │  Week Detail │ │   (existing) │
 │    (NEW)     │ └──────────────┘
 └──────────────┘
```

---

## New Route Definitions

Add to existing `Screen` sealed class:

```kotlin
@Serializable
sealed class Screen {
    // ... existing routes from 001-daily-rating ...
    
    // ========== NEW INSIGHT ROUTES ==========
    
    /** Insights hub - entry point for all insight views */
    @Serializable
    data object Insights : Screen()
    
    /** Weekly summary view (US1) */
    @Serializable
    data class WeeklySummary(
        val weekStart: String  // ISO-8601 format: "2026-01-13"
    ) : Screen() {
        companion object {
            /** Navigate to current week */
            fun currentWeek() = WeeklySummary(
                weekStart = getWeekStart(LocalDate.now()).toString()
            )
        }
    }
    
    /** Category breakdown within a week (US1 drill-down) */
    @Serializable
    data class CategoryWeekDetail(
        val categoryId: Long,
        val weekStart: String
    ) : Screen()
    
    /** Monthly calendar heat map (US2) */
    @Serializable
    data class MonthlyCalendar(
        val yearMonth: String  // Format: "2026-01"
    ) : Screen() {
        companion object {
            /** Navigate to current month */
            fun currentMonth() = MonthlyCalendar(
                yearMonth = YearMonth.now().toString()
            )
        }
    }
    
    /** Trend graphs view (US3) */
    @Serializable
    data class Trends(
        val categoryId: Long? = null,  // null = Overall Day
        val timeRange: String = "MONTH" // TrendTimeRange name
    ) : Screen()
    
    /** Statistics dashboard (US4) */
    @Serializable
    data object Statistics : Screen()
}
```

---

## Screen Specifications

### Insights Hub Screen (NEW)

| Property | Value |
|----------|-------|
| Route | `Screen.Insights` |
| Back Navigation | → Rating |
| Bottom Nav | Yes (replaces History) |

**Content**:
- Quick stats cards (current streak, this week's mood)
- Navigation tiles to:
  - Weekly Summary
  - Monthly Calendar
  - Trends
  - Statistics
- "View History" link to day-by-day list (existing History screen)

---

### Weekly Summary Screen (NEW - US1)

| Property | Value |
|----------|-------|
| Route | `Screen.WeeklySummary(weekStart)` |
| Arguments | `weekStart: String` (ISO date) |
| Back Navigation | → Insights |
| Bottom Nav | No |

**Content**:
- Week header with date range (e.g., "Jan 13 - Jan 19, 2026")
- Navigation arrows for prev/next week
- List of categories with weekly aggregation:
  - Category name
  - Emoji distribution bar (e.g., 3😊 2😐 1😢)
  - Tap → CategoryWeekDetail
- "Days rated: 5/7" completion indicator
- Empty state if no ratings this week

---

### Category Week Detail Screen (NEW - US1)

| Property | Value |
|----------|-------|
| Route | `Screen.CategoryWeekDetail(categoryId, weekStart)` |
| Arguments | `categoryId: Long`, `weekStart: String` |
| Back Navigation | → WeeklySummary |
| Bottom Nav | No |

**Content**:
- Category name header
- Week date range
- 7-row list showing each day:
  - Day name (Mon, Tue, etc.)
  - Date
  - Rating emoji or "—" if not rated

---

### Monthly Calendar Screen (NEW - US2)

| Property | Value |
|----------|-------|
| Route | `Screen.MonthlyCalendar(yearMonth)` |
| Arguments | `yearMonth: String` (e.g., "2026-01") |
| Back Navigation | → Insights |
| Bottom Nav | No |

**Content**:
- Month/year header with navigation arrows
- 7-column calendar grid (weekday headers)
- Each day cell:
  - Day number
  - Background color based on overall rating (green/yellow/red/gray)
  - Tap → existing DayDetail screen
- Legend: 😊 = green, 😐 = yellow, 😢 = red, no rating = gray

---

### Trends Screen (NEW - US3)

| Property | Value |
|----------|-------|
| Route | `Screen.Trends(categoryId?, timeRange)` |
| Arguments | `categoryId: Long?`, `timeRange: String` |
| Back Navigation | → Insights |
| Bottom Nav | No |

**Content**:
- Category selector dropdown (Overall Day + all categories)
- Time range tabs: 1W | 1M | 3M | 1Y | All
- Line chart (Vico library):
  - X-axis: dates
  - Y-axis: rating scale (1-3)
  - Data points tappable to show date + rating
- Summary below chart:
  - Average rating
  - Days rated / total days
  - Trend indicator (↑ improving, ↓ declining, → stable)

---

### Statistics Screen (NEW - US4)

| Property | Value |
|----------|-------|
| Route | `Screen.Statistics` |
| Back Navigation | → Insights |
| Bottom Nav | No |

**Content**:
- **Streaks Section**:
  - Current streak (with 🔥 if >7 days)
  - Longest streak ever
  - Total days rated
- **Averages Section**:
  - Per-category average (sorted best to worst)
  - Visual bar or emoji indicator
- **Distribution Section**:
  - Pie/donut chart or bar: % 😊 / 😐 / 😢
  - Total count per rating type
- **Patterns Section** (if sufficient data):
  - Best day of week
  - Worst day of week

---

## Updated Bottom Navigation

Replace "History" with "Insights" in bottom nav:

```kotlin
val bottomNavItems = listOf(
    BottomNavItem(Screen.Rating, "Today", Icons.Default.Today),
    BottomNavItem(Screen.Insights, "Insights", Icons.Default.Insights),
    BottomNavItem(Screen.Settings, "Settings", Icons.Default.Settings)
)
```

The existing History screen becomes accessible:
- From Insights hub via "View History" link
- Or from Monthly Calendar by tapping a day

---

## Navigation Actions

```kotlin
// In InsightsScreen (hub)
fun navigateToWeeklySummary(navController: NavController) {
    navController.navigate(Screen.WeeklySummary.currentWeek())
}

fun navigateToMonthlyCalendar(navController: NavController) {
    navController.navigate(Screen.MonthlyCalendar.currentMonth())
}

fun navigateToTrends(navController: NavController) {
    navController.navigate(Screen.Trends())
}

fun navigateToStatistics(navController: NavController) {
    navController.navigate(Screen.Statistics)
}

// In WeeklySummaryScreen
fun navigateToPreviousWeek(navController: NavController, current: LocalDate) {
    val prevWeek = current.minusWeeks(1)
    navController.navigate(Screen.WeeklySummary(prevWeek.toString()))
}

fun navigateToCategoryDetail(
    navController: NavController, 
    categoryId: Long, 
    weekStart: LocalDate
) {
    navController.navigate(
        Screen.CategoryWeekDetail(categoryId, weekStart.toString())
    )
}

// In MonthlyCalendarScreen
fun navigateToDayDetail(navController: NavController, date: LocalDate) {
    navController.navigate(Screen.DayDetail(date.toString()))
}
```

---

## Deep Links (Future)

| Pattern | Destination |
|---------|-------------|
| `dayrater://insights` | Insights hub |
| `dayrater://week/{date}` | WeeklySummary |
| `dayrater://month/{yearMonth}` | MonthlyCalendar |
| `dayrater://trends` | Trends |
| `dayrater://stats` | Statistics |
