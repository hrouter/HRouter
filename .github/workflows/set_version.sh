#!/usr/bin/env bash

set -e

if [ -z "$GITHUB_RUN_NUMBER" ] ;then
    GITHUB_RUN_NUMBER=0
fi


VERSION_NAME="v$(date +'%Y.%m.%d').$GITHUB_RUN_NUMBER"

echo "生成的版本号:$VERSION_NAME"

if [ -n "${GITHUB_ENV:-}" ] ;then
  echo "VERSION_NAME=$VERSION_NAME" >> $GITHUB_ENV
fi

if [ -n "${GITHUB_OUTPUT:-}" ] ; then
  echo "version=$VERSION_NAME" >> $GITHUB_OUTPUT
fi
