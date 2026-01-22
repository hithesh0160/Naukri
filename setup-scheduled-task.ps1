# Naukri Automation - Windows Scheduled Task Setup with Wake Timer
# Run this script as Administrator

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Naukri Automation - Scheduled Task Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "ERROR: This script must be run as Administrator!" -ForegroundColor Red
    Write-Host "Right-click PowerShell and select 'Run as Administrator'" -ForegroundColor Yellow
    pause
    exit
}

# Get current directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$batFile = Join-Path $scriptPath "run-and-sleep.bat"

if (-not (Test-Path $batFile)) {
    Write-Host "ERROR: run-and-sleep.bat not found in $scriptPath" -ForegroundColor Red
    pause
    exit
}

Write-Host "Script location: $batFile" -ForegroundColor Green
Write-Host ""

# Task configuration
$taskName = "Naukri Resume Upload"
$taskDescription = "Automatically updates Naukri profile daily at 6:30 AM"
$taskTime = "06:30"  # 6:30 AM

Write-Host "Creating scheduled task..." -ForegroundColor Yellow
Write-Host "Task Name: $taskName" -ForegroundColor White
Write-Host "Run Time: $taskTime daily" -ForegroundColor White
Write-Host "Action: Run automation and sleep" -ForegroundColor White
Write-Host ""

# Remove existing task if it exists
$existingTask = Get-ScheduledTask -TaskName $taskName -ErrorAction SilentlyContinue
if ($existingTask) {
    Write-Host "Removing existing task..." -ForegroundColor Yellow
    Unregister-ScheduledTask -TaskName $taskName -Confirm:$false
}

# Create action
$action = New-ScheduledTaskAction -Execute $batFile -WorkingDirectory $scriptPath

# Create trigger (daily at 6:30 AM)
$trigger = New-ScheduledTaskTrigger -Daily -At $taskTime

# Create settings with wake timer
$settings = New-ScheduledTaskSettingsSet `
    -AllowStartIfOnBatteries `
    -DontStopIfGoingOnBatteries `
    -StartWhenAvailable `
    -WakeToRun `
    -ExecutionTimeLimit (New-TimeSpan -Hours 1) `
    -RestartCount 3 `
    -RestartInterval (New-TimeSpan -Minutes 5)

# Create principal (run with highest privileges)
$principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest

# Register the task
Register-ScheduledTask `
    -TaskName $taskName `
    -Description $taskDescription `
    -Action $action `
    -Trigger $trigger `
    -Settings $settings `
    -Principal $principal

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "✓ Scheduled Task Created Successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Task Details:" -ForegroundColor Cyan
Write-Host "  • Runs daily at: $taskTime" -ForegroundColor White
Write-Host "  • Will wake PC from sleep" -ForegroundColor White
Write-Host "  • Runs in headless mode (no browser window)" -ForegroundColor White
Write-Host "  • Automatically sleeps after completion" -ForegroundColor White
Write-Host ""
Write-Host "To view/edit the task:" -ForegroundColor Yellow
Write-Host "  1. Open Task Scheduler (taskschd.msc)" -ForegroundColor White
Write-Host "  2. Look for '$taskName'" -ForegroundColor White
Write-Host ""
Write-Host "To test the task now:" -ForegroundColor Yellow
Write-Host "  Start-ScheduledTask -TaskName '$taskName'" -ForegroundColor White
Write-Host ""
Write-Host "To disable the task:" -ForegroundColor Yellow
Write-Host "  Disable-ScheduledTask -TaskName '$taskName'" -ForegroundColor White
Write-Host ""
Write-Host "To remove the task:" -ForegroundColor Yellow
Write-Host "  Unregister-ScheduledTask -TaskName '$taskName' -Confirm:`$false" -ForegroundColor White
Write-Host ""

# Test if wake timer is supported
Write-Host "Checking wake timer support..." -ForegroundColor Yellow
$wakeSupport = powercfg /devicequery wake_armed

if ($wakeSupport) {
    Write-Host "✓ Wake timers are supported on this PC" -ForegroundColor Green
} else {
    Write-Host "⚠ Wake timers may not be supported" -ForegroundColor Yellow
    Write-Host "  Check Power Options → Advanced → Sleep → Allow wake timers" -ForegroundColor White
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
