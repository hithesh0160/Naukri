package com.naukri.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        logger.info("Setting up ChromeDriver");
        
        ChromeOptions options = new ChromeOptions();
        
        // Make browser look more like a real user to avoid bot detection
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--headless=new");
        
        // Anti-detection measures
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        // Set a realistic user agent
        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        Path userDataDir = Files.createTempDirectory("chrome-user-data-");
        options.addArguments("--user-data-dir=" + userDataDir.toString());
        
        logger.info("Chrome options configured (headless mode for CI with anti-detection)");
        
        // In CI environment, explicitly create ChromeDriverService with driver path
        String ciEnv = System.getenv("CI");
        WebDriver driver;
        
        if ("true".equalsIgnoreCase(ciEnv)) {
            File driverFile = new File("/usr/local/bin/chromedriver");
            if (driverFile.exists()) {
                logger.info("Using ChromeDriver at: {}", driverFile.getAbsolutePath());
                ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(driverFile)
                    .usingAnyFreePort()
                    .build();
                driver = new ChromeDriver(service, options);
            } else {
                logger.warn("ChromeDriver not found at /usr/local/bin/chromedriver, using default");
                driver = new ChromeDriver(options);
            }
        } else {
            driver = new ChromeDriver(options);
        }
        
        // Execute CDP commands to further hide automation
        if ("true".equalsIgnoreCase(ciEnv)) {
            ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", 
                java.util.Map.of("source", 
                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"));
        }
        
        logger.info("Chrome WebDriver created successfully");
        
        return driver;
    }
    
    private static WebDriver createFirefox() {
        logger.info("Setting up FirefoxDriver");
        
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        // Headless mode - uncomment for headless execution
        // options.addArguments("--headless");
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        logger.info("Firefox options configured (visible browser for local testing)");
        
        WebDriver driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        logger.info("Firefox WebDriver created successfully");
        
        return driver;
    }
}

