#!/data/data/com.termux/files/usr/bin/bash

# Naukri Automation - Termux Setup Script
# Run this script on your Android device in Termux

echo "=========================================="
echo "Naukri Automation - Termux Setup"
echo "=========================================="
echo ""

# Update packages
echo "Step 1: Updating Termux packages..."
pkg update -y && pkg upgrade -y

# Install required packages
echo ""
echo "Step 2: Installing Java, Maven, Git, and Firefox..."
pkg install -y openjdk-17 maven git firefox geckodriver

# Setup storage access
echo ""
echo "Step 3: Setting up storage access..."
termux-setup-storage

# Wait for user to grant permission
echo "Please grant storage permission in the popup..."
sleep 5

# Clone project directly to home
echo ""
echo "Step 4: Cloning project..."
cd ~

if [ -d "Naukri" ]; then
    echo "Project directory already exists. Pulling latest changes..."
    cd Naukri
    git pull
else
    echo "Cloning Naukri repository..."
    git clone https://github.com/hithesh0160/Naukri.git
    cd Naukri
fi

# Setup configuration
echo ""
echo "Step 5: Setting up configuration..."
cd ~/Naukri/src/com/naukri/config

if [ ! -f "config.properties" ]; then
    echo "Creating config.properties from example..."
    cp config.properties.example config.properties
fi

# Create run script
echo ""
echo "Step 6: Creating run script..."
cd ~/Naukri
cat > run-naukri-android.sh << 'EOFSCRIPT'
#!/data/data/com.termux/files/usr/bin/bash

LOG_FILE=~/naukri.log

echo "========================================" | tee -a $LOG_FILE
echo "Naukri Resume Upload - $(date)" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE

# Set headless mode
export HEADLESS=true

# Navigate to project directory
cd ~/Naukri

# Run the automation
mvn clean test 2>&1 | tee -a $LOG_FILE

echo "" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE
echo "Execution completed at $(date)" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE
EOFSCRIPT

chmod +x run-naukri-android.sh

# Install cron for scheduling
echo ""
echo "Step 7: Installing cron for scheduling..."
pkg install -y cronie

# Create cron startup script
echo ""
echo "Step 8: Creating boot script..."
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-cron.sh << 'EOFBOOT'
#!/data/data/com.termux/files/usr/bin/bash
termux-wake-lock
crond
EOFBOOT

chmod +x ~/.termux/boot/start-cron.sh

# Start cron now
crond

# Create helper script for editing config
cat > ~/edit-config.sh << 'EOFCONFIG'
#!/data/data/com.termux/files/usr/bin/bash
nano ~/Naukri/src/com/naukri/config/config.properties
EOFCONFIG

chmod +x ~/edit-config.sh

# Create helper script for copying resumes
cat > ~/copy-resumes.sh << 'EOFRESUME'
#!/data/data/com.termux/files/usr/bin/bash
echo "Copying resume files from Downloads..."
cp ~/storage/downloads/*.pdf ~/Naukri/data/ 2>/dev/null
echo "Files in data folder:"
ls -lh ~/Naukri/data/
EOFRESUME

chmod +x ~/copy-resumes.sh

# Create helper script for scheduling
cat > ~/schedule-job.sh << 'EOFCRON'
#!/data/data/com.termux/files/usr/bin/bash
echo "Adding cron job for 6:30 AM daily..."
(crontab -l 2>/dev/null; echo "30 6 * * * ~/Naukri/run-naukri-android.sh") | crontab -
echo "Cron job added!"
echo "Current cron jobs:"
crontab -l
EOFCRON

chmod +x ~/schedule-job.sh

echo ""
echo "=========================================="
echo "✅ Setup Complete!"
echo "=========================================="
echo ""
echo "📝 NEXT STEPS (Easy!):"
echo ""
echo "1️⃣  Edit credentials (opens nano editor):"
echo "   ~/edit-config.sh"
echo ""
echo "2️⃣  Copy resume PDFs to Downloads folder on your phone"
echo "   Then run:"
echo "   ~/copy-resumes.sh"
echo ""
echo "3️⃣  Test the automation:"
echo "   ~/Naukri/run-naukri-android.sh"
echo ""
echo "4️⃣  Schedule daily run at 6:30 AM:"
echo "   ~/schedule-job.sh"
echo ""
echo "5️⃣  Install Termux:Boot from F-Droid for auto-start"
echo ""
echo "=========================================="
echo ""
echo "💡 TIP: All commands are in your home directory (~)"
echo "Just type: ls ~/*.sh to see all helper scripts"
echo ""
