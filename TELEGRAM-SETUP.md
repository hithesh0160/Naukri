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

**Option 1: Config File (Recommended)**

Edit `src/com/naukri/config/config.properties`:
```properties
telegram.token=123456789:ABCdefGHIjklMNOpqrsTUVwxyz
telegram.chatid=123456789
```

**Option 2: Environment Variables**

1. Press `Win + R` → `sysdm.cpl` → Advanced → Environment Variables
2. Add User variables:
   - `TELEGRAM_TOKEN` = your bot token
   - `TELEGRAM_CHAT_ID` = your chat ID

**Note:** `config.properties` takes priority over environment variables.

### Step 5: Test It

```cmd
run-naukri.bat
```

You'll receive Telegram messages for each stage of the automation.

---

## Notification Types

### Resume Upload Success
```
✅ Naukri Resume Upload Successful

📄 Resume: [filename]
🕒 Time: [timestamp]
💻 Status: Completed successfully
```

### Auto-Apply Summary
```
*Naukri Auto-Apply Summary*
*Applied:* 7 jobs
*Time:* 13-05-2026 14:30:00

1. Wipro - SDET
2. Infosys - Automation Engineer
3. Amazon - QA SDET
...
```

After the resume upload, this summary lists every company you applied to.

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

1. **Bot token correct?** No spaces, full token from @BotFather
2. **Chat ID correct?** Just numbers from @userinfobot
3. **Started chat with bot?** Search bot → click Start
4. **Env vars set?** Close and reopen terminal, or restart PC

### Test manually
```cmd
set TELEGRAM_TOKEN=your_token_here
set TELEGRAM_CHAT_ID=your_chat_id_here
mvn clean test
```

## Disable Notifications

Remove from config or clear env vars. The automation still runs without notifications.

## Security Notes

- ✅ Bot token is like a password - keep it secret
- ✅ Never commit credentials to Git
- ✅ Only you can message your bot (it's private)
- ⚠️ Anyone with your bot token can send messages as your bot

---

**Last Updated:** 2026-05-13  
**Version:** 2.0.0
