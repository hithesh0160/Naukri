#!/data/data/com.termux/files/usr/bin/bash
# Setup cron for Naukri automation in Termux

echo "Setting up cron job for Naukri automation..."

# Install cronie if not already installed
pkg install cronie -y

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCRIPT_PATH="$SCRIPT_DIR/run-naukri-termux.sh"

# Make script executable
chmod +x "$SCRIPT_PATH"

# Create cron entry (daily at 6:30 AM)
CRON_ENTRY="30 6 * * * cd $SCRIPT_DIR && $SCRIPT_PATH >> $SCRIPT_DIR/cron.log 2>&1"

# Add to crontab
(crontab -l 2>/dev/null | grep -v "run-naukri-termux.sh"; echo "$CRON_ENTRY") | crontab -

echo "✓ Cron job added successfully!"
echo "  Schedule: Daily at 6:30 AM"
echo "  Script: $SCRIPT_PATH"
echo "  Log: $SCRIPT_DIR/cron.log"
echo ""

# Start cron daemon
crond

echo "✓ Cron daemon started"
echo ""
echo "To view cron jobs:"
echo "  crontab -l"
echo ""
echo "To remove this cron job:"
echo "  crontab -e  (then delete the line with run-naukri-termux.sh)"
echo ""
echo "Note: Keep Termux running in background and disable battery optimization"
