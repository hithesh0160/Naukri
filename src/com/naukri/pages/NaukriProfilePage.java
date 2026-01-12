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
    }
    
    public void waitForHomePageLoad() {
        logger.info("Waiting for successful login");
        
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
        } catch (Exception e) {
            logger.warn("Could not verify login with standard checks, current title: {}", driver.getTitle());
            // Continue anyway as we're past the login page
        }
    }
    
    public void clickUploadResumeButton() {
        logger.info("Clicking upload resume button");
        WebElement uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(uploadResumeButton));
        uploadBtn.click();
    }
    
    public void uploadResume(File resumeFile) {
        logger.info("Uploading resume: {}", resumeFile.getAbsolutePath());
        WebElement attachCV = wait.until(ExpectedConditions.presenceOfElementLocated(attachCVInput));
        attachCV.sendKeys(resumeFile.getAbsolutePath());
    }
    
    public void uploadResumeComplete(File resumeFile) {
        navigateToProfile();
        clickUploadResumeButton();
        uploadResume(resumeFile);
    }
}
