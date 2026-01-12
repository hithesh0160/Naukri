# Configuration Directory

## Files

### config.properties (Not in Git)
Contains your Naukri credentials for local development. Create from example:

```bash
cp config.properties.example config.properties
```

Then edit with your credentials:
```properties
username=your_email@example.com
password=your_password
```

**Security:** This file is in `.gitignore` and will never be committed.

### config.properties.example
Template file showing the required format. Safe to commit.

### last_resume_uploaded.properties
Tracks which resume was uploaded last time. Automatically managed by the application.

## Configuration Priority

Configuration is loaded in this order:

### 1. config.properties file (Highest Priority - Local Development)
- Used for local development
- Easy to manage and edit
- Never committed to Git
- **Recommended for local testing**

### 2. Environment Variables (Fallback - CI/CD)
- Used when config.properties doesn't exist
- Required for GitHub Actions
- Set as GitHub Secrets
- **Recommended for CI/CD pipelines**

## Setup Instructions

### For Local Development (Recommended)

1. Create config.properties:
   ```bash
   cd src/com/naukri/config
   cp config.properties.example config.properties
   ```

2. Edit config.properties with your credentials:
   ```properties
   username=your_naukri_email@example.com
   password=your_naukri_password
   ```

3. Run tests:
   ```bash
   cd ../../..
   mvn test
   ```

### For GitHub Actions (CI/CD)

1. Go to GitHub repository Settings
2. Navigate to: **Secrets and variables** → **Actions**
3. Add repository secrets:
   - `NAUKRI_USERNAME`: Your Naukri email
   - `NAUKRI_PASSWORD`: Your Naukri password

4. GitHub Actions will automatically use these secrets

## How It Works

```
┌─────────────────────────────────────┐
│  Application starts                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Check for config.properties        │
└──────────────┬──────────────────────┘
               │
        ┌──────┴──────┐
        │             │
    Found         Not Found
        │             │
        ▼             ▼
┌──────────────┐  ┌──────────────────┐
│ Use file     │  │ Use environment  │
│ credentials  │  │ variables        │
└──────────────┘  └──────────────────┘
```

## Security Best Practices

✅ **DO:**
- Use config.properties for local development
- Use GitHub Secrets for CI/CD
- Keep config.properties in .gitignore
- Rotate passwords regularly

❌ **DON'T:**
- Commit config.properties to Git
- Share credentials in plain text
- Use same password everywhere
- Hardcode credentials in code
