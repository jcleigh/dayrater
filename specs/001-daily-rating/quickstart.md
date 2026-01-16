# Quickstart: Daily Rating Core

**Feature**: 001-daily-rating  
**Date**: 2026-01-16

Get the DayRater project running locally in under 10 minutes.

---

## Prerequisites

**Option A: Dev Container (Recommended)**
- Docker Desktop installed
- VS Code with Dev Containers extension
- That's it! No Java/Android SDK needed locally.

**Option B: Local Development**
- JDK 21 (Temurin recommended)
- Android Studio Ladybug (2024.2.x) or later
- Android SDK 35 with Build Tools 35.0.0

---

## Quick Start (Dev Container)

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/dayrater.git
cd dayrater

# 2. Open in VS Code
code .

# 3. When prompted "Reopen in Container", click Yes
#    Or: Cmd+Shift+P → "Dev Containers: Reopen in Container"

# 4. Wait for container to build (~2-5 min first time)

# 5. Build the app
./gradlew assembleDebug

# 6. Run tests
./gradlew test
```

---

## Quick Start (Local)

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/dayrater.git
cd dayrater

# 2. Ensure ANDROID_HOME is set
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
# export ANDROID_HOME=$HOME/Android/Sdk        # Linux

# 3. Build the app
./gradlew assembleDebug

# 4. Run tests
./gradlew test

# 5. Install on connected device/emulator
./gradlew installDebug
```

---

## Project Structure

```
dayrater/
├── app/
│   ├── src/main/
│   │   ├── java/com/dayrater/
│   │   │   ├── MainActivity.kt
│   │   │   ├── DayRaterApplication.kt
│   │   │   ├── di/                    # Hilt modules
│   │   │   ├── data/                  # Room entities, DAOs, repos
│   │   │   ├── domain/                # Domain models
│   │   │   └── ui/                    # Compose screens
│   │   ├── res/                       # Resources (themes, strings)
│   │   └── AndroidManifest.xml
│   └── src/test/                      # Unit tests
├── .devcontainer/                     # Dev Container config
├── .github/workflows/                 # CI/CD
├── specs/                             # Feature specifications
├── gradle/
│   └── libs.versions.toml             # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Common Tasks

### Build Debug APK
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Build Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Run All Tests
```bash
./gradlew test
```

### Run Lint
```bash
./gradlew lint
# Report: app/build/reports/lint-results-debug.html
```

### Install on Device
```bash
# List connected devices
adb devices

# Install debug build
./gradlew installDebug

# Or install specific APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Clean Build
```bash
./gradlew clean
```

---

## Running on Physical Device

### Enable USB Debugging
1. On your Android device, go to **Settings → About phone**
2. Tap **Build number** 7 times to enable Developer options
3. Go to **Settings → Developer options**
4. Enable **USB debugging**
5. Connect device via USB and accept the prompt

### ADB over WiFi (for containers)
```bash
# On device connected via USB
adb tcpip 5555

# Note device IP from Settings → About → IP address
adb connect 192.168.1.XXX:5555

# Now you can disconnect USB
```

---

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `ANDROID_HOME` | Android SDK location | Auto-detected in container |
| `JAVA_HOME` | JDK location | Auto-detected in container |

---

## Troubleshooting

### "SDK location not found"
```bash
# Create local.properties with SDK path
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

### "Gradle daemon out of memory"
```bash
# Add to gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:+HeapDumpOnOutOfMemoryError
```

### "Device not found" in container
```bash
# Use ADB over network (see above)
# Or run install commands on host machine
```

### Lint errors blocking build
```bash
# Run with continue to see all errors
./gradlew lint --continue

# Or temporarily disable lint failures in build.gradle.kts
# lintOptions { abortOnError = false }
```

---

## CI/CD

Builds run automatically on GitHub Actions:

- **Push to main**: Build + Test
- **Pull Request**: Build + Test + Lint
- **Tag v*.*.* **: Build + Release APK to GitHub Releases

Download release APKs from: `https://github.com/YOUR_USERNAME/dayrater/releases`

---

## Next Steps

After setup, see:
- [spec.md](spec.md) — Feature requirements
- [data-model.md](data-model.md) — Database schema
- [contracts/](contracts/) — Repository and navigation contracts
- [tasks.md](tasks.md) — Implementation task list (created via `/speckit.tasks`)
