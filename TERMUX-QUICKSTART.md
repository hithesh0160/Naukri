# Termux Quick Start Guide

Quick reference for running Naukri automation on Android/Termux.

## 🚀 Installation (One-time)

```bash
# 1. Install packages
pkg update && pkg upgrade
pkg install openjdk-17 maven git termux-api x11-repo chromium tigervnc cronie

# 2. Setup VNC
vncserver -localhost
echo 'export DISPLAY=:1' >> ~/.bashrc
source ~/.bashrc

# 3. Clone repo
cd ~ && git clone YOUR_REPO_URL
cd Naukri

# 4. Configure credentials
nano src/com/naukri/config/config.properties
# Add: username, password, telegram tokens

# 5. Make scripts executable
chmod +x *.sh

# 6. Setup scheduling (choose one)
./schedule-naukri.sh        # Option A: termux-job-scheduler
./setup-cron-termux.sh      # Option B: cron
```

## 📱 Android Settings

**Critical - Do this or it won't work!**

1. Settings → Apps → Termux
2. Battery → Don't optimize
3. Background activity → Allow
4. Disable Adaptive Battery for Termux

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
termux-job-scheduler --list

# View cron jobs
crontab -l

# Cancel scheduled job
termux-job-scheduler --cancel --job-id 1001

# Stop VNC
vncserver -kill :*

# Restart VNC
vncserver -localhost && export DISPLAY=:1

# Manual run
cd ~/Naukri && ./run-naukri-termux.sh
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Job not running | Check battery settings, keep Termux in recents |
| Chrome not found | `pkg install chromium` |
| Out of memory | `export MAVEN_OPTS="-Xmx512m"` |
| VNC issues | `vncserver -kill :* && vncserver -localhost` |
| OTP prompts | Use WiFi (not mobile data), login manually first |

## 📁 File Locations

- Script: `~/Naukri/run-naukri-termux.sh`
- Logs: `~/Naukri/logs/naukri-automation.log`
- Cron log: `~/Naukri/cron.log`
- Config: `~/Naukri/src/com/naukri/config/config.properties`
- Screenshots: `~/Naukri/*.png`

## 🔄 Keep Termux Alive

**Install Termux:Boot from F-Droid**, then:

```bash
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-services.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash
vncserver -localhost
export DISPLAY=:1
crond
EOF
chmod +x ~/.termux/boot/start-services.sh
```

## ⚠️ Limitations

- Battery drain (keep plugged in during run time)
- Mobile IP changes → OTP issues (use WiFi)
- Less reliable than PC/laptop setup
- High resource usage on phone

## 💡 Tips

1. Test manually before scheduling
2. Use old/spare Android device
3. Keep device plugged in
4. Use stable WiFi, not mobile data
5. Check logs weekly
6. Restart device weekly

## 📖 Full Documentation

See `TERMUX-ANDROID-SETUP.md` for detailed setup guide.

---

Need help? Check logs first:
```bash
cat ~/Naukri/logs/naukri-automation.log
cat ~/Naukri/cron.log
```
