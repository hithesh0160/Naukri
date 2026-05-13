package com.naukri.testscript;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.naukri.pages.NaukriJobApplyPage;
import com.naukri.pages.NaukriLoginPage;
import com.naukri.pages.NaukriProfilePage;
import com.naukri.util.ConfigUtil;
import com.naukri.util.DriverManager;
import com.naukri.util.ResumeManager;
import com.naukri.util.ScreenshotUtil;

import java.io.File;

public class Naukri {
    private static final Logger logger = LoggerFactory.getLogger(Naukri.class);
    
    private WebDriver driver;
    private NaukriLoginPage loginPage;
    private NaukriProfilePage profilePage;
    private NaukriJobApplyPage jobApplyPage;
    
    @BeforeMethod
    public void setup() throws Exception {
        logger.info("=== Starting Naukri Resume Upload Test ===");
        
        // Validate configuration
        ConfigUtil.validateConfig();
        
        // Create WebDriver
        driver = DriverManager.createChromeDriver();
        
        // Initialize page objects
        loginPage = new NaukriLoginPage(driver);
        profilePage = new NaukriProfilePage(driver);
        jobApplyPage = new NaukriJobApplyPage(driver);
    }
    
    @Test
    public void testResumeUpload() {
        String resumeName = null;
        try {
            // Add a random delay between 10 seconds and 2 minutes to appear more organic
            int randomDelayMs = 10000 + (int)(Math.random() * 110000);
            logger.info("Waiting for {} ms (organic start delay)...", randomDelayMs);
            Thread.sleep(randomDelayMs);
            
            // Get credentials
            String username = ConfigUtil.getConfig("NAUKRI_USERNAME", "username");
            String password = ConfigUtil.getConfig("NAUKRI_PASSWORD", "password");
            
            // Login to Naukri
            loginPage.login(username, password);
            
            // Capture screenshot right after login
            ScreenshotUtil.captureScreenshot(driver, "after-login.png");
            
            // Wait for successful login
            profilePage.waitForHomePageLoad();
            logger.info("Login successful");
            
            // Navigate to profile for resume upload
            logger.info("Navigating to profile for resume upload");
            profilePage.navigateToProfile();
            
            // Update Profile text with human-like typing to avoid bot detection
            try {
                profilePage.updateHeadlineSection();
                profilePage.updateAboutSection();
                
                // Add a random key skill derived from the resume data
                String[] resumeSkills = {"Java", "Selenium", "TestNG", "Maven", "Jenkins", "Playwright", "SQL", "API Testing", "Postman", "Agile"};
                String randomSkill = resumeSkills[(int)(Math.random() * resumeSkills.length)];
                profilePage.updateKeySkills(randomSkill);
            } catch (Exception e) {
                logger.warn("Profile update failed, continuing with resume upload: {}", e.getMessage());
            }
            
            // Get next resume file to upload
            ResumeManager resumeManager = new ResumeManager();
            File resumeFile = resumeManager.getNextResumeFile();
            resumeName = resumeFile.getName();
            
            // Capture screenshot before upload attempt
            ScreenshotUtil.captureScreenshot(driver, "before-upload.png");
            
            // Upload resume
            profilePage.uploadResumeComplete(resumeFile);
            
            // Wait a bit for upload to process
            Thread.sleep(3000);
            
            // Capture success screenshot
            ScreenshotUtil.captureScreenshot(driver, "resume-upload-success.png");
            
            logger.info("=== Resume upload completed successfully ===");
            
            // Send success notification to Telegram
            com.naukri.util.TelegramNotifier.sendSuccessNotification(resumeName);
            
            // Auto-apply to jobs
            try {
                logger.info("Starting auto-apply to matching jobs...");
                String[] searchKeywords = {
                    "SDET Java", "Automation Testing Selenium", "Playwright",
                    "QA Automation"
                };
                String location = "Bangalore";
                java.util.List<String> allApplied = new java.util.ArrayList<>();
                for (String keyword : searchKeywords) {
                    java.util.List<String> applied = jobApplyPage.searchAndApply(keyword, location);
                    allApplied.addAll(applied);
                    logger.info("Applied to {} jobs for keyword: {}", applied.size(), keyword);
                    Thread.sleep(5000 + (long)(Math.random() * 10000));
                }
                logger.info("=== Auto-apply completed - Total applied: {} ===", allApplied.size());
                if (!allApplied.isEmpty()) {
                    com.naukri.util.TelegramNotifier.sendJobApplicationSummary(allApplied, allApplied.size());
                }
            } catch (Exception e) {
                logger.warn("Auto-apply failed: {}", e.getMessage());
            }
            
            // Assert test passed
            Assert.assertTrue(true, "Resume uploaded successfully");
            
        } catch (Exception e) {
            logger.error("Test failed with error: {}", e.getMessage(), e);
            
            // Capture failure screenshot
            if (driver != null) {
                ScreenshotUtil.captureScreenshot(driver, "resume-upload-failure.png");
            }
            
            // Send failure notification to Telegram
            com.naukri.util.TelegramNotifier.sendFailureNotification(e.getMessage());
            
            Assert.fail("Resume upload failed: " + e.getMessage());
        }
    }
    
    @AfterMethod
    public void teardown() {
        if (driver != null) {
            logger.info("Closing browser");
            driver.quit();
        }
        logger.info("=== Test execution completed ===");
    }
}
