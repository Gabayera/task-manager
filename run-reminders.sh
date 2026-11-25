#!/usr/bin/env sh
# Assignment 6: runs the stand-alone demo of the reminder feature (FR-14).

set -eu

cd "$(dirname "$0")"

./build.sh

java -cp out/main com.taskmanager.demo.ReminderDemo
