# Telegram Notifications Setup

Get instant notifications on your phone when the Naukri automation runs!

## Setup (5 Minutes)

### Step 1: Create a Telegram Bot

1. Open Telegram app
2. Search for **@BotFather**
3. Send `/newbot`
4. Follow prompts:
   - Bot name: `Naukri Automation Bot` (or any name)
   - Username: `your_naukri_bot` (must end with 'bot')
5. **Copy the bot token** (looks like: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

### Step 2: Get Your Chat ID

1. Search for **@userinfobot** on Telegram
2. Send `/start`
3. **Copy your Chat ID** (looks like: `123456789`)

### Step 3: Start Chat with Your Bot

1. Search for your bot username (e.g., `@your_naukri_bot`)
2. Click **Start** or send `/start`
3. This allows the bot to send you messages

### Step 4: Configure Credentials

**Option 1: Config File (Recommended - More Secure)**

Edit `src/com/naukri/config/config.properties`:

```properties
# Telegram Notification Settings (optional)
telegram.token=123456789:ABCdefGHIjklMNOpqrsTUVwxyz
telegram.chatid=123456789
```

**Option 2: Environment Variables**

1. Press `Win + R`
2. Type: `sysdm.cpl` and press Enter
3. Go to **Advanced** tab → **Environment Variables**
4. Under **User variables**, click **New**:
   - Variable name: `TELEGRAM_TOKEN`
   - Variable value: `your_bot_token`
5. Click **New** again:
   - Variable name: `TELEGRAM_CHAT_ID`
   - Variable value: `your_chat_id`
6. Click **OK** to save

**Note:** Config file takes priority over environment variables.
### Step 5: Test It

Run the automation:
```cmd
.\run-and-sleep.bat
```

You should receive a Telegram message like:

```
✅ Naukri Resume Upload Successful

📄 Resume: Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf.pdf
🕒 Time: 20-01-2026 14:30:45
💻 Status: Completed successfully
```

---

## Notification Types

### Success Notification
```
✅ Naukri Resume Upload Successful

📄 Resume: [filename]
🕒 Time: [timestamp]
💻 Status: Completed successfully
```

### Failure Notification
```
❌ Naukri Resume Upload Failed

⚠️ Error: [error message]
🕒 Time: [timestamp]
💻 Check logs for details
```

---

## Troubleshooting

### Not receiving messages?

**Check 1: Bot token correct?**
- Make sure you copied the full token from @BotFather
- No spaces before/after the token

**Check 2: Chat ID correct?**
- Get it from @userinfobot
- Should be just numbers

**Check 3: Started chat with bot?**
- Search for your bot on Telegram
- Click Start button

**Check 4: Environment variables set?**
- Close and reopen PowerShell/Command Prompt
- Or restart your PC

### Test manually

Create a test file `test-telegram.bat`:
```batch
@echo off
set TELEGRAM_TOKEN=your_token_here
set TELEGRAM_CHAT_ID=your_chat_id_here
set HEADLESS=true

cd /d "%~dp0"
mvn test -Dtest=Naukri#testResumeUpload
```

---

## Disable Notifications

To disable Telegram notifications:

**Option 1:** Remove from config file:
```properties
telegram.token=
telegram.chatid=
```

**Option 2:** Remove environment variables:
- System Properties → Environment Variables
- Delete `TELEGRAM_TOKEN` and `TELEGRAM_CHAT_ID`

The automation will still work, just without notifications.

---

## Security Notes

- ✅ Bot token is like a password - keep it secret
- ✅ Credentials stored in `config.properties` (excluded from git)
- ✅ Never commit `config.properties` to Git
- ✅ Use `config.properties.example` as template
- ✅ Only you can message your bot (it's private)
- ⚠️ Anyone with your bot token can send messages as your bot

---

## Advanced: Group Notifications

Want notifications in a Telegram group?

1. Create a Telegram group
2. Add your bot to the group
3. Make bot an admin
4. Get group chat ID:
   - Send a message in the group
   - Visit: `https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates`
   - Look for `"chat":{"id":-123456789` (negative number)
   - Use this as TELEGRAM_CHAT_ID

---

**Last Updated:** 2026-01-20  
**Version:** 1.0.0
