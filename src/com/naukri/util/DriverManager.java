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
        }
        
        // Local: use BROWSER env var to choose, default to Chrome (better anti-detection)
        String browser = System.getenv("BROWSER");
        if ("firefox".equalsIgnoreCase(browser)) {
            logger.info("Local environment detected - using Firefox (BROWSER=firefox)");
            return createFirefox();
        }
        
        logger.info("Local environment detected - using Chrome");
        return createChrome();
    }
    
    private static WebDriver createChrome() throws IOException {
        logger.info("Setting up ChromeDriver");
        
        // Disable Selenium Manager explicitly
        System.setProperty("se:manager:disable", "true");
        
        // Set ChromeDriver path explicitly to bypass Selenium Manager
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            logger.info("Set webdriver.chrome.driver to: {}", chromeDriverPath);
        }
        
        ChromeOptions options = new ChromeOptions();
        
        // Make browser look more like a real user to avoid bot detection
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        
        // Android/Termux specific arguments for Chromium
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        options.addArguments("--disable-translate");
        options.addArguments("--hide-scrollbars");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--mute-audio");
        options.addArguments("--no-first-run");
        options.addArguments("--safebrowsing-disable-auto-update");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-logging");
        
        // Respect HEADLESS env variable (default to headless for CI)
        String headlessMode = System.getenv("HEADLESS");
        boolean isHeadless = !"false".equalsIgnoreCase(headlessMode);
        if (isHeadless) {
            options.addArguments("--headless=new");
            logger.info("Chrome running in HEADLESS mode");
        } else {
            logger.info("Chrome running in VISIBLE mode");
        }
        
        // Anti-detection measures
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        // Additional anti-detection arguments
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=IsolateOrigins,site-per-process");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-save-password-bubble");
        
        // Set a realistic user agent
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");
        
        // Set additional preferences to appear more human
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        Path userDataDir = Files.createTempDirectory("chrome-user-data-");
        options.addArguments("--user-data-dir=" + userDataDir.toString());
        
        logger.info("Chrome options configured with anti-detection");
        
        // Check for explicit ChromeDriver path (for Termux/Android or custom setups)
        String ciEnv = System.getenv("CI");
        WebDriver driver;
        
        if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
            File driverFile = new File(chromeDriverPath);
            if (driverFile.exists()) {
                logger.info("Using ChromeDriver at: {}", driverFile.getAbsolutePath());
                ChromeDriverService service = new ChromeDriverService.Builder()
                    .usingDriverExecutable(driverFile)
                    .usingAnyFreePort()
                    .build();
                driver = new ChromeDriver(service, options);
            } else {
                logger.warn("ChromeDriver not found at {}, using default", chromeDriverPath);
                driver = new ChromeDriver(options);
            }
        } else if ("true".equalsIgnoreCase(ciEnv)) {
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
        ChromeDriver chromeDriver = (ChromeDriver) driver;
        
        // Hide webdriver property
        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", 
            java.util.Map.of("source", 
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"));
        
        // Override other automation indicators
        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", 
            java.util.Map.of("source", 
                "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});" +
                "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});"));
        
        logger.info("Chrome WebDriver created successfully");
        
        return driver;
    }
    
    private static WebDriver createFirefox() {
        logger.info("Setting up FirefoxDriver");
        
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        // Check if headless mode is requested via environment variable
        String headlessMode = System.getenv("HEADLESS");
        boolean isHeadless = "true".equalsIgnoreCase(headlessMode);
        
        if (isHeadless) {
            options.addArguments("--headless");
            logger.info("Firefox running in HEADLESS mode");
        } else {
            logger.info("Firefox running in VISIBLE mode");
        }
        
        // Anti-detection: set a realistic user agent for Windows
        options.addPreference("general.useragent.override", 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:136.0) Gecko/20100101 Firefox/136.0");
        
        // Disable automation flags
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        
        // Disable password manager prompts
        options.addPreference("signon.rememberSignons", false);
        options.addPreference("signon.autofillForms", false);
        
        // Make Firefox appear less like automation
        options.addPreference("privacy.trackingprotection.enabled", true);
        options.addPreference("media.autoplay.default", 0);
        
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        FirefoxDriver driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        
        // Hide automation indicators via JS if visible mode
        if (!isHeadless) {
            driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        }
        
        logger.info("Firefox WebDriver created successfully");
        
        return driver;
    }
}

