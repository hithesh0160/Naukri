package com.naukri.testscript;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    }
    
    @Test
    public void testResumeUpload() {
        try {
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
            
            // Get next resume file to upload
            ResumeManager resumeManager = new ResumeManager();
            File resumeFile = resumeManager.getNextResumeFile();
            
            // Capture screenshot before upload attempt
            ScreenshotUtil.captureScreenshot(driver, "before-upload.png");
            
            // Upload resume
            profilePage.uploadResumeComplete(resumeFile);
            
            // Wait a bit for upload to process
            Thread.sleep(3000);
            
            // Capture success screenshot
            ScreenshotUtil.captureScreenshot(driver, "resume-upload-success.png");
            
            logger.info("=== Resume upload completed successfully ===");
            
            // Assert test passed
            Assert.assertTrue(true, "Resume uploaded successfully");
            
        } catch (Exception e) {
            logger.error("Test failed with error: {}", e.getMessage(), e);
            
            // Capture failure screenshot
            if (driver != null) {
                ScreenshotUtil.captureScreenshot(driver, "resume-upload-failure.png");
            }
            
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
