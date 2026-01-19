# Tasks: Historical Insights

**Input**: Design documents from `/specs/002-historical-insights/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Not explicitly requested — unit tests for business logic only per Constitution III (Pragmatic Quality).

**Organization**: Tasks grouped by user story for independent implementation and testing.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US#]**: Which user story this task belongs to
- All paths relative to repository root

---

## Phase 1: Setup (Dependencies & Configuration)

**Purpose**: Add Vico charting library and configure for insights feature

- [ ] T001 Add Vico library versions to `gradle/libs.versions.toml`
- [ ] T002 Add Vico dependencies to `app/build.gradle.kts`
- [ ] T003 [P] Add insight-related strings to `app/src/main/res/values/strings.xml`

**Checkpoint**: `./gradlew assembleDebug` succeeds with Vico dependency

---

## Phase 2: Foundational (Domain Models & Data Layer)

**Purpose**: Core domain models and repository infrastructure required by ALL user stories

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Domain Models

- [ ] T004 [P] Create TrendTimeRange enum in `app/src/main/java/com/dayrater/domain/model/TrendTimeRange.kt`
- [ ] T005 [P] Create CategorySummary data class in `app/src/main/java/com/dayrater/domain/model/CategorySummary.kt`
- [ ] T006 [P] Create WeeklySummary data class in `app/src/main/java/com/dayrater/domain/model/WeeklySummary.kt`
- [ ] T007 [P] Create DayIndicator data class in `app/src/main/java/com/dayrater/domain/model/DayIndicator.kt`
- [ ] T008 [P] Create MonthlyCalendarData data class in `app/src/main/java/com/dayrater/domain/model/MonthlyCalendarData.kt`
- [ ] T009 [P] Create TrendDataPoint data class in `app/src/main/java/com/dayrater/domain/model/TrendDataPoint.kt`
- [ ] T010 [P] Create TrendData data class in `app/src/main/java/com/dayrater/domain/model/TrendData.kt`
- [ ] T011 [P] Create RatingDistribution data class in `app/src/main/java/com/dayrater/domain/model/RatingDistribution.kt`
- [ ] T012 [P] Create CategoryAverage data class in `app/src/main/java/com/dayrater/domain/model/CategoryAverage.kt`
- [ ] T013 [P] Create UserStatistics data class in `app/src/main/java/com/dayrater/domain/model/UserStatistics.kt`

### Data Layer Extensions

- [ ] T014 Create RatingWithCategory result class in `app/src/main/java/com/dayrater/data/local/dao/RatingDao.kt`
- [ ] T015 Create DateRatingPair result class in `app/src/main/java/com/dayrater/data/local/dao/RatingDao.kt`
- [ ] T016 Create RatingCount result class in `app/src/main/java/com/dayrater/data/local/dao/RatingDao.kt`
- [ ] T017 Add observeRatingsInRange() query to RatingDao
- [ ] T018 [P] Add observeAllRatedDates() query to RatingDao
- [ ] T019 [P] Add observeCategoryHistory() query to RatingDao
- [ ] T020 [P] Add observeOverallRatingsInRange() query to RatingDao
- [ ] T021 [P] Add observeRatingDistribution() query to RatingDao
- [ ] T022 [P] Add observeTotalRatedDays() query to RatingDao

### Repository Layer

- [ ] T023 Create InsightsRepository interface in `app/src/main/java/com/dayrater/data/repository/InsightsRepository.kt`
- [ ] T024 Create InsightsRepositoryImpl skeleton in `app/src/main/java/com/dayrater/data/repository/InsightsRepositoryImpl.kt`
- [ ] T025 Add InsightsRepository to DatabaseModule in `app/src/main/java/com/dayrater/di/DatabaseModule.kt`

### Navigation Setup

- [ ] T026 Add new insight routes to Screen sealed class in `app/src/main/java/com/dayrater/ui/navigation/Screen.kt`
- [ ] T027 Update BottomNavBar to replace History with Insights in `app/src/main/java/com/dayrater/ui/navigation/BottomNavBar.kt`

**Checkpoint**: App compiles, navigation shows Insights tab, InsightsRepository injectable

---

## Phase 3: User Story 1 - Weekly Summary View (Priority: P1) 🎯 MVP

**Goal**: Users can view a summary of the past week with emoji distribution per category

**Independent Test**: Rate 4+ days → Insights → Weekly Summary → see aggregated emoji counts per category

### Repository Implementation

- [ ] T028 [US1] Implement getWeekBounds() helper in InsightsRepositoryImpl
- [ ] T029 [US1] Implement observeWeeklySummary() in InsightsRepositoryImpl
- [ ] T030 [US1] Implement getCategoryWeekBreakdown() in InsightsRepositoryImpl

### UI Components

- [ ] T031 [P] [US1] Create WeekNavigator component in `app/src/main/java/com/dayrater/ui/insights/components/WeekNavigator.kt`
- [ ] T032 [P] [US1] Create EmojiDistributionBar component in `app/src/main/java/com/dayrater/ui/insights/components/EmojiDistributionBar.kt`

### Screens

- [ ] T033 [US1] Create WeeklySummaryUiState in `app/src/main/java/com/dayrater/ui/insights/WeeklySummaryUiState.kt`
- [ ] T034 [US1] Create WeeklySummaryViewModel in `app/src/main/java/com/dayrater/ui/insights/WeeklySummaryViewModel.kt`
- [ ] T035 [US1] Implement WeeklySummaryScreen in `app/src/main/java/com/dayrater/ui/insights/WeeklySummaryScreen.kt`
- [ ] T036 [US1] Create CategoryWeekDetailUiState in `app/src/main/java/com/dayrater/ui/insights/CategoryWeekDetailUiState.kt`
- [ ] T037 [US1] Create CategoryWeekDetailViewModel in `app/src/main/java/com/dayrater/ui/insights/CategoryWeekDetailViewModel.kt`
- [ ] T038 [US1] Implement CategoryWeekDetailScreen in `app/src/main/java/com/dayrater/ui/insights/CategoryWeekDetailScreen.kt`
- [ ] T039 [US1] Add WeeklySummary and CategoryWeekDetail destinations to NavGraph

### Unit Tests

- [ ] T040 [P] [US1] Create streak/week calculation tests in `app/src/test/java/com/dayrater/domain/InsightsCalculationsTest.kt`

**Checkpoint**: US1 complete — Weekly summary displays, navigation between weeks works, category drill-down works

---

## Phase 4: User Story 2 - Monthly Overview (Priority: P2)

**Goal**: Users can see a monthly calendar heat map with color-coded days

**Independent Test**: Rate several days → Monthly Calendar → see color-coded calendar grid

### Repository Implementation

- [ ] T041 [US2] Implement observeMonthlyCalendar() in InsightsRepositoryImpl

### UI Components

- [ ] T042 [P] [US2] Create MonthNavigator component in `app/src/main/java/com/dayrater/ui/insights/components/MonthNavigator.kt`
- [ ] T043 [US2] Create CalendarHeatMap component in `app/src/main/java/com/dayrater/ui/insights/components/CalendarHeatMap.kt`

### Screens

- [ ] T044 [US2] Create MonthlyCalendarUiState in `app/src/main/java/com/dayrater/ui/insights/MonthlyCalendarUiState.kt`
- [ ] T045 [US2] Create MonthlyCalendarViewModel in `app/src/main/java/com/dayrater/ui/insights/MonthlyCalendarViewModel.kt`
- [ ] T046 [US2] Implement MonthlyCalendarScreen in `app/src/main/java/com/dayrater/ui/insights/MonthlyCalendarScreen.kt`
- [ ] T047 [US2] Add MonthlyCalendar destination to NavGraph

**Checkpoint**: US2 complete — Calendar displays with correct colors, day tap → DayDetail, month navigation works

---

## Phase 5: User Story 3 - Trend Graphs (Priority: P3)

**Goal**: Users can see line graphs showing rating trends over time

**Independent Test**: Rate 2+ weeks → Trends → see line graph with data points

### Repository Implementation

- [ ] T048 [US3] Implement observeTrendData() in InsightsRepositoryImpl
- [ ] T049 [US3] Implement observeTrendCategories() in InsightsRepositoryImpl

### UI Components

- [ ] T050 [US3] Create TrendLineChart component (Vico wrapper) in `app/src/main/java/com/dayrater/ui/insights/components/TrendLineChart.kt`

### Screens

- [ ] T051 [US3] Create TrendsUiState in `app/src/main/java/com/dayrater/ui/insights/TrendsUiState.kt`
- [ ] T052 [US3] Create TrendsViewModel in `app/src/main/java/com/dayrater/ui/insights/TrendsViewModel.kt`
- [ ] T053 [US3] Implement TrendsScreen in `app/src/main/java/com/dayrater/ui/insights/TrendsScreen.kt`
- [ ] T054 [US3] Add Trends destination to NavGraph

**Checkpoint**: US3 complete — Graph renders, category selector works, time range tabs work

---

## Phase 6: User Story 4 - Statistics Dashboard (Priority: P4)

**Goal**: Users can see key statistics (streaks, averages, distribution)

**Independent Test**: Rate 2+ weeks → Statistics → see streak counts, averages, distribution

### Repository Implementation

- [ ] T055 [US4] Implement calculateStreaks() helper in InsightsRepositoryImpl
- [ ] T056 [US4] Implement calculateDayOfWeekAverages() helper in InsightsRepositoryImpl
- [ ] T057 [US4] Implement observeStatistics() in InsightsRepositoryImpl
- [ ] T058 [US4] Implement hasMinimumDataForInsights() in InsightsRepositoryImpl

### UI Components

- [ ] T059 [P] [US4] Create StatCard component in `app/src/main/java/com/dayrater/ui/insights/components/StatCard.kt`
- [ ] T060 [P] [US4] Create DistributionBar component in `app/src/main/java/com/dayrater/ui/insights/components/DistributionBar.kt`

### Screens

- [ ] T061 [US4] Create StatisticsUiState in `app/src/main/java/com/dayrater/ui/insights/StatisticsUiState.kt`
- [ ] T062 [US4] Create StatisticsViewModel in `app/src/main/java/com/dayrater/ui/insights/StatisticsViewModel.kt`
- [ ] T063 [US4] Implement StatisticsScreen in `app/src/main/java/com/dayrater/ui/insights/StatisticsScreen.kt`
- [ ] T064 [US4] Add Statistics destination to NavGraph

### Unit Tests

- [ ] T065 [P] [US4] Add streak calculation tests to InsightsCalculationsTest
- [ ] T066 [P] [US4] Add day-of-week average tests to InsightsCalculationsTest

**Checkpoint**: US4 complete — Stats display correctly, streaks accurate, distribution shows

---

## Phase 7: Insights Hub & Integration

**Purpose**: Create the hub screen that ties all insights together

- [ ] T067 Create InsightsUiState in `app/src/main/java/com/dayrater/ui/insights/InsightsUiState.kt`
- [ ] T068 Create InsightsViewModel in `app/src/main/java/com/dayrater/ui/insights/InsightsViewModel.kt`
- [ ] T069 Implement InsightsScreen (hub) in `app/src/main/java/com/dayrater/ui/insights/InsightsScreen.kt`
- [ ] T070 Add Insights hub destination to NavGraph
- [ ] T071 Add "View History" link from Insights hub to existing History screen

**Checkpoint**: Full insights navigation working from bottom nav → hub → all insight screens

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Quality improvements across all user stories

- [ ] T072 [P] Add empty state handling to all insight screens (no data messaging)
- [ ] T073 [P] Add loading states to all insight ViewModels
- [ ] T074 [P] Add error handling and user feedback (Snackbars) to insight screens
- [ ] T075 [P] Ensure all insight screens respect system text size and theme
- [ ] T076 Create InsightsRepositoryTest in `app/src/test/java/com/dayrater/data/repository/InsightsRepositoryTest.kt`
- [ ] T077 Run full quickstart.md validation on clean build

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup) ──────────────────────────────────────────┐
                                                          │
Phase 2 (Foundational) ◀──────────────────────────────────┘
         │
         ├────────────┬────────────┬────────────┐
         ▼            ▼            ▼            ▼
      Phase 3      Phase 4      Phase 5      Phase 6
       (US1)        (US2)        (US3)        (US4)
         │            │            │            │
         └────────────┴────────────┴────────────┘
                           │
                           ▼
                      Phase 7 (Hub)
                           │
                           ▼
                      Phase 8 (Polish)
```

