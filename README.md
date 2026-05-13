# Naukri Resume Upload Automation

Automates daily resume upload + profile update + job auto-apply on Naukri using Java + Selenium + TestNG.

The reliable setup is **local execution on a consistent IP** (Windows Task Scheduler recommended).

## What This Project Does

- **Logs in** to Naukri and navigates to profile
- **Updates profile sections**: headline (verified save), about section, key skills
- **Uploads resume** from local `data/` (rotates between two PDFs each run)
- **Auto-applies** to matching jobs (SDET, Automation Testing, Playwright, QA Automation) via Easy Apply
- **Captures screenshots** for success/failure troubleshooting
- **Sends Telegram notifications** with resume status + list of companies applied to
- **Supports unattended** local runs via `run-naukri.bat` and `run-and-sleep.bat`

## Prerequisites

- Java 17+
- Maven 3.6+
- Chrome (auto-downloads ChromeDriver via Selenium Manager)
- Windows 10/11 (for wake + schedule flow)

## Quick Start

1. Clone repo and open project folder.
2. Create `src/com/naukri/config/config.properties`:
   ```properties
   username=your_email@example.com
   password=your_password
   telegram.token=
   telegram.chatid=
   ```
3. Place resume files in `data/`.
4. Run:
   ```cmd
   run-naukri.bat
   ```

## Run Modes

| Command | Headless | Browser | Sleep after |
|---|---|---|---|
| `run-naukri.bat` | No (visible) | Chrome | No |
| `run-and-sleep.bat` | Yes (headless) | Chrome | Yes (S3 sleep) |
| `mvn clean test` | Uses env vars | Chrome default | No |

**Environment variables:**
- `HEADLESS=false` — set to `true` to hide browser
- `BROWSER=chrome` — set to `firefox` to use Firefox

## Auto-Apply Feature

After profile update + resume upload, the script searches Naukri for:
- `SDET Java`, `Automation Testing Selenium`, `Playwright`, `QA Automation`

Applies to up to **10 jobs per run** with random delays (30-90s) to avoid detection. Applied jobs are tracked in `job_apply.properties` to prevent re-application within 30 days.

## Configuration Priority

1. `config.properties`
2. Environment variables (`NAUKRI_USERNAME`, `NAUKRI_PASSWORD`, `TELEGRAM_TOKEN`, `TELEGRAM_CHAT_ID`, `HEADLESS`, `BROWSER`)

## Documentation

- Setup and reliability notes: `AUTOMATION-GUIDE.md`
- Windows wake/schedule instructions: `WINDOWS-AUTO-WAKE-GUIDE.md`
- Telegram setup: `TELEGRAM-SETUP.md`
- Security checklist: `SECURITY.md`

## Output Artifacts

- Screenshots: `*.png`
- Logs: `logs/naukri-automation.log`
- Test reports: `test-output/` and `target/surefire-reports/`
- Applied jobs tracker: `src/com/naukri/config/job_apply.properties`

## Security

- Never commit credentials, tokens, or personal data files.
- Keep `config.properties` and `job_apply.properties` local only.
- Rotate credentials immediately if exposed.

See `SECURITY.md` for remediation guidance.
