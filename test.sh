#!/usr/bin/env sh

set -eu

cd "$(dirname "$0")"

./build.sh

java -cp out/main:out/test \
    com.taskmanager.testframework.TestRunner