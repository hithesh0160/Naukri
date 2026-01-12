#!/bin/bash
# Naukri Resume Upload - Local Automation Script
# Run this script daily using cron

echo "========================================"
echo "Naukri Resume Upload Automation"
echo "========================================"
echo ""

# Change to script directory
cd "$(dirname "$0")"

# Run Maven test
echo "Running tests..."
mvn clean test

echo ""
echo "========================================"
echo "Execution completed"
echo "Check logs folder for details"
echo "========================================"
