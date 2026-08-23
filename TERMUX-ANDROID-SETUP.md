# Naukri Automation - Termux Android Setup Guide

This guide explains how to run the Naukri resume upload automation on Android using Termux with scheduled execution.

## ⚠️ Important Limitations

1. **Battery drain**: Running scheduled browser automation will consume battery
2. **Background restrictions**: Android may kill Termux in the background
3. **IP consistency**: Mobile IPs change frequently, which may trigger OTP
4. **Resource intensive**: Selenium + Chrome is heavy for mobile devices
5. **Reliability**: Less reliable than a dedicated PC/server setup

**Recommendation**: Use this as a backup solution. A Windows PC with Task Scheduler is more reliable.

## Prerequisites

1. **Android device** running Android 7.0+ (64-bit recommended)
2. **Termux app** from F-Droid (NOT Google Play version)
   - Download: https://f-droid.org/en/packages/com.termux/
3. **Termux:API** from F-Droid (for scheduling)
   - Download: https://f-droid.org/en/packages/com.termux.api/
4. **VNC Viewer** (optional, for debugging)
   - Download from Google Play Store
5. **At least 2GB free storage**
6. **Stable WiFi connection** (to maintain consistent IP)

## Step-by-Step Setup

### 1. Install Termux and Termux:API

```bash
# Open Termux and update packages
pkg update && pkg upgrade

# If prompted, press Y to continue
```

### 2. Install Required Software

```bash
# Install Java 17 (required for Selenium)
pkg install openjdk-17

# Install Maven (build tool)
pkg install maven

# Install Git (to clone repository)
pkg install git

# Install Termux:API (for scheduling)
pkg install termux-api

# Install Chromium browser
pkg install x11-repo
pkg install chromium

# Install VNC server (for display)
pkg install tigervnc

# Install cronie (alternative scheduling)
pkg install cronie
```

### 3. Setup VNC Server (Required for Browser)

Selenium needs a display to run Chrome:

```bash
# Start VNC server
vncserver -localhost

# Password will be prompted - set a secure password
# View-only password: n

# Set display environment variable
echo 'export DISPLAY=:1' >> ~/.bashrc
source ~/.bashrc
```

### 4. Clone Your Repository

```bash
# Navigate to home directory
cd ~

# Clone the repository
git clone https://github.com/YOUR_USERNAME/Naukri.git

# Navigate to project directory
cd Naukri
```

### 5. Configure Credentials

```bash
# Create config file
nano src/com/naukri/config/config.properties
```

Add your credentials:
```properties
username=your_email@example.com
password=your_password
telegram.token=your_telegram_bot_token
telegram.chatid=your_telegram_chat_id
```

Save: `Ctrl+O`, Enter, then `Ctrl+X`

### 6. Test Manual Run

```bash
# Make scripts executable
chmod +x run-naukri-termux.sh

# Test run
./run-naukri-termux.sh
```

Watch the output for errors. If successful, you'll see:
- Java and Maven version checks
- Maven downloading dependencies
- Tests running
- Screenshots saved
- Telegram notification sent

### 7. Setup Scheduled Execution

You have two options:

#### Option A: Using termux-job-scheduler (Recommended)

```bash
# Run the scheduler setup script
chmod +x schedule-naukri.sh
./schedule-naukri.sh
```

This creates a job that runs every 24 hours.

**Verify scheduled job:**
```bash
termux-job-scheduler --list
```

**Cancel scheduled job:**
```bash
termux-job-scheduler --cancel --job-id 1001
```

#### Option B: Using Cron

```bash
# Run the cron setup script
chmod +x setup-cron-termux.sh
./setup-cron-termux.sh
```

This creates a cron job that runs daily at 6:30 AM.

**Verify cron job:**
```bash
crontab -l
```

**Start cron daemon on boot:**
```bash
# Add to .bashrc
echo 'crond' >> ~/.bashrc
```

### 8. Configure Android Battery Optimization

**Critical**: Prevent Android from killing Termux:

1. Go to **Settings** → **Apps** → **Termux**
2. Select **Battery** or **Power management**
3. Disable **Battery optimization** or set to "Don't optimize"
4. Enable **Background activity**
5. Disable **Adaptive battery** for Termux
6. In **Developer options** (if enabled), disable **Standby apps**

### 9. Keep Termux Alive

#### Method 1: Termux:Boot (Install from F-Droid)

1. Install **Termux:Boot** from F-Droid
2. Create startup script:

