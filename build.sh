#!/usr/bin/env sh

set -eu

cd "$(dirname "$0")"

mkdir -p out/main out/test

find src/main/java -type f -name '*.java' -print \
    > out/main-sources.txt

find src/test/java -type f -name '*.java' -print \
    > out/test-sources.txt

javac -encoding UTF-8 \
    -d out/main \
    @out/main-sources.txt

javac -encoding UTF-8 \
    -cp out/main \
    -d out/test \
    @out/test-sources.txt