#!/bin/bash
set -e

# Android SDK Setup Script for Dev Containers
# This script installs Android SDK components needed to build DayRater

ANDROID_SDK_ROOT="${ANDROID_HOME:-$PWD/.android-sdk}"
CMDLINE_TOOLS_VERSION="11076708"  # Latest as of 2024

echo "=== Setting up Android SDK at $ANDROID_SDK_ROOT ==="

# Create SDK directory
mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"

# Download command-line tools if not present
if [ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ]; then
    echo "Downloading Android command-line tools..."
    cd /tmp
    wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip" -O cmdline-tools.zip
    unzip -q cmdline-tools.zip
    mv cmdline-tools "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    rm cmdline-tools.zip
    echo "Command-line tools installed."
fi

# Accept licenses
echo "Accepting Android SDK licenses..."
yes | "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" --licenses > /dev/null 2>&1 || true

# Install required SDK components
echo "Installing SDK components..."
"$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    --sdk_root="$ANDROID_SDK_ROOT"

# Create local.properties for Gradle
echo "Creating local.properties..."
echo "sdk.dir=$ANDROID_SDK_ROOT" > "$OLDPWD/local.properties"

echo ""
echo "=== Android SDK setup complete! ==="
echo "SDK location: $ANDROID_SDK_ROOT"
echo ""
echo "To build the app, run:"
echo "  ./gradlew assembleDebug"
echo ""
echo "To connect a physical device over ADB TCP:"
echo "  adb tcpip 5555"
echo "  adb connect <device-ip>:5555"
