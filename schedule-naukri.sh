#!/data/data/com.termux/files/usr/bin/bash
# Setup scheduled job for Naukri automation using termux-job-scheduler

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCRIPT_PATH="$SCRIPT_DIR/run-naukri-termux.sh"

# Make script executable
chmod +x "$SCRIPT_PATH"

# Schedule job to run daily at 6:30 AM
# Note: termux-job-scheduler uses job IDs
# Job ID 1001 = Naukri automation
# Period: 86400000 ms = 24 hours

echo "Setting up scheduled job for Naukri automation..."
echo "Job will run daily at approximately 6:30 AM"
echo ""

# Remove existing job if any
termux-job-scheduler --cancel --job-id 1001

# Schedule new job
# --job-id: unique identifier
# --script: path to script to run
# --period-ms: run every 24 hours (86400000 milliseconds)
# --persisted: survive device reboots
# --network: require network connectivity

termux-job-scheduler \
  --job-id 1001 \
  --script "$SCRIPT_PATH" \
  --period-ms 86400000 \
  --persisted true \
  --network any

if [ $? -eq 0 ]; then
    echo "✓ Scheduled job created successfully!"
    echo "  Job ID: 1001"
    echo "  Script: $SCRIPT_PATH"
    echo "  Frequency: Every 24 hours"
    echo ""
    echo "To view scheduled jobs:"
    echo "  termux-job-scheduler --list"
    echo ""
    echo "To cancel this job:"
    echo "  termux-job-scheduler --cancel --job-id 1001"
else
    echo "✗ Failed to create scheduled job"
    echo "Make sure Termux:API is installed from F-Droid"
fi