### User Story Dependencies

| Story | Depends On | Can Parallel With |
|-------|------------|-------------------|
| US1 (P1) | Phase 2 only | — (MVP, do first) |
| US2 (P2) | Phase 2 only | US1, US3, US4 |
| US3 (P3) | Phase 2 only | US1, US2, US4 |
| US4 (P4) | Phase 2 only | US1, US2, US3 |

### Parallel Opportunities

```
# Phase 2 parallel tasks (domain models):
T004-T013: All domain model files (10 files in parallel)

# Phase 2 parallel tasks (DAO queries):
T018-T022: All new RatingDao queries (5 queries in parallel)

# Phase 3 parallel tasks:
T031, T032: WeekNavigator, EmojiDistributionBar components

# Phase 4 parallel tasks:
T042: MonthNavigator component

# Phase 6 parallel tasks:
T059, T060: StatCard, DistributionBar components
T065, T066: Unit tests

# Phase 8 parallel tasks:
T072-T075: All polish tasks
```

---

## Implementation Strategy

### MVP First (Recommended)

1. ✅ Complete Phase 1: Setup (add Vico)
2. ✅ Complete Phase 2: Foundational (domain models, repository)
3. ✅ Complete Phase 3: User Story 1 (Weekly Summary)
4. **STOP**: Build APK, validate weekly summary works
5. Continue with US2-US4 based on priority

