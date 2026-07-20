#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$ROOT_DIR/backend"
mvn -q -DskipTests package

cd "$ROOT_DIR/android"
./gradlew -q :app:assembleDebug

echo "Build completed successfully."
