@echo off
setlocal

cd /d "%~dp0"

call build.bat
if errorlevel 1 exit /b 1

java -cp "out\main;out\test" com.taskmanager.testframework.TestRunner

exit /b %ERRORLEVEL%