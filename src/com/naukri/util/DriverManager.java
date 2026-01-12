package com.naukri.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    
    public static WebDriver createChromeDriver() throws IOException {
        // Check if running in CI/CD environment (GitHub Actions)
        String ciEnv = System.getenv("CI");
        boolean isCI = "true".equalsIgnoreCase(ciEnv);
        
        if (isCI) {
            logger.info("CI environment detected - using Chrome");
            return createChrome();
        } else {
            logger.info("Local environment detected - using Firefox");
            return createFirefox();
        }
    }
    
    private static WebDriver createChrome() throws IOException {
        logger.info("Setting up ChromeDriver using WebDriverManager");
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless=new");  // Always headless in CI
        
        // Custom user agent (optional - uncomment if needed)
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        Path userDataDir = Files.createTempDirectory("chrome-user-data-");
        options.addArguments("--user-data-dir=" + userDataDir.toString());
        
        logger.info("Chrome options configured (headless mode for CI)");
        
        WebDriver driver = new ChromeDriver(options);
        logger.info("Chrome WebDriver created successfully");
        
        return driver;
    }
    
    private static WebDriver createFirefox() {
        logger.info("Setting up FirefoxDriver using WebDriverManager");
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        // options.addArguments("--width=1920");
        // options.addArguments("--height=1080");
        
        // Headless mode - uncomment for headless execution
        options.addArguments("--headless");
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        logger.info("Firefox options configured (visible browser for local testing)");
        
        WebDriver driver = new FirefoxDriver(options);
        logger.info("Firefox WebDriver created successfully");
        
        return driver;
    }
}
