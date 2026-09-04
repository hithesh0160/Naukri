# Termux Quick Start Guide

Quick reference for running Naukri automation on Android/Termux.

## 🚀 Installation (One-time)

```bash
# 1. Install packages
pkg update && pkg upgrade
pkg install openjdk-17 maven git termux-boot firefox tigervnc

# 2. Install geckodriver
wget https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz
tar -xzf geckodriver-v0.34.0-linux64.tar.gz
mv geckodriver $PREFIX/bin/
chmod +x $PREFIX/bin/geckodriver

# 3. Setup VNC
vncserver -localhost :1
echo 'export DISPLAY=:1' >> ~/.bashrc
source ~/.bashrc

# 4. Clone repo
cd ~ && git clone https://github.com/hithesh0160/Naukri.git
cd Naukri

# 5. Configure credentials
nano src/com/naukri/config/config.properties
# Add: username, password, telegram tokens

# 6. Make scripts executable
chmod +x *.sh

# 7. Setup scheduling (choose one)
# Samsung Routines (recommended for Samsung) - see full guide
./schedule-naukri.sh        # Option B: termux-job-scheduler
./setup-cron-termux.sh      # Option C: cron
```

## 📱 Android Settings

**Critical - Do this or it won't work!**

1. Settings → Apps → Termux
2. Battery → Don't optimize
3. Background activity → Allow
4. Disable Adaptive Battery for Termux

**Install Termux:Boot app from F-Droid** for auto-start functionality

## ✅ Test Run

```bash
cd ~/Naukri
./run-naukri-termux.sh
```

Expected: Logs, screenshots, Telegram notification

## 📋 Commands

```bash
# View logs
tail -f ~/Naukri/logs/naukri-automation.log

# View scheduled jobs
termux-job-scheduler '-list'

# View cron jobs
crontab -l

# Cancel scheduled job
termux-job-scheduler '-cancel' '-job-id' 1001

# Stop VNC
vncserver -kill :*

# Restart VNC
vncserver -localhost :1 && export DISPLAY=:1

# Manual run
cd ~/Naukri && ./run-naukri-termux.sh
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Job not running | Check battery settings, use Samsung Routines if available |
| Firefox not found | `pkg install firefox` |
| geckodriver missing | Reinstall geckodriver from GitHub releases |
| Out of memory | `export MAVEN_OPTS="-Xmx512m"` |
| VNC issues | `vncserver -kill :* && vncserver -localhost :1` |
| OTP prompts | Use WiFi (not mobile data), login manually first |
| Firefox headless crash | Script uses visible mode with VNC - check VNC is running |

## 📁 File Locations

- Script: `~/Naukri/run-naukri-termux.sh`
- Logs: `~/Naukri/logs/naukri-automation.log`
- Cron log: `~/Naukri/cron.log`
- Config: `~/Naukri/src/com/naukri/config/config.properties`
- Screenshots: `~/Naukri/*.png`

## 🔄 Keep Termux Alive

**Samsung Routines (Recommended for Samsung):**
1. Install Termux:Boot from F-Droid
2. Create startup script: `~/.termux/boot/naukri-automation.sh` (see full guide)
3. Create Samsung Routine: Time → Open Termux app

**Termux:Boot (For non-Samsung):**
```bash
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-services.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash
vncserver -localhost :1
export DISPLAY=:1
EOF
chmod +x ~/.termux/boot/start-services.sh
```

## ⚠️ Limitations

- Battery drain (keep plugged in during run time)
- Mobile IP changes → OTP issues (use WiFi)
- Less reliable than PC/laptop setup
- High resource usage on phone
- Script timeout: 10 minutes (auto-kills if exceeds)
- Requires VNC server for Firefox display

## 💡 Tips

1. Test manually before scheduling
2. Use Samsung Routines if you have a Samsung device (most reliable)
3. Use old/spare Android device
4. Keep device plugged in
5. Use stable WiFi, not mobile data
6. Check logs weekly
7. Restart device weekly
8. Firefox is more stable than Chrome on Android

## 📖 Full Documentation

See `TERMUX-ANDROID-SETUP.md` for detailed setup guide.

---

**Browser:** Firefox (more stable than Chrome on Android)
**Scheduling:** Samsung Routines + Termux:Boot (recommended)

Need help? Check logs first:
```bash
cat ~/Naukri/logs/naukri-automation.log
cat ~/Naukri/cron.log
```