```bash
mkdir -p ~/.termux/boot
nano ~/.termux/boot/start-services.sh
```

Add:
```bash
#!/data/data/com.termux/files/usr/bin/bash
# Start VNC
vncserver -localhost
export DISPLAY=:1

# Start cron
crond
```

3. Make executable:
```bash
chmod +x ~/.termux/boot/start-services.sh
```

#### Method 2: Termux:Widget (Manual trigger)

1. Install **Termux:Widget** from F-Droid
2. Create widget script:

```bash
mkdir -p ~/.shortcuts
cp run-naukri-termux.sh ~/.shortcuts/Naukri-Upload.sh
```

3. Add widget to home screen
4. Tap widget to run manually

### 10. Monitoring and Logs

```bash
# View logs
cd ~/Naukri
cat logs/naukri-automation.log

# View cron logs (if using cron)
tail -f cron.log

# View screenshots
ls -lh *.png

# Check last run status
cat src/com/naukri/config/last_resume_uploaded.properties
```

## Troubleshooting

### Chrome/Chromium Issues

If Selenium can't find Chrome:

```bash
# Check if chromium is installed
which chromium-browser

# If not found, install again
pkg install chromium
```

### Out of Memory Errors

```bash
# Increase Java heap size
export MAVEN_OPTS="-Xmx512m"
./run-naukri-termux.sh
```

### VNC Connection Issues

```bash
# Stop all VNC servers
vncserver -kill :*

# Restart VNC
vncserver -localhost
export DISPLAY=:1
```

### Job Not Running Automatically

1. Check if Termux is being killed:
   - Disable battery optimization (see Step 8)
   - Keep Termux in recent apps
   
2. Check if scheduler is working:
   ```bash
   # For termux-job-scheduler
   termux-job-scheduler --list
   
   # For cron
   ps aux | grep cron
   ```

3. Check logs for errors:
   ```bash
   cat cron.log
   tail -f logs/naukri-automation.log
   ```

### OTP Issues

Mobile IPs change frequently, causing OTP prompts:

1. **Use WiFi** instead of mobile data for consistent IP
2. **Login manually** in Chrome on your phone first
3. Consider using a **VPN** with static IP
4. Or run this on a **PC/laptop** instead

## File Structure

```
~/Naukri/
├── run-naukri-termux.sh        # Main execution script for Termux
├── schedule-naukri.sh           # termux-job-scheduler setup
├── setup-cron-termux.sh         # Cron setup
├── cron.log                     # Cron execution logs
├── logs/                        # Application logs
├── data/                        # Resume PDF files
├── src/                         # Source code
└── *.png                        # Screenshots
```

## Best Practices

1. **Test thoroughly** before relying on scheduled runs
2. **Monitor battery** consumption in first few days
3. **Keep Termux updated**: `pkg update && pkg upgrade`
4. **Backup configuration** files regularly
5. **Check logs** weekly for failures
6. **Restart phone** weekly to prevent memory issues
7. **Keep phone plugged in** during scheduled run time if possible

## Alternative: Termux on Tablet/Old Phone

For better reliability:
- Use a dedicated old Android device
- Keep it plugged in permanently
- Disable all power-saving features
- Use WiFi with static IP
- Treat it like a mini-server

## Comparison: Termux vs Windows PC

| Feature | Termux (Android) | Windows PC |
|---------|------------------|------------|
| Setup complexity | High | Medium |
| Reliability | Medium | High |
| Battery impact | High | N/A |
| IP consistency | Low (mobile) | High |
| Resource usage | High for device | Low for PC |
| Maintenance | High | Low |
| OTP issues | Common | Rare |

**Verdict**: Use Termux only if you don't have access to a PC. Windows Task Scheduler is the recommended approach.

## Support

If you encounter issues:

1. Check Termux logs: `cat ~/Naukri/cron.log`
2. Check application logs: `cat ~/Naukri/logs/naukri-automation.log`
3. Review screenshots for errors
4. Test manual run first: `./run-naukri-termux.sh`
5. Create GitHub issue with error details

## Uninstall

```bash
# Stop scheduled jobs
termux-job-scheduler --cancel --job-id 1001
crontab -r

# Stop VNC
vncserver -kill :*

# Remove project
rm -rf ~/Naukri

# Remove packages (optional)
pkg uninstall openjdk-17 maven chromium tigervnc cronie
```

---

**Last Updated**: 2026-08-23  
**Version**: 1.0.0  
**Tested On**: Termux 0.118.0, Android 12+
