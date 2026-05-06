# Security Guide

## Sensitive Data in This Project

Treat the following as secrets:

- `src/com/naukri/config/config.properties` (Naukri credentials and optional Telegram token/chat ID)
- Environment variables (`NAUKRI_USERNAME`, `NAUKRI_PASSWORD`, `TELEGRAM_TOKEN`, `TELEGRAM_CHAT_ID`)
- Screenshots and logs (may contain personal profile/account details)

Use `src/com/naukri/config/config.properties.example` as template and keep `config.properties` local only.

## Local Setup Checklist

1. Create `config.properties` from the example file.
2. Add credentials locally (never commit).
3. Ensure `run-naukri.bat` and `run-and-sleep.bat` do not contain real secrets.

## If Secrets Were Exposed

1. Rotate all exposed credentials immediately:
   - Change Naukri password
   - Revoke/regen Telegram bot token from `@BotFather`
2. Remove secrets from current files and commit the cleanup.
3. If already pushed, rewrite git history with a dedicated cleanup tool, then force-push carefully.
4. Notify collaborators to re-clone if history was rewritten.

Note: this repository does not include an automated history-cleanup script.

## Best Practices

✅ **DO:**
- Keep `config.properties` private
- Use strong, unique passwords
- Regularly update credentials
- Review `.gitignore` before committing
- Use environment variables for CI/CD

❌ **DON'T:**
- Commit `config.properties` to git
- Share your bot token publicly
- Use the same password across services
- Push to public repos without reviewing files

## Credential Storage Priority

The application reads credentials in this order:

1. **config.properties** (highest priority)
2. **Environment variables** (fallback)
3. **Batch file variables** (avoid storing secrets here)

## Reporting Security Issues

If you find a security vulnerability:
1. Do NOT create a public issue
2. Contact the repository owner directly
3. Provide details about the vulnerability
4. Wait for confirmation before disclosing

---

**Last Updated:** 2026-01-22  
**Version:** 1.0.0
