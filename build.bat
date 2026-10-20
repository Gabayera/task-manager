@echo off
setlocal

cd /d "%~dp0"

if not exist out\main mkdir out\main
if not exist out\test mkdir out\test

dir /s /b src\main\java\*.java > out\main-sources.txt
if errorlevel 1 exit /b 1

dir /s /b src\test\java\*.java > out\test-sources.txt
if errorlevel 1 exit /b 1

javac -encoding UTF-8 -d out\main @out\main-sources.txt
if errorlevel 1 exit /b 1

javac -encoding UTF-8 -cp out\main -d out\test @out\test-sources.txt
if errorlevel 1 exit /b 1

exit /b 0