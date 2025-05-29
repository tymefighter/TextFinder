#!/bin/bash

set -e

SRC_DIR="src"
BIN_DIR="bin"
JAR_NAME="textFinder.jar"
MANIFEST_FILE="manifest.txt"  # Your pre-created manifest

mkdir -p "$BIN_DIR"

echo "Compiling Java files..."
find "$SRC_DIR" -name "*.java" > sources.txt
javac -g -d "$BIN_DIR" @sources.txt
rm sources.txt

echo "Packaging into JAR using your manifest..."
jar cfm "$JAR_NAME" "$MANIFEST_FILE" -C "$BIN_DIR" .

echo "Build complete. JAR file is $JAR_NAME"
