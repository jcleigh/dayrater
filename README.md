# DayRater 📊

A simple Android app to track your daily vibes with emoji-based ratings.

## Features

- 🎯 **Rate Your Day** - Track how your day went across multiple categories using 3 emoji levels (😄 Great → 😢 Bad)
- 👨‍👩‍👧‍👦 **Family Tracking** - Add family members and track everyone's daily vibes
- 📅 **Rating History** - Browse past ratings with a calendar view
- ✏️ **Custom Categories** - Create your own categories beyond the defaults (Sleep, Diet, Exercise, Family Time)
- 📤 **Data Export** - Export your data to CSV or JSON for analysis in spreadsheets
- 🎨 **Theme Support** - Light, dark, or system-default themes with Material 3 Dynamic Color

## Tech Stack

- **Language**: Kotlin 2.1.0
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room for local SQLite storage
- **DI**: Hilt for dependency injection
- **Navigation**: Compose Navigation with type-safe routes

## Requirements

- Android Studio Ladybug (2024.2.1) or later
- JDK 17+
- Android SDK 35 (compileSdk)
- Minimum SDK: 26 (Android 8.0 Oreo)

## Building

### Using Android Studio

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Using Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Using Dev Container (Recommended)

This project includes a Dev Container configuration for consistent development environments:

1. Install [Docker](https://www.docker.com/get-started) and [VS Code](https://code.visualstudio.com/)
2. Install the [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)
3. Open the project folder
4. Click "Reopen in Container" when prompted
5. Wait for the container to build (first time only)

The Dev Container includes:
- Android SDK 35 with build-tools
- JDK 17
- Gradle
- All required dependencies

## Project Structure

```
app/src/main/java/com/dayrater/
├── data/
│   ├── local/           # Room database, entities, DAOs
│   └── repository/      # Repository implementations
├── di/                  # Hilt dependency injection modules
├── domain/
│   └── model/           # Domain models
└── ui/
    ├── components/      # Reusable UI components
    ├── export/          # Export screen
    ├── history/         # Calendar and day detail screens
    ├── navigation/      # Navigation graph and bottom nav
    ├── rating/          # Main rating screen
    ├── settings/        # Settings and family setup screens
    └── theme/           # Material 3 theme configuration
```

## Architecture

The app follows **Clean Architecture** principles:

1. **UI Layer** - Compose screens with ViewModels using `StateFlow` for state management
2. **Domain Layer** - Domain models representing core business entities
3. **Data Layer** - Room database with repositories abstracting data access

```
┌─────────────────────────────────────┐
│           UI Layer                  │
│   (Screens, ViewModels, UiState)    │
├─────────────────────────────────────┤
│         Domain Layer                │
│   (Models, Repository Interfaces)   │
├─────────────────────────────────────┤
│          Data Layer                 │
│   (Room DB, DAOs, Repository Impl)  │
└─────────────────────────────────────┘
```

## Default Categories

The app comes pre-configured with these rating categories:

| Category | Description |
|----------|-------------|
| 😴 Sleep | How well did you sleep? |
| 🥗 Diet | How healthy did you eat? |
| 🏃 Exercise | How active were you? |
| 👨‍👩‍👧 Family Time | Quality time with family? |

Users can add custom categories in Settings.

## Rating Scale

| Emoji | Label | Meaning |
|-------|-------|---------|
| 😄 | Great | Excellent day |
| 🙂 | Good | Above average |
| 😐 | Okay | Average |
| 😕 | Not Great | Below average |
| 😢 | Bad | Rough day |

## CI/CD

The project includes GitHub Actions workflows:

- **Build** (`build.yml`) - Runs on every push/PR, builds debug APK and runs tests
- **Release** (`release.yml`) - Triggered on version tags (v*.*.*), builds signed release APK

## License

[MIT License](LICENSE)

## Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests (`./gradlew test`)
5. Submit a pull request
