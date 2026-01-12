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
    
    // Locators
    private final By loginLink = By.cssSelector("a[href*='login'], a.nI-gNb-lg-rg__login, a#login_Layer");
    private final By emailInput = By.xpath("//label[text()='Email ID / Username']/../input");
    private final By passwordInput = By.xpath("//input[@type='password']");
    private final By loginButton = By.xpath("//button[text()='Login']");
    private final By googleIframe = By.cssSelector("iframe[src*='accounts.google.com']");
    
    public NaukriLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }
    
    public void navigateToNaukri() {
        logger.info("Navigating to Naukri.com");
        driver.get("https://www.naukri.com");
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
        WebElement emailElement = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        emailElement.clear();
        emailElement.sendKeys(email);
    }
    
    public void enterPassword(String password) {
        logger.info("Entering password");
        WebElement passwordElement = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passwordElement.clear();
        passwordElement.sendKeys(password);
    }
    
    public void clickLoginButton() {
        logger.info("Clicking Login button");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginBtn.click();
    }
    
    public void login(String username, String password) {
        navigateToNaukri();
        clickLoginLink();
        enterEmail(username);
        enterPassword(password);
        clickLoginButton();
    }
}
