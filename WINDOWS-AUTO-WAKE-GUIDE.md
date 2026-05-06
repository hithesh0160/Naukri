# Windows Auto Wake + Run + Sleep Setup

## Overview

Your PC will:
1. ⏰ Wake up from sleep at 6:30 AM
2. 🤖 Run Naukri automation (headless mode)
3. 😴 Go back to sleep automatically

**Power usage:** ~10 minutes daily = ₹30-40/month electricity

---

## Quick Setup (5 Minutes)

### Step 1: Enable Wake Timers

1. Open **Control Panel** → **Power Options**
2. Click **Change plan settings** (for your active plan)
3. Click **Change advanced power settings**
4. Expand **Sleep** → **Allow wake timers**
5. Set to **Enable** for both "On battery" and "Plugged in"
6. Click **OK**

### Step 2: Create Scheduled Task (PowerShell)

Run the following in **PowerShell as Administrator**:

```powershell
cd "D:\Naukri Job Update\Naukri"  # Update this path

$action = New-ScheduledTaskAction -Execute "$PWD\run-and-sleep.bat" -WorkingDirectory $PWD
$trigger = New-ScheduledTaskTrigger -Daily -At "06:30"
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable -WakeToRun
$principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest

Register-ScheduledTask -TaskName "Naukri Resume Upload" -Description "Daily Naukri profile update" -Action $action -Trigger $trigger -Settings $settings -Principal $principal
```

If this fails on your machine, use the Manual Setup section below.

---

## Manual Setup (Alternative)

If the PowerShell script doesn't work, follow these steps:

### 1. Open Task Scheduler

- Press `Win + R`
- Type: `taskschd.msc`
- Press Enter

### 2. Create Basic Task

1. Click **"Create Basic Task"** (right panel)
2. Name: `Naukri Resume Upload`
3. Description: `Daily Naukri profile update with auto sleep`
4. Click **Next**

### 3. Set Trigger

1. Select **"Daily"**
2. Click **Next**
3. Start time: `6:30 AM`
4. Recur every: `1 days`
5. Click **Next**

### 4. Set Action

1. Select **"Start a program"**
2. Click **Next**
3. Program/script: Browse to `run-and-sleep.bat`
4. Start in: `D:\Naukri Job Update\Naukri` (your project folder)
5. Click **Next**
6. Click **Finish**

### 5. Enable Wake Timer

1. In Task Scheduler, find your task
2. Right-click → **Properties**
3. Go to **Conditions** tab
4. ✅ Check **"Wake the computer to run this task"**
5. Go to **Settings** tab
6. ✅ Check **"Run task as soon as possible after a scheduled start is missed"**
7. ✅ Check **"If the task fails, restart every: 5 minutes"**
8. Set **"Attempt to restart up to: 3 times"**
9. Click **OK**

---

## Testing

### Test 1: Run Task Manually

1. Open Task Scheduler
2. Find **"Naukri Resume Upload"**
3. Right-click → **Run**
4. Watch it execute and sleep

### Test 2: Test Wake from Sleep

1. Put PC to sleep manually
2. Wait 2 minutes
3. Manually wake PC
4. Check Task Scheduler history to verify wake timer works

### Test 3: Full Test (Optional)

1. Change task time to 2 minutes from now
2. Put PC to sleep
3. Wait and see if it wakes up automatically
4. Change time back to 6:30 AM

---

## Troubleshooting

### PC doesn't wake up

**Check Power Settings:**
```
Control Panel → Power Options → Change plan settings → 
Change advanced power settings → Sleep → Allow wake timers → Enable
```

**Check BIOS/UEFI:**
- Some PCs need "Wake on RTC" or "Wake on Timer" enabled in BIOS
- Restart PC → Press F2/Del to enter BIOS → Look for Wake settings

**Check Task Properties:**
- Task Scheduler → Your task → Properties → Conditions
- ✅ "Wake the computer to run this task" must be checked

### PC wakes but doesn't run task

**Check Task Settings:**
- Properties → General → "Run with highest privileges" ✅
- Properties → Settings → "Allow task to be run on demand" ✅

**Check Batch File:**
- Make sure `run-and-sleep.bat` path is correct
- Test by double-clicking the batch file

### PC doesn't go back to sleep

**Check Sleep Settings:**
```
Settings → System → Power & sleep → 
Additional power settings → Change plan settings → 
Put the computer to sleep: 15 minutes (or your preference)
```

**Or use Hibernate instead:**
Edit `run-and-sleep.bat` and change last line to:
```batch
shutdown /h
```

### Task runs but fails

**Check logs:**
- `logs/naukri-automation.log` in project folder
- Task Scheduler → History tab (enable if disabled)

**Check screenshots:**
- Look for `resume-upload-failure.png` in project folder

---

## Power Management Tips

### Minimize Power Usage

1. **Use Hibernate instead of Sleep:**
   - Edit `run-and-sleep.bat`
   - Change `rundll32.exe powrprof.dll,SetSuspendState 0,1,0`
   - To: `shutdown /h`

2. **Adjust sleep timeout:**
   - Settings → Power & sleep
   - Set to 5-10 minutes after task completes

3. **Use power saving mode:**
   - Settings → System → Power mode → Best power efficiency

### Estimated Costs

- **Sleep mode:** ~2-5W = ₹5-10/month
- **Wake + Run:** ~50W for 10 minutes = ₹30-40/month
- **Total:** ~₹35-50/month

---

## Maintenance

### Weekly
- Check Task Scheduler history for any failures
- Verify resume uploads on Naukri

### Monthly
- Check logs for errors
- Update project: `git pull`

---

## Disable/Remove

### Temporarily Disable
```powershell
Disable-ScheduledTask -TaskName "Naukri Resume Upload"
```

### Re-enable
```powershell
Enable-ScheduledTask -TaskName "Naukri Resume Upload"
```

### Permanently Remove
```powershell
Unregister-ScheduledTask -TaskName "Naukri Resume Upload" -Confirm:$false
```

Or use Task Scheduler GUI:
1. Open Task Scheduler
2. Find task → Right-click → Delete

---

## FAQ

**Q: Will this work if PC is shut down?**  
A: No, only works from Sleep/Hibernate. PC must be in sleep mode, not shut down.

**Q: What if I'm using the PC at 6:30 AM?**  
A: Task will run in background (headless mode). You won't see any browser window.

**Q: Can I change the time?**  
A: Yes, edit the task trigger in Task Scheduler or recreate the task with a different `-At` value.

**Q: Will this drain my laptop battery?**  
A: Minimal. ~10 minutes daily. Keep laptop plugged in at night for best results.

**Q: Can I run multiple times per day?**  
A: Not recommended. Naukri may flag frequent updates as suspicious. Once daily is optimal.

---

**Last Updated:** 2026-01-20  
**Version:** 1.0.0  
**Status:** ✅ Tested and Working
