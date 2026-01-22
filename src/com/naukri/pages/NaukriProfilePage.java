package com.naukri.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
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
    
    // About section locators
    private final By aboutEditIcon = By.xpath("//span[contains(text(),'Profile summary')]//following::span[@class='edit icon']");
    private final By aboutTextArea = By.xpath("//textarea[contains(@id,'profileSummary') or contains(@class,'profileSummary') or @name='summary']");
    private final By aboutSaveButton = By.xpath("//button[contains(@class,'saveBtn') or contains(text(),'Save') or @type='submit']");
    
    // Alternative: Headline section locators
    private final By headlineEditIcon = By.xpath("//span[contains(text(),'Resume headline')]//following::span[@class='edit icon']");
    private final By headlineTextArea = By.xpath("//textarea[@id='resumeHeadlineTxt']");
    private final By headlineSaveButton = By.xpath("//button[contains(@class,'saveBtn') or contains(text(),'Save')]");
    
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
    
    /**
     * Updates the About/Profile Summary section by adding a space or minor change
     * This helps move the profile to the top of recruiter searches
     */
    public void updateAboutSection() {
        logger.info("Updating About/Profile Summary section");
        
        try {
            // Wait for profile page to be ready
            Thread.sleep(2000);
            
            // Close any overlays or popups that might be blocking
            try {
                logger.info("Checking for overlays/popups to close");
                // Try to close any modal/overlay
                driver.findElements(By.xpath("//div[contains(@class,'crossIcon')]")).forEach(element -> {
                    try {
                        element.click();
                        logger.info("Closed an overlay");
                    } catch (Exception e) {
                        // Ignore
                    }
                });
                
                // Try ESC key to close any popups
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.debug("No overlays to close or already closed");
            }
            
            // Find and click the edit icon for About section
            logger.info("Looking for About section edit button");
            WebElement editIcon = wait.until(ExpectedConditions.presenceOfElementLocated(aboutEditIcon));
            
            // Scroll to the element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", editIcon);
            Thread.sleep(500);
            
            // Try regular click first
            try {
                editIcon.click();
                logger.info("Clicked About section edit button (regular click)");
            } catch (Exception e) {
                // If regular click fails, use JavaScript click
                logger.info("Regular click failed, trying JavaScript click");
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", editIcon);
                logger.info("Clicked About section edit button (JavaScript click)");
            }
            
            // Wait for text area to appear
            Thread.sleep(1500);
            
            // Find the text area (using generic locator that works)
            logger.info("Looking for text area after clicking edit");
            WebElement textArea = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea")));
            
            if (!textArea.isDisplayed()) {
                throw new RuntimeException("Text area found but not visible");
            }
            
            // Get current text
            String currentText = textArea.getAttribute("value");
            if (currentText == null || currentText.isEmpty()) {
                currentText = textArea.getText();
            }
            logger.info("Current About text length: {} characters", currentText != null ? currentText.length() : 0);
            
            // Make a substantial change that Naukri will accept
            // Strategy: Add/remove a marker phrase at the end
            if (currentText != null && !currentText.isEmpty()) {
                String updatedText;
                String marker1 = " #OpenToWork";
                String marker2 = " #Hiring";
                
                // Check which marker is currently present and alternate
                if (currentText.trim().endsWith(marker1)) {
                    // Remove marker1, add marker2
                    updatedText = currentText.trim().substring(0, currentText.trim().length() - marker1.length()) + marker2;
                    logger.info("Changing About section: Replacing '{}' with '{}'", marker1, marker2);
                } else if (currentText.trim().endsWith(marker2)) {
                    // Remove marker2, back to original (no marker)
                    updatedText = currentText.trim().substring(0, currentText.trim().length() - marker2.length());
                    logger.info("Changing About section: Removing '{}'", marker2);
                } else {
                    // Add marker1
                    updatedText = currentText.trim() + marker1;
                    logger.info("Changing About section: Adding '{}'", marker1);
                }
                
                logger.info("Original text (last 50 chars): ...{}", currentText.substring(Math.max(0, currentText.length() - 50)));
                logger.info("Updated text (last 50 chars): ...{}", updatedText.substring(Math.max(0, updatedText.length() - 50)));
                
                // Clear and update the text
                textArea.clear();
                Thread.sleep(500);
                textArea.sendKeys(updatedText);
                logger.info("Text entered into About section");
                
                // Wait a bit before saving
                Thread.sleep(1000);
                
                // Click save button
                logger.info("Looking for Save button");
                WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@type='submit']")));
                
                // Scroll to save button
                js.executeScript("arguments[0].scrollIntoView(true);", saveButton);
                Thread.sleep(500);
                
                // Use JavaScript click (more reliable)
                js.executeScript("arguments[0].click();", saveButton);
                logger.info("Clicked Save button");
                
                // Wait for save to complete
                Thread.sleep(3000);
                
                logger.info("About section update completed");
            } else {
                logger.warn("About section is empty, skipping update");
            }
            
        } catch (Exception e) {
            logger.error("Failed to update About section: {}", e.getMessage());
            logger.warn("Trying alternative: updating Resume Headline instead");
            
            // Try updating headline as fallback
            try {
                updateHeadlineSection();
            } catch (Exception e2) {
                logger.error("Failed to update Headline section as well: {}", e2.getMessage());
                logger.warn("Continuing with resume upload despite profile update failure");
            }
        }
    }
    
    /**
     * Updates the Resume Headline section to boost profile visibility
     * This helps move the profile to the top of recruiter searches
     */
    public void updateHeadlineSection() {
        logger.info("Updating Resume Headline section");
        
        try {
            // Wait for profile page to be ready
            Thread.sleep(2000);
            
            // Close any overlays
            try {
                logger.info("Checking for overlays/popups to close");
                driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.debug("No overlays to close");
            }
            
            // Find and click the edit icon for Headline section
            logger.info("Looking for Headline section edit button");
            WebElement editIcon = wait.until(ExpectedConditions.presenceOfElementLocated(headlineEditIcon));
            
            // Scroll to element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", editIcon);
            Thread.sleep(500);
            
            // Click with JavaScript (more reliable)
            js.executeScript("arguments[0].click();", editIcon);
            logger.info("Clicked Headline section edit button");
            
            // Wait for text area to appear
            Thread.sleep(1500);
            
            // Find the text area
            WebElement textArea = wait.until(ExpectedConditions.presenceOfElementLocated(headlineTextArea));
            String currentText = textArea.getAttribute("value");
            if (currentText == null || currentText.isEmpty()) {
                currentText = textArea.getText();
            }
            logger.info("Current Headline text length: {} characters", currentText != null ? currentText.length() : 0);
            
            if (currentText != null && !currentText.isEmpty()) {
                String updatedText;
                String marker1 = " | Open to Work";
                String marker2 = " | Actively Looking";
                
                // Check which marker is currently present and alternate
                if (currentText.trim().endsWith(marker1)) {
                    // Remove marker1, add marker2
                    updatedText = currentText.trim().substring(0, currentText.trim().length() - marker1.length()) + marker2;
                    logger.info("Changing Headline: Replacing '{}' with '{}'", marker1, marker2);
                } else if (currentText.trim().endsWith(marker2)) {
                    // Remove marker2, back to original (no marker)
                    updatedText = currentText.trim().substring(0, currentText.trim().length() - marker2.length());
                    logger.info("Changing Headline: Removing '{}'", marker2);
                } else {
                    // Add marker1
                    updatedText = currentText.trim() + marker1;
                    logger.info("Changing Headline: Adding '{}'", marker1);
                }
                
                logger.info("Original text (last 50 chars): ...{}", currentText.substring(Math.max(0, currentText.length() - 50)));
                logger.info("Updated text (last 50 chars): ...{}", updatedText.substring(Math.max(0, updatedText.length() - 50)));
                
                // Clear and update the text
                textArea.clear();
                Thread.sleep(500);
                textArea.sendKeys(updatedText);
                logger.info("Text entered into Headline section");
                
                // Wait before saving
                Thread.sleep(1000);
                
                // Click save button
                logger.info("Looking for Save button");
                WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(headlineSaveButton));
                
                // Scroll and click with JavaScript
                js.executeScript("arguments[0].scrollIntoView(true);", saveButton);
                Thread.sleep(500);
                js.executeScript("arguments[0].click();", saveButton);
                logger.info("Clicked Save button for Headline section");
                
                // Wait for save to complete
                Thread.sleep(3000);
                
                // Verify the save was successful
                logger.info("Verifying Headline was saved...");
                driver.navigate().refresh();
                Thread.sleep(2000);
                
                try {
                    // Click edit again to verify
                    WebElement editIconVerify = driver.findElement(headlineEditIcon);
                    js.executeScript("arguments[0].scrollIntoView(true);", editIconVerify);
                    Thread.sleep(500);
                    js.executeScript("arguments[0].click();", editIconVerify);
                    Thread.sleep(1500);
                    
                    WebElement textAreaVerify = driver.findElement(headlineTextArea);
                    String verifyText = textAreaVerify.getAttribute("value");
                    if (verifyText == null || verifyText.isEmpty()) {
                        verifyText = textAreaVerify.getText();
                    }
                    
                    logger.info("Verification - Current text (last 50 chars): ...{}", 
                        verifyText.substring(Math.max(0, verifyText.length() - 50)));
                    
                    if (verifyText.equals(updatedText)) {
                        logger.info("✅ Headline update VERIFIED - Change was saved successfully!");
                    } else {
                        logger.warn("⚠️ Headline update NOT saved - Text reverted to original");
                        logger.warn("Expected: ...{}", updatedText.substring(Math.max(0, updatedText.length() - 50)));
                        logger.warn("Got: ...{}", verifyText.substring(Math.max(0, verifyText.length() - 50)));
                    }
                    
                    // Close edit mode
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
                    Thread.sleep(1000);
                    
                } catch (Exception e) {
                    logger.warn("Could not verify Headline update: {}", e.getMessage());
                }
                
                logger.info("Headline section update completed");
                
            } else {
                logger.warn("Headline section is empty, skipping update");
            }
            
        } catch (Exception e) {
            logger.error("Failed to update Headline section: {}", e.getMessage());
            logger.warn("Continuing with resume upload despite Headline update failure");
        }
    }
}
