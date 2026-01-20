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

# Create project directory
echo ""
echo "Step 4: Creating project directory..."
mkdir -p ~/naukri-automation
cd ~/naukri-automation

# Check if project already exists
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
cd src/com/naukri/config

if [ ! -f "config.properties" ]; then
    echo "Creating config.properties from example..."
    cp config.properties.example config.properties
    echo ""
    echo "⚠️  IMPORTANT: Edit config.properties with your credentials!"
    echo "Run: nano ~/naukri-automation/Naukri/src/com/naukri/config/config.properties"
    echo ""
else
    echo "config.properties already exists"
fi

# Go back to project root
cd ~/naukri-automation/Naukri

# Create run script
echo ""
echo "Step 6: Creating run script..."
cat > run-naukri-android.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash

# Naukri Automation Run Script for Android
LOG_FILE=~/naukri-automation/naukri.log

echo "========================================" | tee -a $LOG_FILE
echo "Naukri Resume Upload - $(date)" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE

# Set headless mode
export HEADLESS=true

# Navigate to project directory
cd ~/naukri-automation/Naukri

# Run the automation
mvn clean test 2>&1 | tee -a $LOG_FILE

echo "" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE
echo "Execution completed at $(date)" | tee -a $LOG_FILE
echo "========================================" | tee -a $LOG_FILE
EOF

chmod +x run-naukri-android.sh

# Install cron for scheduling
echo ""
echo "Step 7: Installing cron for scheduling..."
pkg install -y cronie

# Create cron startup script
echo ""
echo "Step 8: Creating boot script..."
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-cron.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/bash
termux-wake-lock
crond
EOF

chmod +x ~/.termux/boot/start-cron.sh

# Start cron now
crond

echo ""
echo "=========================================="
echo "✅ Setup Complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo ""
echo "1. Edit your credentials:"
echo "   nano ~/naukri-automation/Naukri/src/com/naukri/config/config.properties"
echo ""
echo "2. Copy your resume files to:"
echo "   ~/naukri-automation/Naukri/data/"
echo "   Files needed:"
echo "   - Hithesh_SDET_Playwright_Java_Automation_Test_Engineer_Bangalore.pdf.pdf"
echo "   - Hithesh_SDET_Selenium_Java_Automation_Test_Engineer_Bangalore.pdf.pdf"
echo ""
echo "3. Test the automation:"
echo "   cd ~/naukri-automation/Naukri"
echo "   ./run-naukri-android.sh"
echo ""
echo "4. Schedule daily run at 6:30 AM:"
echo "   crontab -e"
echo "   Add line: 30 6 * * * ~/naukri-automation/Naukri/run-naukri-android.sh"
echo ""
echo "5. Install Termux:Boot from F-Droid for auto-start on phone reboot"
echo ""
echo "=========================================="
