# Tasks: Daily Rating Core

**Input**: Design documents from `/specs/001-daily-rating/`  
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: Not explicitly requested — unit tests for business logic only per Constitution III (Pragmatic Quality).

**Organization**: Tasks grouped by user story for independent implementation and testing.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[US#]**: Which user story this task belongs to
- All paths relative to repository root

---

## Phase 1: Setup (Project Infrastructure)

**Purpose**: Initialize Android project with all dependencies and tooling

- [ ] T001 Create Android project with Gradle Kotlin DSL in `app/build.gradle.kts`, `settings.gradle.kts`
- [ ] T002 Configure version catalog with all dependencies in `gradle/libs.versions.toml`
- [ ] T003 [P] Create Dev Container configuration in `.devcontainer/devcontainer.json`
- [ ] T004 [P] Create Android SDK setup script in `.devcontainer/setup-android-sdk.sh`
- [ ] T005 [P] Create GitHub Actions build workflow in `.github/workflows/build.yml`
- [ ] T006 [P] Create GitHub Actions release workflow in `.github/workflows/release.yml`
- [ ] T007 [P] Configure Hilt in `app/src/main/java/com/dayrater/DayRaterApplication.kt`
- [ ] T008 Create `AndroidManifest.xml` with FileProvider for export in `app/src/main/AndroidManifest.xml`

**Checkpoint**: `./gradlew assembleDebug` succeeds, empty app installs on device

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure required by ALL user stories

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Database Layer

- [ ] T009 Create RatingValue enum in `app/src/main/java/com/dayrater/data/local/entity/RatingValue.kt`
- [ ] T010 [P] Create CategoryType enum in `app/src/main/java/com/dayrater/data/local/entity/CategoryType.kt`
- [ ] T011 [P] Create RelationshipType enum in `app/src/main/java/com/dayrater/data/local/entity/RelationshipType.kt`
- [ ] T012 Create CategoryEntity in `app/src/main/java/com/dayrater/data/local/entity/CategoryEntity.kt`
- [ ] T013 [P] Create FamilyMemberEntity in `app/src/main/java/com/dayrater/data/local/entity/FamilyMemberEntity.kt`
- [ ] T014 Create DailyRatingEntity in `app/src/main/java/com/dayrater/data/local/entity/DailyRatingEntity.kt`
- [ ] T015 Create Room TypeConverters in `app/src/main/java/com/dayrater/data/local/Converters.kt`
- [ ] T016 Create CategoryDao in `app/src/main/java/com/dayrater/data/local/dao/CategoryDao.kt`
- [ ] T017 [P] Create FamilyMemberDao in `app/src/main/java/com/dayrater/data/local/dao/FamilyMemberDao.kt`
- [ ] T018 [P] Create RatingDao in `app/src/main/java/com/dayrater/data/local/dao/RatingDao.kt`
- [ ] T019 Create DayRaterDatabase with all DAOs in `app/src/main/java/com/dayrater/data/local/DayRaterDatabase.kt`
- [ ] T020 Create DatabaseModule for Hilt DI in `app/src/main/java/com/dayrater/di/DatabaseModule.kt`

### Domain Layer

- [ ] T021 [P] Create Category domain model in `app/src/main/java/com/dayrater/domain/model/Category.kt`
- [ ] T022 [P] Create FamilyMember domain model in `app/src/main/java/com/dayrater/domain/model/FamilyMember.kt`
- [ ] T023 [P] Create Rating domain model in `app/src/main/java/com/dayrater/domain/model/Rating.kt`
- [ ] T024 Create DayRatings domain model in `app/src/main/java/com/dayrater/domain/model/DayRatings.kt`

### Theme & Navigation

- [ ] T025 [P] Create Color.kt with Material 3 color scheme in `app/src/main/java/com/dayrater/ui/theme/Color.kt`
- [ ] T026 [P] Create Type.kt with typography in `app/src/main/java/com/dayrater/ui/theme/Type.kt`
- [ ] T027 Create Theme.kt with light/dark themes in `app/src/main/java/com/dayrater/ui/theme/Theme.kt`
- [ ] T028 Create Screen sealed class for navigation in `app/src/main/java/com/dayrater/ui/navigation/Screen.kt`
- [ ] T029 Create NavGraph with all routes in `app/src/main/java/com/dayrater/ui/navigation/NavGraph.kt`
- [ ] T030 Create BottomNavBar component in `app/src/main/java/com/dayrater/ui/components/BottomNavBar.kt`
- [ ] T031 Create MainActivity with Compose setup in `app/src/main/java/com/dayrater/MainActivity.kt`

### Shared UI Components

- [ ] T032 Create EmojiRatingSelector component in `app/src/main/java/com/dayrater/ui/components/EmojiRatingSelector.kt`
- [ ] T033 Create CategoryCard component in `app/src/main/java/com/dayrater/ui/components/CategoryCard.kt`

### Resources

- [ ] T034 [P] Create strings.xml with all UI strings in `app/src/main/res/values/strings.xml`
- [ ] T035 [P] Create themes.xml (light) in `app/src/main/res/values/themes.xml`
- [ ] T036 [P] Create themes.xml (dark) in `app/src/main/res/values-night/themes.xml`

**Checkpoint**: App launches with empty rating screen, navigation works, theme switches

---

## Phase 3: User Story 1 - Rate My Day (Priority: P1) 🎯 MVP

**Goal**: Users can rate today across all default categories with 3-emoji scale, ratings persist

**Independent Test**: Open app → tap emojis for each category → close app → reopen → ratings visible

### Repository Layer

- [ ] T037 [US1] Create RatingRepository interface in `app/src/main/java/com/dayrater/data/repository/RatingRepository.kt`
- [ ] T038 [US1] Implement RatingRepositoryImpl with Room DAOs in `app/src/main/java/com/dayrater/data/repository/RatingRepositoryImpl.kt`
- [ ] T039 [US1] Add default category seeding logic to DayRaterDatabase callback

### UI Implementation

- [ ] T040 [US1] Create RatingUiState data class in `app/src/main/java/com/dayrater/ui/rating/RatingUiState.kt`
- [ ] T041 [US1] Create RatingViewModel in `app/src/main/java/com/dayrater/ui/rating/RatingViewModel.kt`
- [ ] T042 [US1] Implement RatingScreen composable in `app/src/main/java/com/dayrater/ui/rating/RatingScreen.kt`

### Unit Tests

- [ ] T043 [P] [US1] Create RatingRepositoryTest in `app/src/test/java/com/dayrater/data/repository/RatingRepositoryTest.kt`

**Checkpoint**: US1 complete — can rate today's categories, data persists across app restart

---

## Phase 4: User Story 2 - Configure Family Members (Priority: P2)

**Goal**: Users can set spouse name and children names, categories update dynamically

**Independent Test**: Settings → add spouse "Alex" + child "Emma" → rating screen shows personalized labels

### Repository Layer

- [ ] T044 [US2] Create FamilyRepository interface in `app/src/main/java/com/dayrater/data/repository/FamilyRepository.kt`
- [ ] T045 [US2] Implement FamilyRepositoryImpl in `app/src/main/java/com/dayrater/data/repository/FamilyRepositoryImpl.kt`

### UI Implementation

- [ ] T046 [US2] Create SettingsUiState data class in `app/src/main/java/com/dayrater/ui/settings/SettingsUiState.kt`
- [ ] T047 [US2] Create SettingsViewModel in `app/src/main/java/com/dayrater/ui/settings/SettingsViewModel.kt`
- [ ] T048 [US2] Implement SettingsScreen composable in `app/src/main/java/com/dayrater/ui/settings/SettingsScreen.kt`
- [ ] T049 [US2] Create FamilySetupUiState data class in `app/src/main/java/com/dayrater/ui/settings/FamilySetupUiState.kt`
- [ ] T050 [US2] Create FamilySetupViewModel in `app/src/main/java/com/dayrater/ui/settings/FamilySetupViewModel.kt`
- [ ] T051 [US2] Implement FamilySetupScreen composable in `app/src/main/java/com/dayrater/ui/settings/FamilySetupScreen.kt`

### Unit Tests

- [ ] T052 [P] [US2] Create FamilyRepositoryTest in `app/src/test/java/com/dayrater/data/repository/FamilyRepositoryTest.kt`

**Checkpoint**: US2 complete — family config works, rating categories update dynamically

---

## Phase 5: User Story 3 - View Rating History (Priority: P3)

**Goal**: Users can browse past days and see category breakdowns for any historical day

**Independent Test**: Rate several days → History screen shows list → tap day → see all ratings

### UI Implementation

- [ ] T053 [US3] Create HistoryUiState data class in `app/src/main/java/com/dayrater/ui/history/HistoryUiState.kt`
- [ ] T054 [US3] Create HistoryViewModel in `app/src/main/java/com/dayrater/ui/history/HistoryViewModel.kt`
- [ ] T055 [US3] Implement HistoryScreen composable in `app/src/main/java/com/dayrater/ui/history/HistoryScreen.kt`
- [ ] T056 [US3] Create DayDetailUiState data class in `app/src/main/java/com/dayrater/ui/history/DayDetailUiState.kt`
- [ ] T057 [US3] Create DayDetailViewModel in `app/src/main/java/com/dayrater/ui/history/DayDetailViewModel.kt`
- [ ] T058 [US3] Implement DayDetailScreen composable in `app/src/main/java/com/dayrater/ui/history/DayDetailScreen.kt`

**Checkpoint**: US3 complete — can browse history and view any past day's ratings

---

## Phase 6: User Story 4 - Add Custom Categories (Priority: P4)

**Goal**: Users can create and manage custom rating categories

**Independent Test**: Settings → Custom Categories → add "Work Stress" → appears on rating screen

### UI Implementation

- [ ] T059 [US4] Create CustomCategoriesUiState data class in `app/src/main/java/com/dayrater/ui/settings/CustomCategoriesUiState.kt`
- [ ] T060 [US4] Create CustomCategoriesViewModel in `app/src/main/java/com/dayrater/ui/settings/CustomCategoriesViewModel.kt`
- [ ] T061 [US4] Implement CustomCategoriesScreen composable in `app/src/main/java/com/dayrater/ui/settings/CustomCategoriesScreen.kt`
- [ ] T062 [US4] Add category management methods to RatingRepository

**Checkpoint**: US4 complete — custom categories can be added/removed/renamed

---

## Phase 7: User Story 5 - Export My Data (Priority: P5)

**Goal**: Users can export all ratings to CSV/JSON via Android share sheet

**Independent Test**: Accumulate ratings → Export screen → tap export → share sheet opens with file

### Repository Layer

- [ ] T063 [US5] Add exportToCsv() method to RatingRepository
- [ ] T064 [US5] Add exportToJson() method to RatingRepository

### UI Implementation

- [ ] T065 [US5] Create ExportUiState data class in `app/src/main/java/com/dayrater/ui/export/ExportUiState.kt`
- [ ] T066 [US5] Create ExportViewModel in `app/src/main/java/com/dayrater/ui/export/ExportViewModel.kt`
- [ ] T067 [US5] Implement ExportScreen composable in `app/src/main/java/com/dayrater/ui/export/ExportScreen.kt`
- [ ] T068 [US5] Implement share intent with FileProvider in ExportViewModel

**Checkpoint**: US5 complete — data exports correctly, shareable to Drive/email/etc.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Quality improvements across all user stories

- [ ] T069 [P] Create SettingsRepository for theme preference in `app/src/main/java/com/dayrater/data/repository/SettingsRepository.kt`
- [ ] T070 Implement theme switching in SettingsScreen using DataStore
- [ ] T071 [P] Add empty state handling to HistoryScreen
- [ ] T072 [P] Add loading states to all ViewModels
- [ ] T073 [P] Add error handling and user feedback (Snackbars) across screens
- [ ] T074 Update README.md with project description and build instructions
- [ ] T075 Run full quickstart.md validation on clean clone

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup) ─────────────────────────────────────────┐
                                                         │
