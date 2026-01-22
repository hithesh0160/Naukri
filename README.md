# Naukri Resume Upload Automation

Automated Selenium-based framework for uploading resumes to Naukri.com daily. Runs locally in headless mode with auto-wake and auto-hibernate scheduling.

## Features

- ✅ **Automated daily resume upload** at 6:30 AM (Windows scheduled task with auto-wake)
- ✅ **Alternates between two resume files** (Playwright and Selenium versions)
- ✅ **Headless mode** - Runs invisibly in background
- ✅ **Auto-hibernate** after execution (zero power consumption)
- ✅ **Local execution** with consistent IP (no OTP issues)
- ✅ **Screenshot capture** on success/failure
- ✅ **Comprehensive logging**
- ✅ **Page Object Model** architecture

**Note:** Profile text updates (Headline/About section) are not automated as Naukri detects and blocks automated text changes. Resume upload alone is sufficient to trigger "recently updated" status. **For best results, manually update your Resume Headline once a week.**

## Quick Start (10 Minutes)

### Prerequisites

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Firefox** ([Download](https://www.mozilla.org/firefox/))
- **Git** ([Download](https://git-scm.com/downloads))
- **Windows 10/11** (for auto-wake scheduling)

### Local Setup (5 Minutes)

1. **Clone the repository**
```bash
git clone https://github.com/hithesh0160/Naukri.git
cd Naukri
```

2. **Configure credentials**
```bash
cd src/com/naukri/config
cp config.properties.example config.properties
```

Edit `config.properties`:
```properties
username=your_naukri_email@example.com
password=your_naukri_password
```

3. **Add resume files**

Place two PDF files in `data/`:
- `Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf.pdf`
- `Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf.pdf`

4. **Test the automation**
```cmd
# Test with visible browser
set HEADLESS=false
run-naukri.bat

# Test with headless mode (recommended)
set HEADLESS=true
run-naukri.bat
```

5. **Setup automated scheduling** (see below)

---

## Windows Auto-Wake Setup

Your PC will automatically:
1. ⏰ Wake from hibernate at 6:30 AM
2. 🤖 Run automation (headless, invisible)
3. 💤 Hibernate again (zero power)

**Power cost:** ~₹35-50/month (10 minutes daily)

### Quick Setup (5 Minutes)

**Step 1: Enable Wake Timers**
1. Control Panel → Power Options → Change plan settings
2. Change advanced power settings → Sleep → Allow wake timers → **Enable**

**Step 2: Enable Hibernate**
```powershell
# Run in PowerShell (Admin)
powercfg /hibernate on
```

**Step 3: Create Scheduled Task**
```powershell
# Run in PowerShell (Admin)
cd "D:\Naukri Job Update\Naukri"  # Update path

$action = New-ScheduledTaskAction -Execute "$PWD\run-and-sleep.bat" -WorkingDirectory $PWD
$trigger = New-ScheduledTaskTrigger -Daily -At "06:30"
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable -WakeToRun
$principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest

Register-ScheduledTask -TaskName "Naukri Resume Upload" -Description "Daily Naukri profile update" -Action $action -Trigger $trigger -Settings $settings -Principal $principal
```

**Done!** Your PC will now wake daily at 6:30 AM, run the automation, and hibernate.

📖 **For detailed setup guide, see:** [WINDOWS-AUTO-WAKE-GUIDE.md](WINDOWS-AUTO-WAKE-GUIDE.md)

---
cd Naukri
```

2. **Configure credentials**
```bash
cd src/com/naukri/config
cp config.properties.example config.properties
```

Edit `config.properties`:
```properties
username=your_naukri_email@example.com
password=your_naukri_password
```

3. **Add resume files**

Place two PDF files in `data/`:
- `Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf`
- `Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf`

4. **Run tests**
```bash
mvn clean test
```

## GitHub Actions Setup (Optional - Has OTP Limitations)

**Note:** GitHub Actions may trigger OTP verification from Naukri. Local scheduling is recommended for reliable automation.

### 1. Fork Repository

Click "Fork" button on GitHub.

### 2. Add Secrets

Go to: **Settings** → **Secrets and variables** → **Actions**

Add two secrets:
- `NAUKRI_USERNAME` = your_naukri_email@example.com
- `NAUKRI_PASSWORD` = your_naukri_password

### 3. Enable Actions

Go to **Actions** tab → Enable workflows

### 4. Done!

- Runs automatically daily at **8:00 AM IST**
- Manual trigger: Actions → Run workflow

## Project Structure

```
├── .github/workflows/
│   └── naukri-resume-upload.yml    # GitHub Actions workflow
├── src/com/naukri/
│   ├── pages/                      # Page Object Model
│   │   ├── NaukriLoginPage.java
│   │   └── NaukriProfilePage.java
│   ├── testscript/                 # Test classes
│   │   └── Naukri.java
│   ├── util/                       # Utilities
│   │   ├── ConfigUtil.java
│   │   ├── DriverManager.java
│   │   ├── ResumeManager.java
│   │   └── ScreenshotUtil.java
│   ├── config/                     # Configuration
│   │   ├── config.properties.example
│   │   └── last_resume_uploaded.properties
│   └── logback.xml                 # Logging config
├── data/                           # Resume files
│   ├── resume1.pdf
│   └── resume2.pdf
├── pom.xml                         # Maven configuration
└── testng.xml                      # TestNG suite
```

## Configuration

### Local Development (Recommended)

Uses `config.properties` file:
```properties
username=your_email@example.com
password=your_password
```

**Note:** This file is in `.gitignore` and never committed.

### GitHub Actions

Uses GitHub Secrets as environment variables. No code changes needed.

## How It Works

### Browser Selection

- **Local:** Automatically uses Firefox (visible browser)
- **GitHub Actions:** Automatically uses Chrome (headless)

Detection is automatic based on `CI` environment variable.

### Anti-Bot Detection Measures

To avoid being blocked by Naukri.com's bot detection systems, the framework implements multiple anti-detection strategies:

#### 1. Browser Fingerprint Masking
- **Disabled automation flags:** `--disable-blink-features=AutomationControlled`
- **Removed automation switches:** Excludes `enable-automation` switch
- **Disabled automation extension:** `useAutomationExtension: false`
- **Hidden webdriver property:** Uses CDP commands to set `navigator.webdriver` to `undefined`
- **Fake plugins array:** Overrides `navigator.plugins` to appear as real browser
- **Language settings:** Sets realistic `navigator.languages` array
- **Realistic user agent:** Sets Linux Chrome 143 user agent string
- **Disabled web security features:** For seamless automation
- **Disabled infobars and notifications:** Removes automation indicators
- **Password manager disabled:** Prevents automation detection via browser preferences

#### 2. Human-Like Behavior
- **Slow typing:** Types credentials character-by-character with 100-200ms random delays per character
- **Multiple strategic delays:**
  - 2 seconds after page load (let page fully render)
  - 1.5 seconds after clicking login link
  - 800ms between email and password entry
  - 1 second before clicking login button
  - 5 seconds after login submission (wait for processing)
- **Realistic window size:** Uses standard 1920x1080 resolution
- **Temporary user data:** Creates fresh Chrome profile for each run

#### 3. Intelligent Element Detection
- **Multiple locator strategies:** Falls back to alternative CSS selectors if primary fails
- **JavaScript click fallback:** Uses JS click if regular click is blocked
- **Direct file upload:** Attempts direct file input before clicking buttons
- **Extended timeouts:** 60-second waits for critical elements
- **Google iframe handling:** Detects and hides Google Sign-In overlays that block interactions

#### 4. Debugging & Monitoring
- **Multi-stage screenshots:** 
  - `after-login.png` - Captures state immediately after login attempt
  - `before-upload.png` - Shows page state before upload
  - `resume-upload-success.png` - Success confirmation
  - `resume-upload-failure.png` - Failure state for debugging
- **Detailed logging:** Logs URLs, titles, and element states at each step
- **Login verification:** Explicitly checks if still on login page and throws clear errors
- **Page source logging:** Records page source length for debugging

These measures make the automation appear more like a real user, reducing the likelihood of being flagged as a bot.

### Resume Rotation

Alternates between `Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf` and `Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf` on each run. State tracked in `last_resume_uploaded.properties`.

### Logging

- **Console:** Real-time output
- **File:** `logs/naukri-automation.log` (30-day rotation)

## Troubleshooting

### Maven not found
Install from https://maven.apache.org/download.cgi and add to PATH.

### Firefox not found
Install from https://www.mozilla.org/firefox/

### Config not found
Create `src/com/naukri/config/config.properties` from the example file.

### Resume file not found
Ensure files are named exactly:
- `Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf`
- `Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf`

And placed in the `data/` folder.

### Test fails
Check logs in `logs/naukri-automation.log` and screenshots (`.png` files).

### GitHub Actions fails
- Verify secrets are configured correctly
- Check workflow logs in Actions tab
- Download artifacts (screenshots, logs) for debugging
- If blocked by bot detection, check `before-upload.png` to see page state
- Bot detection errors typically show "Access Denied" or missing elements
- **OTP Required:** If `after-login.png` shows OTP page, GitHub Actions won't work reliably
  - Solution: Use local scheduling or self-hosted runner instead

## Customization

### Change Schedule Time

Edit `.github/workflows/naukri-resume-upload.yml`:
```yaml
schedule:
  - cron: '30 2 * * *'  # 8:00 AM IST = 2:30 AM UTC
```

Use [crontab.guru](https://crontab.guru/) to generate cron expressions.

### Add More Resume Files

Edit `src/com/naukri/util/ResumeManager.java` and add files to `RESUME_FILES` array.

## Technology Stack

- **Language:** Java 17
- **Build Tool:** Maven 3.6+
- **Test Framework:** TestNG 7.10.2
- **Automation:** Selenium 4.27.0 (with built-in Selenium Manager)
- **Logging:** SLF4J 2.0.16 + Logback 1.5.12
- **CI/CD:** GitHub Actions

## Security

- ✅ Never commit `config.properties`
- ✅ Use GitHub Secrets for CI/CD
- ✅ `.gitignore` excludes sensitive files
- ✅ Rotate passwords regularly

## Output Files

After running tests:
- `after-login.png` - Screenshot immediately after login attempt (shows if OTP required)
- `before-upload.png` - Screenshot before upload attempt (for debugging)
- `resume-upload-success.png` - Screenshot on success
- `resume-upload-failure.png` - Screenshot on failure
- `logs/naukri-automation.log` - Detailed logs
- `test-output/` - TestNG HTML reports

**Note:** All `.png` files are excluded from Git and overwritten on each run.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test locally
5. Submit a pull request

## License

MIT License - See LICENSE file for details

## Support

For issues or questions:
1. Check this README
2. Search existing GitHub Issues
3. Create new issue with error details and logs

---

**Last Updated:** 2026-01-12  
**Version:** 1.0.0  
**Status:** ✅ Production Ready (Local Automation) | ⚠️ Limited (GitHub Actions - OTP Required)
