@echo off
REM Naukri Resume Upload - Local Automation Script
REM Run this script daily using Windows Task Scheduler

echo ========================================
echo Naukri Resume Upload Automation
echo ========================================
echo.

REM Change to script directory
cd /d "%~dp0"

REM Run Maven test
echo Running tests...
call mvn clean test

echo.
echo ========================================
echo Execution completed
echo Check logs folder for details
echo ========================================

REM Keep window open if run manually
if "%1"=="" pause
