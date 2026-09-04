# Naukri Automation Guide

## The OTP Challenge

Naukri.com implements security measures that trigger OTP (One-Time Password) verification when:
- Logging in from new or changing IP addresses
- Detecting automated browser patterns
- Identifying suspicious login behavior

**GitHub Actions Impact:** Each GitHub Actions run uses a different IP address from GitHub's pool, causing Naukri to treat every login as suspicious and require OTP verification. This makes scheduled automation via GitHub Actions unreliable.

## How The Automation Works

The script performs these steps in order:

1. **Random organic delay** (10s-2min) — simulates human behavior
2. **Login** to Naukri with configured credentials
3. **Close overlays** — handles cookie banners, app prompts, chat widgets
4. **Update resume headline** — alternates "Open to Work" / "Actively Looking" markers (truncated to 245 chars for Naukri's limit)
5. **Update about section** — alternates "#OpenToWork" / "#Hiring" markers
6. **Add random key skill** — rotates through Java, Selenium, Playwright, SQL, etc.
7. **Upload resume** — rotates between two PDFs from `data/`
8. **Auto-apply** — searches and Easy Applies to matching jobs
9. **Telegram notification** — success/failure + applied companies list

### Anti-Detection Measures

- **Chrome** (default): CDP commands to hide `navigator.webdriver`, realistic user agent, temp user data dir, disabled automation flags
- **Firefox** (optional via `BROWSER=firefox`): user agent override, `dom.webdriver.enabled=false`, JS injection
- Both: random typing delays (bulk send + 10 slow chars), random organic startup delay, random pauses between job applications

## Setup Instructions

### Windows Users

1. **Test the script first:**
   ```cmd
   run-naukri.bat
   ```
   Chrome will open visibly. Watch the browser work through login → profile update → resume upload → auto-apply.

2. **For scheduled runs (headless):**
   Edit `run-naukri.bat` → set `HEADLESS=true`, then set up Task Scheduler.

3. **Set up Task Scheduler:**
   - Press `Win + R`, type `taskschd.msc`
   - Click "Create Basic Task"
   - Name: `Naukri Resume Upload`
   - Trigger: Daily at 6:30 AM
   - Action: Start `run-and-sleep.bat`
   - In Properties → Conditions → ✅ "Wake the computer to run this task"

4. **Verify it works:**
   - Right-click the task → Run
   - Check the logs folder for output

### Linux/Mac Users

Use cron directly:
```bash
cd /full/path/to/Naukri
HEADLESS=true BROWSER=chrome mvn clean test >> cron.log 2>&1
```

Then add to `crontab -e` at your preferred schedule.

### Android/Termux Users

For Android devices, use Termux with Firefox (more stable than Chrome on Android):

1. **Install Termux from F-Droid** (NOT Google Play)
2. **Install required packages:**
   ```bash
   pkg install openjdk-17 maven git termux-boot firefox tigervnc
   ```
3. **Install geckodriver:**
   ```bash
   wget https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz
   tar -xzf geckodriver-v0.34.0-linux64.tar.gz
   mv geckodriver $PREFIX/bin/
   chmod +x $PREFIX/bin/geckodriver
   ```
4. **Setup VNC server** (required for Firefox display on Android)
5. **Configure scheduling** - Samsung Routines + Termux:Boot is recommended for Samsung devices
6. **Disable battery optimization** for Termux in Android settings

**Important:** Android setup has limitations - see `TERMUX-ANDROID-SETUP.md` for detailed instructions.

### Alternative: Self-Hosted GitHub Runner

1. Go to your GitHub repo → Settings → Actions → Runners
2. Click "New self-hosted runner"
3. Follow installation instructions for your OS
4. Uncomment the schedule in `.github/workflows/naukri-resume-upload.yml`

## Troubleshooting

### Still Getting OTP Locally?

1. **Clear Chrome user data** — the script uses temp profiles each run
2. **Login manually first** — open Naukri in Chrome, login, then run the script
3. **Check IP changes** — ensure your ISP isn't changing your IP frequently
4. **Wait 24 hours** — Naukri may trust your IP after consistent usage

### Script Fails to Run

1. **Check Java version:** Must be Java 17+
2. **Check Maven path:** Ensure Maven is in PATH or set `MAVEN_HOME`
3. **Check Chrome:** Must be installed (Selenium Manager auto-downloads ChromeDriver)
4. **Check credentials:** Verify config.properties has correct username/password
5. **Check resume files:** Ensure files expected by `ResumeManager` exist in `data/`

### Headline / Profile Updates Not Saving

- **Character limit:** Naukri has a 250-char limit. The script auto-truncates to 245.
- **Save button not found:** The script clicks outside the textarea first to reveal the save button, then clicks it via JavaScript.
- **Verification passes**: Successful saves are verified by re-opening the editor and checking the persisted value.

### Logs Show Errors

1. **Check logs/naukri-automation.log** for detailed error messages
2. **Check screenshots** (*.png files) to see what the browser saw
3. **Run manually** with `mvn clean test` and `HEADLESS=false` to see real-time output

## Job Auto-Apply

The auto-apply feature:
- Searches Naukri for SDET, Automation, Playwright, and QA roles in Bangalore
- Finds job cards using multiple locator strategies
- Clicks "Easy Apply" or "Apply" buttons via JavaScript
- Handles submit steps if present
- Applies to max **5 jobs per run**
- **30-90 second delay** between applications to avoid rate limiting
- Tracks applied jobs in `job_apply.properties` (no re-applies within 30 days)
- Sends Telegram summary of applied companies

## Best Practices

1. **Keep credentials secure:** Never commit config.properties
2. **Monitor logs:** Check logs folder weekly for issues
3. **Update resumes:** Replace PDF files when you update your resume
4. **Test after changes:** Run manually after any code changes
5. **Backup configuration:** Keep a copy of your config.properties
6. **Avoid over-updating:** Once daily is enough for freshness signals

## Why Not Use GitHub Actions?

- ❌ Different IP on every run → OTP required
- ❌ Can't handle OTP input automatically
- ❌ No reliable workaround

Local scheduling is the intended and supported approach.

## Support

1. Check this guide first
2. Review logs and screenshots
3. Test manually with `mvn clean test`
4. Create a GitHub issue with error details

---

**Last Updated:** 2026-05-13  
**Version:** 2.0.0
