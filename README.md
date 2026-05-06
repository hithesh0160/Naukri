# Naukri Resume Upload Automation

Automates daily resume upload on Naukri using Java + Selenium + TestNG.

The reliable setup is **local execution on a consistent IP** (Windows Task Scheduler recommended). GitHub Actions is kept for manual testing and often hits OTP checks.

## What This Project Does

- Logs into Naukri and uploads resume from local `data/`.
- Rotates between two resume files on each run.
- Captures screenshots for success/failure troubleshooting.
- Sends optional Telegram status notifications.
- Supports unattended local runs via `run-naukri.bat` and `run-and-sleep.bat`.

## Prerequisites

- Java 17+
- Maven 3.6+
- Firefox (for local runs)
- Windows 10/11 (for wake + schedule flow)

## Quick Start

1. Clone repo and open project folder.
2. Create `src/com/naukri/config/config.properties` from `src/com/naukri/config/config.properties.example`.
3. Add your values:

```properties
username=your_email@example.com
password=your_password
telegram.token=
telegram.chatid=
```

4. Place resume files in `data/` using names expected by `src/com/naukri/util/ResumeManager.java`.
5. Run once manually:

```cmd
run-naukri.bat
```

6. Set up scheduler for daily run (see `WINDOWS-AUTO-WAKE-GUIDE.md`).

## Run Modes

- `run-naukri.bat`: executes upload flow and exits.
- `run-and-sleep.bat`: executes upload flow and then hibernates machine.
- Direct Maven run:

```cmd
mvn clean test
```

## Configuration Priority

The app reads values in this order:

1. `config.properties`
2. Environment variables (`NAUKRI_USERNAME`, `NAUKRI_PASSWORD`, `TELEGRAM_TOKEN`, `TELEGRAM_CHAT_ID`)

## Important Notes

- Do not automate frequent profile edits. One update per day is enough.
- Resume upload is the primary freshness signal; text/profile edits should be manual.
- If login prompts OTP, local scheduling from your usual machine/IP is more reliable than cloud runners.

## Documentation

- Setup and reliability notes: `AUTOMATION-GUIDE.md`
- Windows wake/schedule instructions: `WINDOWS-AUTO-WAKE-GUIDE.md`
- Telegram setup: `TELEGRAM-SETUP.md`
- Security checklist: `SECURITY.md`

## Output Artifacts

- Screenshots: `*.png`
- Logs: `logs/naukri-automation.log`
- Test reports: `test-output/` and `target/surefire-reports/`

These outputs are ignored by git.

## GitHub Actions

Workflow exists at `.github/workflows/naukri-resume-upload.yml`, but schedule is disabled by default due to OTP/IP reliability issues.

Use `workflow_dispatch` for occasional manual checks, or a self-hosted runner with a consistent IP.

## Security

- Never commit credentials, tokens, or personal data files.
- Keep `config.properties` local only.
- Rotate credentials immediately if exposed.

See `SECURITY.md` for remediation guidance.
