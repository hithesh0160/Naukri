# Security Guide

## Sensitive Files

The following files contain sensitive information and are excluded from git:

### 1. Configuration Files
- `src/com/naukri/config/config.properties` - Contains Naukri credentials and Telegram tokens
- Use `config.properties.example` as a template

### 2. Batch Files (Local Only)
- `run-naukri.bat` - May contain local paths
- `run-and-sleep.bat` - May contain local paths
- Use `.example` versions as templates

### 3. Screenshots
- `*.png` - May contain personal information from profile

## Setup for New Users

1. **Copy example files:**
   ```cmd
   copy src\com\naukri\config\config.properties.example src\com\naukri\config\config.properties
   copy run-naukri.bat.example run-naukri.bat
   copy run-and-sleep.bat.example run-and-sleep.bat
   ```

2. **Edit config.properties:**
   - Add your Naukri username and password
   - Add your Telegram bot token and chat ID (optional)

3. **Edit batch files:**
   - Update Maven path to match your installation
   - Adjust other settings as needed

## Removing Secrets from Git History

If you accidentally committed sensitive data:

1. **Run the cleanup script:**
   ```cmd
   .\remove-secrets-from-history.bat
   ```

2. **Force push to remote:**
   ```cmd
   git push origin --force --all
   git push origin --force --tags
   ```

3. **Revoke compromised credentials:**
   - Change your Naukri password
   - Create a new Telegram bot (revoke old token via @BotFather)

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
3. **Batch file variables** (deprecated, not recommended)

## Reporting Security Issues

If you find a security vulnerability:
1. Do NOT create a public issue
2. Contact the repository owner directly
3. Provide details about the vulnerability
4. Wait for confirmation before disclosing

---

**Last Updated:** 2026-01-22  
**Version:** 1.0.0
