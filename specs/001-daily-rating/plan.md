# Implementation Plan: Daily Rating Core

**Branch**: `001-daily-rating` | **Date**: 2026-01-16 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-daily-rating/spec.md`

## Summary

Build the core daily rating functionality for DayRater—a family-focused Android app for tracking daily vibes. Users rate their day across configurable categories (overall, spouse interactions, child interactions, physical activity, emotional state, self-care, custom) using a 3-emoji scale (😢/😐/😊). Data persists locally with Room/SQLite, supports history viewing, and exports via Android share intents. Built with Kotlin and Jetpack Compose following Material 3 design.

## Technical Context

**Language/Version**: Kotlin 2.x (latest stable, likely 2.1.x)  
**Primary Dependencies**: Jetpack Compose, Room, Hilt, Compose Navigation, Material 3  
**Storage**: Room (SQLite) — local-only, no cloud  
**Testing**: JUnit 5 + Kotlin Test for unit tests, Compose UI testing for critical flows  
**Target Platform**: Android API 26+ (Android 8.0 Oreo, covers ~95% of devices)  
**Project Type**: Mobile (single Android app, no backend)  
**Performance Goals**: App launch <2s, rating screen interactive <500ms, smooth 60fps scrolling  
**Constraints**: Offline-capable (100%), <50MB APK, <100MB runtime memory  
**Scale/Scope**: Single user per device, ~5-15 rating categories, 1+ year of daily history

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Status | Notes |
|-----------|-------------|--------|-------|
| I. Modern Android First | Kotlin only, Jetpack Compose, Material 3 | ✅ PASS | Plan uses Kotlin 2.x, Compose, M3 |
| I. Modern Android First | No Java, no XML layouts | ✅ PASS | All UI in Compose |
| I. Modern Android First | Follow official architecture guidelines | ✅ PASS | ViewModel + Repository pattern |
| II. Local-First | Room/SQLite storage | ✅ PASS | Room is primary storage |
| II. Local-First | Fully offline, no mandatory cloud | ✅ PASS | No network features |
| II. Local-First | Export via share intents | ✅ PASS | Spec requires FR-020/021 |
| III. Pragmatic Quality | Unit tests for business logic | ✅ PASS | Rating calculations, data transforms |
| III. Pragmatic Quality | Keep it simple | ✅ PASS | Standard MVVM, no over-engineering |
| III. Pragmatic Quality | Accessibility built-in | ✅ PASS | Dynamic text, themes per FR-023/024 |

**Gate Status**: ✅ PASSED — No violations. Proceed to Phase 0.

## Project Structure

### Documentation (this feature)

```text
specs/001-daily-rating/
├── plan.md              # This file
├── spec.md              # Feature specification
├── research.md          # Phase 0: Technical decisions
├── data-model.md        # Phase 1: Database schema
├── quickstart.md        # Phase 1: Setup guide
├── contracts/           # Phase 1: Internal APIs
│   ├── repositories.md  # Repository interfaces
│   └── navigation.md    # Screen routes
└── tasks.md             # Phase 2 output (via /speckit.tasks)
```

### Source Code (repository root)

```text
app/
├── src/main/
│   ├── java/com/dayrater/
│   │   ├── MainActivity.kt
│   │   ├── DayRaterApplication.kt
│   │   ├── di/
│   │   │   └── DatabaseModule.kt
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── DayRaterDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   │   ├── RatingDao.kt
│   │   │   │   │   ├── CategoryDao.kt
│   │   │   │   │   └── FamilyMemberDao.kt
│   │   │   │   └── entity/
│   │   │   │       ├── DailyRatingEntity.kt
│   │   │   │       ├── CategoryEntity.kt
│   │   │   │       └── FamilyMemberEntity.kt
│   │   │   └── repository/
│   │   │       ├── RatingRepository.kt
│   │   │       ├── FamilyRepository.kt
│   │   │       └── SettingsRepository.kt
│   │   ├── domain/
│   │   │   └── model/
│   │   │       ├── Rating.kt
│   │   │       ├── Category.kt
│   │   │       └── FamilyMember.kt
│   │   └── ui/
│   │       ├── theme/
│   │       │   ├── Theme.kt
│   │       │   ├── Color.kt
│   │       │   └── Type.kt
│   │       ├── navigation/
│   │       │   ├── Screen.kt
│   │       │   └── NavGraph.kt
│   │       ├── rating/
│   │       │   ├── RatingScreen.kt
│   │       │   └── RatingViewModel.kt
│   │       ├── history/
│   │       │   ├── HistoryScreen.kt
│   │       │   ├── HistoryViewModel.kt
│   │       │   ├── DayDetailScreen.kt
│   │       │   └── DayDetailViewModel.kt
│   │       ├── settings/
│   │       │   ├── SettingsScreen.kt
│   │       │   ├── SettingsViewModel.kt
│   │       │   ├── FamilySetupScreen.kt
│   │       │   ├── FamilySetupViewModel.kt
│   │       │   ├── CustomCategoriesScreen.kt
│   │       │   └── CustomCategoriesViewModel.kt
│   │       ├── export/
│   │       │   ├── ExportScreen.kt
│   │       │   └── ExportViewModel.kt
│   │       └── components/
│   │           ├── EmojiRatingSelector.kt
│   │           ├── CategoryCard.kt
│   │           └── BottomNavBar.kt
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   ├── colors.xml
│   │   │   └── themes.xml
│   │   └── values-night/
│   │       └── themes.xml
│   └── AndroidManifest.xml
└── src/test/
    └── java/com/dayrater/
        ├── data/repository/
        │   ├── RatingRepositoryTest.kt
        │   └── FamilyRepositoryTest.kt
        └── domain/
            └── RatingCalculationsTest.kt

.devcontainer/
├── devcontainer.json
└── setup-android-sdk.sh

.github/
├── workflows/
│   ├── build.yml
│   └── release.yml
└── agents/
    └── copilot-instructions.md

gradle/
└── libs.versions.toml

build.gradle.kts
settings.gradle.kts
```

**Structure Decision**: Single Android app module with feature-based package organization. Data/Domain/UI layers within the app module. Multi-module architecture deferred until build times warrant it.

## Complexity Tracking

> No constitution violations. This section is empty.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| — | — | — |
