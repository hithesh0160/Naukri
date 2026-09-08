#!/data/data/com.termux/files/usr/bin/bash
# Naukri Resume Upload - Termux Android Script
# Run this script daily using termux-job-scheduler or cron
# Only executes between 6:30 AM and 8:00 AM

echo "========================================"
echo "Naukri Resume Upload Automation (Termux)"
echo "========================================"
echo ""

# Change to script directory
cd "$(dirname "$0")" || exit 1

# Check if already run today
DATE_FILE=".last_run_date"
TODAY=$(date +%Y-%m-%d)

if [ -f "$DATE_FILE" ]; then
    LAST_RUN=$(cat "$DATE_FILE")
    if [ "$LAST_RUN" = "$TODAY" ]; then
        echo "Already run today ($TODAY). Skipping execution."
        echo "Will run tomorrow between 6:30 AM and 8:00 AM."
        exit 0
    fi
fi

# Check current time - only run between 6:30 AM and 8:00 AM
CURRENT_HOUR=$(date +%H)
CURRENT_MINUTE=$(date +%M)
CURRENT_TIME_MINUTES=$((CURRENT_HOUR * 60 + CURRENT_MINUTE))
START_TIME_MINUTES=$((6 * 60 + 30))  # 6:30 AM = 390 minutes
END_TIME_MINUTES=$((8 * 60))          # 8:00 AM = 480 minutes

if [ $CURRENT_TIME_MINUTES -lt $START_TIME_MINUTES ] || [ $CURRENT_TIME_MINUTES -gt $END_TIME_MINUTES ]; then
    echo "Current time is $(date +%H:%M). Not in execution window (6:30 AM - 8:00 AM)."
    echo "Skipping execution. Will check again at next scheduled wake."
    exit 0
fi

echo "Time check passed: $(date +%H:%M) is within 6:30 AM - 8:00 AM window."
echo ""

# Start VNC server if not running
if ! pgrep -x "Xvnc" > /dev/null; then
    echo "Starting VNC server..."
    vncserver -localhost :1
    sleep 2
fi

# Set display for VNC
export DISPLAY=:1

# Set headless mode to false for Android (Firefox headless crashes on Android)
export HEADLESS=false

# Set browser to firefox (more stable on Termux than chromium)
export BROWSER=firefox

# Set geckodriver path for Termux
export GECKO_DRIVER_PATH=$PREFIX/bin/geckodriver

# Verify Java is available
echo "Checking Java installation..."
java -version
if [ $? -ne 0 ]; then
    echo "ERROR: Java not found"
    echo "Install with: pkg install openjdk-17"
    exit 1
fi

# Verify Maven is available
echo "Checking Maven installation..."
mvn -version
if [ $? -ne 0 ]; then
    echo "ERROR: Maven not found"
    echo "Install with: pkg install maven"
    exit 1
fi

echo ""
echo "Running tests in headless mode..."

# Run Maven test with 10-minute timeout
timeout 600 mvn clean test || {
    echo "ERROR: Script timed out after 10 minutes"
    echo "Killing all processes..."
    pkill -9 java
    pkill -9 firefox
    pkill -9 geckodriver
    exit 1
}

echo ""
echo "========================================"
echo "Execution completed"
echo "Check logs folder for details"
echo "========================================"

# Record today's date to prevent multiple runs
echo "$TODAY" > "$DATE_FILE"
echo "Recorded execution date: $TODAY"
