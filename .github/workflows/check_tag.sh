#!/usr/bin/env bash

set -e

#获取最新tag
LAST_TAG="$( git describe --tag --abbrev=0 2>/dev/null || echo "v0.0.1")"

echo "最新tag:$LAST_TAG"

BUILD_ID="$( git rev-list head --count )"

echo "当前Build标识:$BUILD_ID"

DATE="$(date +'%Y.%m.%d %H:%M:%S')"

APK_NAME="${LAST_TAG}_${BUILD_ID}_$DATE.apk"

echo "当前Apk Name:$APK_NAME"