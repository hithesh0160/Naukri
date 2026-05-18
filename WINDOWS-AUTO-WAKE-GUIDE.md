# Windows Auto Wake + Run + Sleep Setup

## Overview

Your PC will:
1. Wake from sleep at 6:30 AM
2. Run Naukri automation (headless Chrome)
3. Apply to matching jobs automatically
4. Go back to sleep

**Power usage:** ~5-10 minutes daily = negligible cost

---

## Quick Setup (5 Minutes)

### Step 1: Enable Wake Timers

1. **Control Panel** → **Power Options**
2. **Change plan settings** → **Change advanced power settings**
3. Expand **Sleep** → **Allow wake timers** → **Enable** (both battery and plugged in)
4. Click **OK**

### Step 2: Create Scheduled Task

Run the following in **PowerShell as Administrator**:

```powershell
cd "D:\Naukri Job Update\Naukri"  # Update this path

$action = New-ScheduledTaskAction -Execute "$PWD\run-and-sleep.bat" -WorkingDirectory $PWD
$trigger = New-ScheduledTaskTrigger -Daily -At "06:30"
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable -WakeToRun
$principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest

Register-ScheduledTask -TaskName "Naukri Resume Upload" -Description "Daily Naukri profile update + auto-apply" -Action $action -Trigger $trigger -Settings $settings -Principal $principal
```

### Step 3: Verify

1. Open **Task Scheduler** (`taskschd.msc`)
2. Find **"Naukri Resume Upload"**
3. Right-click → **Run**
4. Watch Chrome open, run the automation, and close

---

## What Happens Each Run

| Time | Event |
|---|---|
| 6:30 AM | PC wakes from sleep |
| 6:30 AM | Chrome launches (headless) |
| 6:31 AM | Login to Naukri |
| 6:32 AM | Update profile sections |
| 6:33 AM | Upload resume |
| 6:34 AM | Auto-apply to jobs (~5 per keyword) |
| 6:40 AM | PC goes back to sleep |

---

## Power Management

The script uses **S3 Sleep** (`rundll32.exe powrprof.dll,SetSuspendState 0,1,0`), not hibernate. Sleep preserves your session in RAM and supports wake timers reliably.

- **Sleep mode:** ~2-5W
- **Wake + Run:** ~50W for 10 minutes
- **Total:** ~₹5-10/month

---

## Testing

### Test 1: Manual Run
```
Task Scheduler → task → Right-click → Run
```

### Test 2: Wake from Sleep
1. Put PC to sleep manually
2. Wait 2 minutes, then wake it
3. Check Task Scheduler history

### Test 3: Full Wake Test
1. Change task time to 2 minutes from now
2. Put PC to sleep
3. Wait — PC should wake and run automatically
4. Change time back to 6:30 AM

---

## Troubleshooting

### PC doesn't wake up

- **Power Settings:** Sleep → Allow wake timers → **Enable**
- **BIOS:** Enable "Wake on RTC" or "Wake on Timer"
- **Task Properties:** Conditions → ✅ "Wake the computer to run this task"

### PC wakes but doesn't run

- **Task Settings:** ✅ "Run with highest privileges"
- **Batch path:** Verify `run-and-sleep.bat` path is correct
- **Test manually:** Double-click the batch file

### PC doesn't go back to sleep

Check the PC isn't being kept awake by other processes. The script sends the sleep command 10 seconds after completion.

### Task runs but fails

- Check `logs/naukri-automation.log`
- Check screenshots (`resume-upload-failure.png`)
- Task Scheduler → History tab

---

## Maintenance

### Weekly
- Verify resume uploads on Naukri
- Check applied jobs in `job_apply.properties`

### Monthly
- Check logs for errors
- Update project: `git pull`

---

## Disable/Remove

```powershell
# Temporarily disable
Disable-ScheduledTask -TaskName "Naukri Resume Upload"

# Re-enable
Enable-ScheduledTask -TaskName "Naukri Resume Upload"

# Permanently remove
Unregister-ScheduledTask -TaskName "Naukri Resume Upload" -Confirm:$false
```

---

## FAQ

**Q: Will this work if PC is shut down?**  
A: No. PC must be in Sleep (S3), not shut down.

**Q: What if I'm using the PC at 6:30 AM?**  
A: Task runs in background (headless). You won't notice it.

**Q: Can I change the time?**  
A: Yes, edit the task trigger in Task Scheduler.

**Q: Will this drain my laptop battery?**  
A: Minimal (~5 min daily). Keep plugged in at night.

**Q: Can I run multiple times per day?**  
A: Not recommended. Once daily is optimal.

---

**Last Updated:** 2026-05-13  
**Version:** 2.0.0