Phase 2 (Foundational) ◀─────────────────────────────────┘
         │
         ├──────────┬──────────┬──────────┬──────────────┐
         ▼          ▼          ▼          ▼              ▼
      Phase 3    Phase 4    Phase 5    Phase 6       Phase 7
       (US1)      (US2)      (US3)      (US4)         (US5)
         │          │          │          │              │
         └──────────┴──────────┴──────────┴──────────────┘
                                   │
                                   ▼
                            Phase 8 (Polish)
```

### User Story Dependencies

| Story | Depends On | Can Parallel With |
|-------|------------|-------------------|
| US1 (P1) | Phase 2 only | — (MVP, do first) |
| US2 (P2) | Phase 2 only | US1, US3, US4, US5 |
| US3 (P3) | Phase 2 only | US1, US2, US4, US5 |
| US4 (P4) | Phase 2 only | US1, US2, US3, US5 |
| US5 (P5) | Phase 2 only | US1, US2, US3, US4 |

### Parallel Opportunities

```
# Phase 1 parallel tasks (T003-T008):
T003: .devcontainer/devcontainer.json
T004: .devcontainer/setup-android-sdk.sh
T005: .github/workflows/build.yml
T006: .github/workflows/release.yml
T007: DayRaterApplication.kt
T008: AndroidManifest.xml

