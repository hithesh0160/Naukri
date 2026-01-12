package com.naukri.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class NaukriProfilePage {
    private static final Logger logger = LoggerFactory.getLogger(NaukriProfilePage.class);
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators
    private final By uploadResumeButton = By.xpath("//input[@class='dummyUpload typ-14Bold']");
    private final By attachCVInput = By.id("attachCV");
    
    public NaukriProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }
    
    public void navigateToProfile() {
        logger.info("Navigating to profile page");
        driver.get("https://www.naukri.com/mnjuser/profile");
        
        // Wait for profile page to load
        try {
            Thread.sleep(3000);
            logger.info("Profile page loaded - URL: {}", driver.getCurrentUrl());
            logger.info("Profile page title: {}", driver.getTitle());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void waitForHomePageLoad() {
        logger.info("Waiting for successful login");
        
        try {
            // Wait longer for login to process
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Log current state
        logger.info("Current URL after login: {}", driver.getCurrentUrl());
        logger.info("Current title after login: {}", driver.getTitle());
        
        // Check if still on login page (login failed)
        if (driver.getCurrentUrl().contains("login.naukri.com")) {
            logger.error("Still on login page - login may have failed");
            logger.error("Possible reasons: incorrect credentials, bot detection, or CAPTCHA");
            throw new RuntimeException("Login failed - still on login page");
        }
        
        // Wait for URL to change from login page
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.naukri.com")));
        
        // Wait for either the title to contain "Naukri" or a logged-in element to appear
        try {
            // Try to wait for a common logged-in element (profile link, user menu, etc.)
            wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Naukri"),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'nI-gNb-drawer')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'profile')]"))
            ));
            logger.info("Login successful - Current page title: {}", driver.getTitle());
            logger.info("Current URL: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            logger.warn("Could not verify login with standard checks, current title: {}", driver.getTitle());
            logger.warn("Current URL: {}", driver.getCurrentUrl());
            
            // Check again if we're on login page
            if (driver.getCurrentUrl().contains("login")) {
                throw new RuntimeException("Login verification failed - appears to still be on login page");
            }
            // Continue anyway as we're past the login page
        }
    }
    
    public void clickUploadResumeButton() {
        logger.info("Clicking upload resume button");
        logger.info("Looking for element with xpath: //input[@class='dummyUpload typ-14Bold']");
        
        try {
            // Try multiple possible locators for the upload button
            WebElement uploadBtn = null;
            
            try {
                uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(uploadResumeButton));
                logger.info("Found upload button with primary locator");
            } catch (Exception e1) {
                logger.warn("Primary locator failed, trying alternative locators");
                
                // Try alternative locators
                By[] alternativeLocators = {
                    By.xpath("//button[contains(text(),'Update Resume')]"),
                    By.xpath("//a[contains(text(),'Update Resume')]"),
                    By.xpath("//div[contains(@class,'updateResume')]//input"),
                    By.cssSelector("input[type='file'][id*='CV']"),
                    By.cssSelector("input.dummyUpload")
                };
                
                for (By locator : alternativeLocators) {
                    try {
                        uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(locator));
                        logger.info("Found upload button with alternative locator: {}", locator);
                        break;
                    } catch (Exception e2) {
                        logger.debug("Alternative locator failed: {}", locator);
                    }
                }
            }
            
            if (uploadBtn != null) {
                uploadBtn.click();
                logger.info("Upload button clicked successfully");
            } else {
                throw new RuntimeException("Could not find upload resume button with any locator");
            }
            
        } catch (Exception e) {
            logger.error("Failed to click upload button. Current URL: {}", driver.getCurrentUrl());
            logger.error("Page source length: {}", driver.getPageSource().length());
            throw e;
        }
    }
    
    public void uploadResume(File resumeFile) {
        logger.info("Uploading resume: {}", resumeFile.getAbsolutePath());
        WebElement attachCV = wait.until(ExpectedConditions.presenceOfElementLocated(attachCVInput));
        attachCV.sendKeys(resumeFile.getAbsolutePath());
    }
    
    public void uploadResumeComplete(File resumeFile) {
        navigateToProfile();
        
        // Try direct file upload first (more reliable)
        try {
            logger.info("Attempting direct file upload");
            uploadResume(resumeFile);
            logger.info("Direct file upload successful");
        } catch (Exception e) {
            logger.warn("Direct upload failed, trying button click method");
            clickUploadResumeButton();
            uploadResume(resumeFile);
        }
    }
}
