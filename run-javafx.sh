#!/bin/bash

# Script to run JavaFX application with proper module path
# This bypasses VS Code/Cursor Java extension issues with JavaFX

# Set JavaFX module path
JAVAFX_PATH="/Users/jacobchan/.m2/repository/org/openjfx"
JAVAFX_VERSION="21.0.4"
PLATFORM="mac-aarch64"

# Build the module path (include all required JavaFX modules)
MODULE_PATH="${JAVAFX_PATH}/javafx-base/${JAVAFX_VERSION}/javafx-base-${JAVAFX_VERSION}-${PLATFORM}.jar:${JAVAFX_PATH}/javafx-graphics/${JAVAFX_VERSION}/javafx-graphics-${JAVAFX_VERSION}-${PLATFORM}.jar:${JAVAFX_PATH}/javafx-controls/${JAVAFX_VERSION}/javafx-controls-${JAVAFX_VERSION}-${PLATFORM}.jar:${JAVAFX_PATH}/javafx-fxml/${JAVAFX_VERSION}/javafx-fxml-${JAVAFX_VERSION}-${PLATFORM}.jar"

# Set classpath to include compiled classes and dependencies
CLASSPATH="app-fx/target/classes:core/target/classes"

# Add Maven dependencies to classpath
for jar in ~/.m2/repository/org/json/json/20240303/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

for jar in ~/.m2/repository/com/squareup/okhttp3/okhttp/4.12.0/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

for jar in ~/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/1.9.24/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

for jar in ~/.m2/repository/com/squareup/okio/okio/3.9.1/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

for jar in ~/.m2/repository/com/squareup/okio/okio-jvm/3.9.1/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

for jar in ~/.m2/repository/org/xerial/sqlite-jdbc/3.44.1.0/*.jar; do
    CLASSPATH="${CLASSPATH}:${jar}"
done

# Find Java executable (prioritize Java 21+, use Homebrew Java 25 that Maven uses)
# Try to find Homebrew Java 25 first (what Maven uses)
HOMEBREW_JAVA=""
if [ -d "/opt/homebrew/Cellar/openjdk" ]; then
    for java_dir in /opt/homebrew/Cellar/openjdk/*/libexec/openjdk.jdk/Contents/Home; do
        if [ -d "$java_dir" ]; then
            HOMEBREW_JAVA="$java_dir"
            break
        fi
    done
fi

# Try to find Java 21+ in order of preference
JAVA_HOME=""
if [ -n "$HOMEBREW_JAVA" ] && [ -x "$HOMEBREW_JAVA/bin/java" ]; then
    JAVA_HOME="$HOMEBREW_JAVA"
    echo "Using Homebrew Java: $JAVA_HOME"
else
    # Try java_home for specific versions
    for version in 25 24 23 22 21; do
        TEST_JAVA=$(/usr/libexec/java_home -v $version 2>/dev/null)
        if [ -n "$TEST_JAVA" ] && [ -x "$TEST_JAVA/bin/java" ]; then
            TEST_VERSION=$("$TEST_JAVA/bin/java" -version 2>&1 | head -1 | grep -oE 'version "[0-9]+' | grep -oE '[0-9]+')
            if [ -n "$TEST_VERSION" ] && [ "$TEST_VERSION" -ge 21 ]; then
                JAVA_HOME="$TEST_JAVA"
                echo "Using Java $version: $JAVA_HOME"
                break
            fi
        fi
    done
    
    # Fallback to default
    if [ -z "$JAVA_HOME" ]; then
        JAVA_HOME=$(/usr/libexec/java_home)
    fi
fi

JAVA="${JAVA_HOME}/bin/java"

# Verify Java version (must be 21+)
JAVA_VERSION=$("${JAVA}" -version 2>&1 | head -1 | grep -oE 'version "[0-9]+' | grep -oE '[0-9]+')
if [ -z "$JAVA_VERSION" ] || [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Error: Java 21 or higher is required. Found Java ${JAVA_VERSION}."
    echo "Please install Java 21 or set JAVA_HOME to point to Java 21+"
    exit 1
fi

# Run the application
cd "$(dirname "$0")"
"${JAVA}" \
    --module-path "${MODULE_PATH}" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics \
    -cp "${CLASSPATH}" \
    com.smartcalendar.fx.App


