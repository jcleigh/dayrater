# Implementation Plan: Historical Insights

**Branch**: `002-historical-insights` | **Date**: 2026-01-17 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-historical-insights/spec.md`

## Summary

Add historical insights and visualization features to DayRater—weekly/monthly summaries, trend graphs, and statistics dashboard. Users can view aggregated rating data across time periods, see calendar heat maps of their daily moods, track rating streaks, and visualize trends with line charts. All computations are local (Room queries + Kotlin aggregation), no external services. Built on the existing 001-daily-rating foundation.

## Technical Context

**Language/Version**: Kotlin 2.x (latest stable, likely 2.1.x)  
**Primary Dependencies**: Jetpack Compose, Room, Hilt, Compose Navigation, Material 3, Charting Library (NEEDS CLARIFICATION)  
**Storage**: Room (SQLite) — queries against existing DailyRatingEntity table  
**Testing**: JUnit 5 + Kotlin Test for aggregation logic, Compose UI testing for graph interactions  
**Target Platform**: Android API 26+ (Android 8.0 Oreo, covers ~95% of devices)  
**Project Type**: Mobile (single Android app, no backend)  
**Performance Goals**: Weekly summary <500ms, monthly calendar 60fps, trend graph <1s for 1 year data  
**Constraints**: Offline-capable (100%), efficient Room queries for large datasets, no additional APK size bloat  
**Scale/Scope**: 1+ year of daily history (~365 rows), 5-15 categories, aggregations computed on-demand

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Status | Notes |
|-----------|-------------|--------|-------|
| I. Modern Android First | Kotlin only, Jetpack Compose, Material 3 | ✅ PASS | All UI in Compose, M3 components |
| I. Modern Android First | No Java, no XML layouts | ✅ PASS | Charts via Vico Compose library |
| I. Modern Android First | Follow official architecture guidelines | ✅ PASS | ViewModel + Repository pattern |
| II. Local-First | Room/SQLite storage | ✅ PASS | Queries existing Room tables |
| II. Local-First | Fully offline, no mandatory cloud | ✅ PASS | All computations local |
| II. Local-First | Export via share intents | N/A | Not applicable to this feature |
| III. Pragmatic Quality | Unit tests for business logic | ✅ PASS | Streak/average calculations tested |
| III. Pragmatic Quality | Keep it simple | ✅ PASS | Vico library vs custom Canvas (31-62h saved) |
| III. Pragmatic Quality | Accessibility built-in | ✅ PASS | Dynamic text, themes, M3 semantics |

**Gate Status**: ✅ PASSED — No violations.

### Post-Design Re-evaluation (Phase 1 Complete)

| Design Decision | Constitution Check | Verdict |
|-----------------|-------------------|---------|
| Vico charting library | III. Pragmatic Quality - avoids 31-62h custom work | ✅ Approved |
| Custom Calendar Heat Map | III. Pragmatic Quality - 4-8h, reasonable scope | ✅ Approved |
| InsightsRepository pattern | I. Modern Android First - follows guidelines | ✅ Approved |
| No new Room entities | II. Local-First - reuses existing schema | ✅ Approved |
| Locale-aware week bounds | Standard Android practice | ✅ Approved |

**Post-Design Gate Status**: ✅ PASSED — All design decisions align with Constitution.

## Project Structure

### Documentation (this feature)

```text
specs/002-historical-insights/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0: Technical decisions (charting library)
├── data-model.md        # Phase 1: Aggregation models
├── quickstart.md        # Phase 1: Setup guide for this feature
├── contracts/           # Phase 1: Internal APIs
│   ├── repositories.md  # InsightsRepository interface
│   └── navigation.md    # New screen routes
└── tasks.md             # Phase 2 output (via /speckit.tasks)
```

### Source Code (repository root)

```text
app/src/main/java/com/dayrater/
├── data/
│   ├── local/
│   │   └── dao/
│   │       └── RatingDao.kt          # Add aggregation queries
│   └── repository/
│       └── InsightsRepository.kt     # NEW: aggregation logic
├── domain/
│   └── model/
│       ├── WeeklySummary.kt          # NEW
│       ├── CategorySummary.kt        # NEW
│       ├── MonthlyCalendarData.kt    # NEW
│       ├── TrendDataPoint.kt         # NEW
│       └── UserStatistics.kt         # NEW
└── ui/
    ├── navigation/
    │   ├── Screen.kt                 # Add new routes
    │   └── NavGraph.kt               # Add new destinations
    └── insights/                     # NEW package
        ├── WeeklySummaryScreen.kt
        ├── WeeklySummaryViewModel.kt
        ├── MonthlyCalendarScreen.kt
        ├── MonthlyCalendarViewModel.kt
        ├── TrendsScreen.kt
        ├── TrendsViewModel.kt
        ├── StatisticsScreen.kt
        ├── StatisticsViewModel.kt
        └── components/
            ├── CalendarHeatMap.kt    # Custom Compose component
            ├── TrendLineChart.kt     # Wrapper around chart library
            ├── WeekNavigator.kt      # Week prev/next controls
            ├── MonthNavigator.kt     # Month prev/next controls
            └── StatCard.kt           # Stat display card

app/src/test/java/com/dayrater/
├── data/repository/
│   └── InsightsRepositoryTest.kt     # NEW
└── domain/
    └── InsightsCalculationsTest.kt   # NEW: streak, average logic
```

**Structure Decision**: Extend existing app module with new `insights/` UI package. Domain models for aggregated data. InsightsRepository encapsulates all aggregation queries and calculations. Builds on existing data layer from 001-daily-rating.

## Complexity Tracking

> No constitution violations. This section is empty.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| — | — | — |
