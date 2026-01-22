@echo off
REM Script to remove sensitive data from git history
REM WARNING: This rewrites git history. Only run if you understand the implications.

echo ========================================
echo Remove Secrets from Git History
echo ========================================
echo.
echo This will:
echo 1. Remove Telegram tokens from run-naukri.bat history
echo 2. Remove Telegram tokens from run-and-sleep.bat history
echo 3. Remove config.properties from history
echo 4. Force push to remote (rewrites history)
echo.
echo WARNING: This rewrites git history!
echo Make sure you have a backup before proceeding.
echo.
pause

echo.
echo Step 1: Removing sensitive files from git cache...
git rm --cached run-naukri.bat
git rm --cached run-and-sleep.bat
git rm --cached src/com/naukri/config/config.properties

echo.
echo Step 2: Committing changes...
git add .gitignore
git add run-naukri.bat.example
git add run-and-sleep.bat.example
git add src/com/naukri/config/config.properties.example
git add src/com/naukri/util/TelegramNotifier.java
git commit -m "Security: Move credentials to config file and remove from git"

echo.
echo Step 3: Removing files from git history using filter-branch...
echo This may take a few minutes...
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch run-naukri.bat run-and-sleep.bat src/com/naukri/config/config.properties" --prune-empty --tag-name-filter cat -- --all

echo.
echo Step 4: Cleaning up...
git for-each-ref --format="delete %(refname)" refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo.
echo ========================================
echo Cleanup completed!
echo ========================================
echo.
echo Next steps:
echo 1. Review the changes: git log --oneline
echo 2. Force push to remote: git push origin --force --all
echo 3. Force push tags: git push origin --force --tags
echo.
echo WARNING: Force push will rewrite remote history!
echo Make sure all team members are aware and re-clone the repo.
echo.
pause
