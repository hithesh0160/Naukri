# Naukri Automation Guide

## The OTP Challenge

Naukri.com implements security measures that trigger OTP (One-Time Password) verification when:
- Logging in from new or changing IP addresses
- Detecting automated browser patterns
- Identifying suspicious login behavior

**GitHub Actions Impact:** Each GitHub Actions run uses a different IP address from GitHub's pool, causing Naukri to treat every login as suspicious and require OTP verification. This makes scheduled automation via GitHub Actions unreliable.

## Recommended Solution: Local Scheduling

The most reliable approach is to run the automation on your local machine or a server with a consistent IP address.

### Why Local Automation Works

1. **Consistent IP Address** - Your home/office IP remains the same
2. **Trusted Device** - Naukri recognizes your machine over time
3. **No OTP Required** - After initial trust, logins work smoothly
4. **Full Control** - You manage when and how it runs

### Setup Instructions

#### Windows Users

1. **Test the script first:**
   ```cmd
   run-naukri.bat
   ```

2. **Set up Task Scheduler:**
   - Press `Win + R`, type `taskschd.msc`, press Enter
   - Click "Create Basic Task" in the right panel
   - Name: `Naukri Resume Upload`
   - Description: `Daily automated resume upload to Naukri.com`
   - Trigger: Daily
   - Start time: 8:00 AM
   - Action: Start a program
   - Program/script: Browse to `run-naukri.bat` in your project folder
   - Click Finish

3. **Verify it works:**
   - Right-click the task → Run
   - Check the logs folder for output

#### Linux/Mac Users

This repository currently includes Windows batch launchers (`run-naukri.bat`, `run-and-sleep.bat`) and does not ship a maintained `run-naukri.sh`.

If you want Linux/Mac scheduling, run the Java command with cron directly:

```bash
cd /full/path/to/Naukri
mvn clean test >> /full/path/to/Naukri/cron.log 2>&1
```

Then add it to `crontab -e` at your preferred schedule.

### Alternative: Self-Hosted GitHub Runner

If you prefer using GitHub Actions, you can set up a self-hosted runner on your machine:

1. **Benefits:**
   - Uses your consistent IP address
   - Integrates with GitHub workflow
   - Centralized logging and monitoring

2. **Setup:**
   - Go to your GitHub repo → Settings → Actions → Runners
   - Click "New self-hosted runner"
   - Follow the installation instructions for your OS
   - Uncomment the schedule in `.github/workflows/naukri-resume-upload.yml`

3. **Considerations:**
   - Your machine must be running when the schedule triggers
   - Requires GitHub Runner service to be active
   - More complex than simple Task Scheduler/cron

## Troubleshooting

### Still Getting OTP Locally?

If you're getting OTP even on your local machine:

1. **Clear browser data:** Delete the temp Chrome user data
2. **Use Firefox:** The local setup uses Firefox which may be more trusted
3. **Login manually first:** Open Naukri in Firefox, login, then run the script
4. **Check IP changes:** Ensure your ISP isn't changing your IP frequently
5. **Wait 24 hours:** Naukri may trust your IP after consistent usage

### Script Fails to Run

1. **Check Maven path:** Ensure Maven is in your system PATH
2. **Check Java version:** Must be Java 17 or higher
3. **Check Firefox:** Must be installed for local runs
4. **Check credentials:** Verify config.properties has correct username/password
5. **Check resume files:** Ensure files expected by `ResumeManager` exist in `data/`

### Logs Show Errors

1. **Check logs/naukri-automation.log** for detailed error messages
2. **Check screenshots** (*.png files) to see what the browser saw
3. **Run manually** with `mvn clean test` to see real-time output

## Best Practices

1. **Keep credentials secure:** Never commit config.properties
2. **Monitor logs:** Check logs folder weekly for issues
3. **Update resumes:** Replace PDF files when you update your resume
4. **Test after changes:** Run manually after any code changes
5. **Backup configuration:** Keep a copy of your config.properties
6. **Avoid over-updating:** Once daily upload is usually enough for freshness

## Why Not Use GitHub Actions?

While GitHub Actions is convenient, it's not suitable for this use case because:

- ❌ Different IP on every run → OTP required
- ❌ Can't handle OTP input automatically
- ❌ Naukri's security measures are designed to block this
- ❌ No reliable workaround without compromising security

Local scheduling is the intended and supported approach for this automation.

## Support

If you encounter issues:
1. Check this guide first
2. Review logs and screenshots
3. Test manually with `mvn clean test`
4. Create a GitHub issue with error details

---

**Remember:** This automation is meant to save you time, not bypass security. Always use your real credentials and respect Naukri's terms of service.
