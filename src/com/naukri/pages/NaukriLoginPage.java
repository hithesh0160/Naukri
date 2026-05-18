package com.naukri.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class NaukriLoginPage {
    private static final Logger logger = LoggerFactory.getLogger(NaukriLoginPage.class);
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Locators - Updated based on MCP/Playwright inspection of current Naukri login page
    // Key finding: The login page URL changed from login.naukri.com/nLogin/Login.php to www.naukri.com/nlogin/login
    // Key finding: There are NO label elements on the page anymore - labels were completely removed
    private final By loginLink = By.cssSelector("a[href*='login'], a.nI-gNb-lg-rg__login, a#login_Layer");
    
    // Email field found via Playwright inspection: <input type="text" id="usernameField" placeholder="Enter Email ID / Username">
    private final By emailInput = By.id("usernameField");
    private final By emailInputByPlaceholder = By.cssSelector("input[placeholder='Enter Email ID / Username']");
    
    // Password field found via Playwright inspection: <input type="password" id="passwordField" placeholder="Enter Password">
    private final By passwordInput = By.id("passwordField");
    
    // Login button: <button type="submit" class="...">Login</button>
    private final By loginButton = By.xpath("//button[text()='Login']");
    
    // OTP button alternative: <button type="submit">Use OTP to Login</button>
    private final By otpLoginButton = By.xpath("//button[contains(text(),'OTP')]");
    
    private final By googleIframe = By.cssSelector("iframe[src*='accounts.google.com']");
    
    public NaukriLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }
    
    public void navigateToNaukri() {
        logger.info("Navigating to Naukri login page");
        // Navigate to the correct current login URL (changed from login.naukri.com/nLogin/Login.php)
        driver.get("https://www.naukri.com/nlogin/login");
    }
    
    public void clickLoginLink() {
        logger.info("Clicking Login link");
        
        try {
            // Wait for page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(loginLink));
            
            // Check if Google Sign-In iframe is present and hide it
            try {
                WebElement iframe = driver.findElement(googleIframe);
                logger.info("Google Sign-In iframe detected, hiding it");
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", iframe);
                Thread.sleep(500); // Brief pause after hiding
            } catch (Exception e) {
                logger.debug("No Google Sign-In iframe found or already hidden");
            }
            
            // Try to click the login link
            try {
                WebElement loginElement = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
                loginElement.click();
                logger.info("Login link clicked successfully");
            } catch (Exception e) {
                // If regular click fails, use JavaScript click
                logger.warn("Regular click failed, trying JavaScript click");
                WebElement loginElement = driver.findElement(loginLink);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginElement);
                logger.info("Login link clicked using JavaScript");
            }
        } catch (Exception e) {
            logger.error("Failed to find or click login link. Current URL: {}", driver.getCurrentUrl());
            logger.error("Page title: {}", driver.getTitle());
            throw e;
        }
    }
    
    public void enterEmail(String email) {
        logger.info("Entering email: {}", email);
        
        // Check if we're already on the direct login page (URL contains nlogin/login)
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("nlogin") || currentUrl.contains("login")) {
            logger.info("Already on login page form: {}", currentUrl);
        }
        
        // Try id-based locator first (more reliable), fallback to placeholder
        WebElement emailElement = null;
        try {
            emailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        } catch (Exception e) {
            logger.warn("Could not find email field by id, trying placeholder locator: {}", e.getMessage());
            try {
                emailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInputByPlaceholder));
            } catch (Exception e2) {
                logger.warn("Could not find by placeholder either, trying generic text input: {}", e2.getMessage());
                // Last resort: find the first visible text input that's not the search bar
                java.util.List<WebElement> textInputs = driver.findElements(By.cssSelector("input[type='text']"));
                for (WebElement input : textInputs) {
                    if (input.isDisplayed() && !input.getAttribute("placeholder").contains("keyword")
                        && !input.getAttribute("placeholder").contains("location")) {
                        emailElement = input;
                        break;
                    }
                }
                if (emailElement == null) {
                    // Fall back to the first text input
                    emailElement = driver.findElements(By.cssSelector("input[type='text']")).get(0);
                }
            }
        }
        
        emailElement.clear();
        emailElement.sendKeys(email);
        logger.info("Email entered successfully");
    }
    
    public void enterPassword(String password) {
        logger.info("Entering password");
        WebElement passwordElement = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passwordElement.clear();
        passwordElement.sendKeys(password);
        logger.info("Password entered successfully");
    }
    
    public void clickLoginButton() {
        logger.info("Clicking Login button");
        
        // Small delay before clicking
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        
        try {
            loginBtn.click();
            logger.info("Login button clicked successfully");
        } catch (Exception e) {
            logger.warn("Regular click failed, trying JavaScript click");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
            logger.info("Login button clicked using JavaScript");
        }
    }
    
    public void login(String username, String password) {
        navigateToNaukri();
        
        // Add delay to let page fully load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check if the page already has the login form visible (new URL has form directly)
        String currentUrl = driver.getCurrentUrl();
        logger.info("Current URL: {}", currentUrl);
        
        // If we're on the main naukri.com page (not login page), click the login link
        if (currentUrl.contains("naukri.com") && !currentUrl.contains("nlogin") && !currentUrl.contains("login")) {
            logger.info("On main Naukri page, clicking login link");
            try {
                clickLoginLink();
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.warn("Could not click login link: {}", e.getMessage());
            }
        }
        
        enterEmail(username);
        
        // Small delay between email and password
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        enterPassword(password);
        
        // Delay before clicking login button
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clickLoginButton();
        
        logger.info("Login form submitted, waiting for response");
    }
}