# Phase 2 parallel tasks (enums):
T009, T010, T011: All enum files

# Phase 2 parallel tasks (DAOs):
T016, T017, T018: All DAO files

# Phase 2 parallel tasks (domain models):
T021, T022, T023: All domain model files

# Phase 2 parallel tasks (theme):
T025, T026: Color.kt, Type.kt

# Phase 2 parallel tasks (resources):
T034, T035, T036: All XML resources
```

---

## Implementation Strategy

### MVP First (Recommended)

1. ✅ Complete Phase 1: Setup
2. ✅ Complete Phase 2: Foundational
3. ✅ Complete Phase 3: User Story 1 (Rate My Day)
4. **STOP**: Build APK, install on family devices, validate
5. Continue with US2-US5 based on feedback

### Task Count Summary

| Phase | Tasks | Parallel | Sequential |
|-------|-------|----------|------------|
| Phase 1: Setup | 8 | 6 | 2 |
| Phase 2: Foundational | 28 | 15 | 13 |
| Phase 3: US1 | 7 | 1 | 6 |
| Phase 4: US2 | 9 | 1 | 8 |
| Phase 5: US3 | 6 | 0 | 6 |
| Phase 6: US4 | 4 | 0 | 4 |
| Phase 7: US5 | 6 | 0 | 6 |
| Phase 8: Polish | 7 | 4 | 3 |
| **Total** | **75** | **27** | **48** |

### Independent MVP Test Criteria

After completing through Phase 3:
- [ ] App builds with `./gradlew assembleDebug`
- [ ] App installs on physical device
- [ ] Rating screen shows 4 default categories
- [ ] Tapping emoji saves rating immediately
- [ ] Ratings persist across app restart
- [ ] Navigation bar visible (History/Settings greyed or placeholder OK)

---

## Notes

- All paths are relative to repository root
- [P] = parallelizable with other [P] tasks in same phase
- [US#] = belongs to that user story (for traceability)
- Commit after each task or logical group
- Run `./gradlew test` after each phase
- Constitution compliance: No Java, no XML layouts, Room for storage, Hilt for DI