### Task Count Summary

| Phase | Tasks | Parallel | Sequential |
|-------|-------|----------|------------|
| Phase 1: Setup | 3 | 1 | 2 |
| Phase 2: Foundational | 24 | 17 | 7 |
| Phase 3: US1 | 13 | 3 | 10 |
| Phase 4: US2 | 7 | 1 | 6 |
| Phase 5: US3 | 7 | 0 | 7 |
| Phase 6: US4 | 12 | 4 | 8 |
| Phase 7: Hub | 5 | 0 | 5 |
| Phase 8: Polish | 6 | 4 | 2 |
| **Total** | **77** | **30** | **47** |

### Independent MVP Test Criteria

After completing through Phase 3:
- [ ] App builds with `./gradlew assembleDebug`
- [ ] Bottom nav shows "Insights" instead of "History"
- [ ] Tapping Insights → shows hub (or weekly summary directly)
- [ ] Weekly summary shows correct date range
- [ ] Categories show emoji distribution (e.g., 2😊 1😐)
- [ ] Week navigation arrows work (prev/next)
- [ ] Tapping category → shows daily breakdown

---

## Notes

- All paths are relative to repository root
- [P] = parallelizable with other [P] tasks in same phase
- [US#] = belongs to that user story (for traceability)
- Commit after each task or logical group
- Run `./gradlew test` after each phase
- Constitution compliance: Kotlin only, Jetpack Compose, Room queries, Hilt DI
- Vico library: Use `compose-m3` module for Material 3 theming
