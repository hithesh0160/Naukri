@echo off
REM Naukri Resume Upload - Local Automation Script
REM Run this script daily using Windows Task Scheduler

echo ========================================
echo Naukri Resume Upload Automation
echo ========================================
echo.

REM Change to script directory
cd /d "%~dp0"

REM Set Maven path
set MAVEN_HOME=D:\hites\Downloads\apache-maven-3.9.12-bin\apache-maven-3.9.12
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Set headless mode (set to "false" to see browser, "true" for headless)
set HEADLESS=true

REM Verify Maven is available
echo Checking Maven installation...
call mvn -version
if errorlevel 1 (
    echo ERROR: Maven not found at %MAVEN_HOME%
    echo Please update MAVEN_HOME in this script
    pause
    exit /b 1
)

echo.
echo Running tests with visible browser...
REM Run Maven test
call mvn clean test

echo.
echo ========================================
echo Execution completed
echo Check logs folder for details
echo ========================================

REM Keep window open if run manually
if "%1"=="" pause
