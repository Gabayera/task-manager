@echo off
rem Assignment 6: runs the stand-alone demo of the reminder feature (FR-14).
setlocal

cd /d "%~dp0"

call build.bat
if errorlevel 1 exit /b 1

java -cp "out\main" com.taskmanager.demo.ReminderDemo

exit /b %ERRORLEVEL%
