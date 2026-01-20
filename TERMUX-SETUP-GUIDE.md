# Naukri Automation - Android/Termux Setup Guide

## Prerequisites

1. **Android phone** (Android 7.0+)
2. **Termux app** from F-Droid (NOT Play Store)
3. **Termux:Boot app** from F-Droid (for auto-start)
4. **Stable internet connection**
5. **At least 2GB free storage**

---

## Installation Steps

### Step 1: Install Termux

1. Open browser on your phone
2. Go to https://f-droid.org/
3. Download and install F-Droid
4. Open F-Droid
5. Search for "Termux"
6. Install **Termux** and **Termux:Boot**

### Step 2: Configure Termux Permissions

1. Open Android Settings
2. Go to Apps → Termux
3. Permissions:
   - ✅ Storage: Allow
   - ✅ Files and media: Allow
4. Battery:
   - ✅ Battery optimization: OFF
   - ✅ Background activity: Allowed

### Step 3: Transfer Setup Script to Phone

**Option A: Download directly**
```bash
# In Termux, run:
pkg install wget -y
wget https://raw.githubusercontent.com/hithesh0160/Naukri/main/setup-termux.sh
chmod +x setup-termux.sh
./setup-termux.sh
```

**Option B: Copy from PC**
1. Copy `setup-termux.sh` to your phone's Downloads folder
2. In Termux, run:
```bash
termux-setup-storage
cp ~/storage/downloads/setup-termux.sh ~/
chmod +x setup-termux.sh
./setup-termux.sh
```

### Step 4: Configure Credentials

After setup completes, edit your credentials:

```bash
nano ~/naukri-automation/Naukri/src/com/naukri/config/config.properties
```

Add:
```properties
username=your_email@example.com
password=your_password
```

Save: `Ctrl+X`, then `Y`, then `Enter`

### Step 5: Copy Resume Files

1. Copy your resume PDFs to phone's Downloads folder
2. In Termux:

```bash
cp ~/storage/downloads/Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf.pdf ~/naukri-automation/Naukri/data/
cp ~/storage/downloads/Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf.pdf ~/naukri-automation/Naukri/data/
```

### Step 6: Test the Automation

```bash
cd ~/naukri-automation/Naukri
./run-naukri-android.sh
```

**What to expect:**
- Script runs in headless mode (no browser window)
- Takes 30-60 seconds
- Check logs: `cat ~/naukri-automation/naukri.log`
- Check screenshots in project folder

### Step 7: Schedule Daily Runs

```bash
crontab -e
```

Add this line (runs at 6:30 AM daily):
```
30 6 * * * ~/naukri-automation/Naukri/run-naukri-android.sh
```

Save and exit.

### Step 8: Enable Auto-Start on Boot

1. Open Termux:Boot app (grants permission)
2. Reboot your phone
3. Termux will auto-start cron in background

---

## Usage

### Manual Run
```bash
cd ~/naukri-automation/Naukri
./run-naukri-android.sh
```

### Check Logs
```bash
cat ~/naukri-automation/naukri.log
tail -f ~/naukri-automation/naukri.log  # Live view
```

### Check Cron Status
```bash
crontab -l  # List scheduled jobs
ps aux | grep crond  # Check if cron is running
```

### Update Project
```bash
cd ~/naukri-automation/Naukri
git pull
```

---

## Troubleshooting

### Issue: "Maven not found"
```bash
pkg install maven -y
```

### Issue: "Java not found"
```bash
pkg install openjdk-17 -y
```

### Issue: "Firefox not found"
```bash
pkg install firefox geckodriver -y
```

### Issue: "Permission denied"
```bash
chmod +x run-naukri-android.sh
```

### Issue: "Cron not running"
```bash
crond
crontab -l
```

### Issue: "Termux killed by Android"
- Disable battery optimization for Termux
- Keep Termux running in background
- Use Termux:Boot for auto-start

### Issue: "Resume file not found"
Check file names exactly match:
```bash
ls -la ~/naukri-automation/Naukri/data/
```

---

## Battery Impact

**Estimated battery usage:**
- Idle: ~5-10 mAh/hour (Termux + cron)
- During execution: ~200-300 mAh (2-3 minutes)
- **Daily total: ~5-8% battery**

**Tips to minimize:**
- Run during charging time
- Use power saving mode after execution
- Keep phone plugged in at night

---

## Maintenance

### Weekly
- Check logs for errors
- Verify resume uploads on Naukri

### Monthly
- Update Termux packages: `pkg upgrade`
- Update project: `git pull`
- Check cron is running: `ps aux | grep crond`

---

## Uninstall

```bash
# Remove cron job
crontab -r

# Remove project
rm -rf ~/naukri-automation

# Uninstall packages (optional)
pkg uninstall maven openjdk-17 firefox geckodriver
```

---

## Support

**If you encounter issues:**
1. Check logs: `cat ~/naukri-automation/naukri.log`
2. Check screenshots in project folder
3. Verify credentials in config.properties
4. Ensure resume files are in data/ folder
5. Check Termux has all permissions

---

## Security Notes

- ✅ config.properties is in .gitignore (not committed)
- ✅ Credentials stored locally on phone only
- ✅ No data sent to external servers
- ⚠️ Keep your phone locked with PIN/password
- ⚠️ Don't share screenshots with credentials visible

---

**Last Updated:** 2026-01-20  
**Version:** 1.0.0  
**Status:** ✅ Tested and Working
