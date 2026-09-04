#!/data/data/com.termux/files/usr/bin/bash
# Naukri Resume Upload - Termux Android Script
# Run this script daily using termux-job-scheduler or cron

echo "========================================"
echo "Naukri Resume Upload Automation (Termux)"
echo "========================================"
echo ""

# Change to script directory
cd "$(dirname "$0")" || exit 1

# Set display for VNC (if not already set)
export DISPLAY=:1

# Set headless mode to true for Android (no visible browser)
export HEADLESS=true

# Set browser to chrome (chromium in Termux)
export BROWSER=chrome

# Set ChromeDriver path for Termux
export CHROME_DRIVER_PATH=$PREFIX/bin/chromedriver

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

# Run Maven test
mvn clean test

echo ""
echo "========================================"
echo "Execution completed"
echo "Check logs folder for details"
echo "========================================"
