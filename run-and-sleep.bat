@echo off
REM Naukri Resume Upload - Auto Sleep After Execution
REM This script runs the automation and puts PC to sleep

echo ========================================
echo Naukri Resume Upload with Auto Sleep
echo ========================================
echo.

REM Change to script directory
cd /d "%~dp0"

REM Set Maven path
set MAVEN_HOME=D:\hites\Downloads\apache-maven-3.9.12-bin\apache-maven-3.9.12
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Set headless mode (true for no browser window)
set HEADLESS=true

REM Use Chrome (better anti-detection, auto-downloads driver)
set BROWSER=chrome

REM Set Telegram credentials (optional - leave empty to disable notifications)
REM Get your bot token from @BotFather on Telegram
REM Get your chat ID from @userinfobot on Telegram
set TELEGRAM_TOKEN=8086512864:AAFaV0N-BlaWWIksESX-eN4ceUqmFfXtB7U
set TELEGRAM_CHAT_ID=504069398

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
echo Running tests in headless mode...
REM Run Maven test
call mvn clean test

echo.
echo ========================================
echo Execution completed
echo Putting PC to sleep in 10 seconds...
echo ========================================
echo.

REM Wait 10 seconds before sleep
timeout /t 10 /nobreak

REM Sleep mode (preserves session, can wake via Task Scheduler)
echo Going to sleep now...
rundll32.exe powrprof.dll,SetSuspendState 0,1,0
