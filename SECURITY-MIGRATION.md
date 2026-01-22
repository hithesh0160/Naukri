# Security Migration Guide

## What Changed?

We've moved sensitive credentials from batch files to a secure config file to prevent accidental exposure.

### Before (Insecure ❌)
```batch
# In run-and-sleep.bat
set TELEGRAM_TOKEN=8086512864:AAFaV0N-BlaWWIksESX-eN4ceUqmFfXtB7U
set TELEGRAM_CHAT_ID=504069398
```

### After (Secure ✅)
```properties
# In config.properties (excluded from git)
telegram.token=8086512864:AAFaV0N-BlaWWIksESX-eN4ceUqmFfXtB7U
telegram.chatid=504069398
```

## Migration Steps

### Step 1: Update Your Config File

Your `config.properties` already has the Telegram credentials. Verify:

```cmd
type src\com\naukri\config\config.properties
```

Should show:
```properties
username=hitesh_p16@yahoo.com
password=Jamesbond444@@

telegram.token=8086512864:AAFaV0N-BlaWWIksESX-eN4ceUqmFfXtB7U
telegram.chatid=504069398
```

### Step 2: Test the Changes

Run a test to ensure Telegram notifications still work:

```cmd
.\run-naukri.bat
```

You should receive a Telegram notification as before.

### Step 3: Clean Git History

**IMPORTANT:** Your Telegram token was exposed in git history. Follow these steps:

#### Option A: Quick Fix (Recommended)
1. Revoke the old bot token:
   - Open Telegram, search for @BotFather
   - Send `/mybots`
   - Select your bot
   - Select "API Token" → "Revoke current token"
   - Copy the new token

2. Update `config.properties` with the new token

3. Commit the security changes:
   ```cmd
   git add .gitignore SECURITY.md TELEGRAM-SETUP.md *.example
   git add src/com/naukri/util/TelegramNotifier.java
   git commit -m "Security: Move credentials to config file"
   git push
   ```

#### Option B: Clean History (Advanced)
If you want to remove the token from git history entirely:

1. **Backup your repo first!**

2. Run the cleanup script:
   ```cmd
   .\remove-secrets-from-history.bat
   ```

3. This will:
   - Remove sensitive files from git cache
   - Rewrite git history to remove tokens
   - Force push to remote (rewrites history)

4. **Warning:** This rewrites git history. Anyone else with the repo will need to re-clone.

### Step 4: Verify Security

Check that sensitive files are ignored:

```cmd
git status
```

Should NOT show:
- `config.properties`
- `run-naukri.bat` (if it has local changes)
- `run-and-sleep.bat` (if it has local changes)

## What's Protected Now?

✅ **Excluded from Git:**
- `config.properties` - All credentials
- `run-naukri.bat` - Local paths
- `run-and-sleep.bat` - Local paths
- `*.png` - Screenshots with personal info

✅ **Included in Git (Safe):**
- `config.properties.example` - Template without credentials
- `run-naukri.bat.example` - Template without paths
- `run-and-sleep.bat.example` - Template without paths

## Recommendation

**For maximum security, revoke your current Telegram bot token and create a new one:**

1. Open Telegram → @BotFather
2. Send `/mybots`
3. Select your bot
4. API Token → Revoke current token
5. Copy new token
6. Update `config.properties`
7. Test: `.\run-naukri.bat`

This ensures the exposed token can't be used by anyone who saw the git history.

---

**Questions?** Check `SECURITY.md` for more details.

**Last Updated:** 2026-01-22
