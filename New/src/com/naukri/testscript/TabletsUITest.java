package com.naukri.testscript;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.constants.AmazonConstants;
import com.naukri.utils.UrlGenerator;

public class TabletsUITest {
    
	public static WebDriver driver;

    public static void main(String[] args) throws Exception {
       TabletsUITest test = new TabletsUITest();
       test.testExtractTabletData();
    }

    @Test
    public void testExtractTabletData() throws Exception {
        // Use UrlGenerator to parameterize the new Amazon URL structure
        String url = UrlGenerator.generateUrlWithParams(
            "tablets", // k
            "Smartphones", // i
            AmazonConstants.Discount50PercentOff, // rh
            "price-asc-rank", // s
            "dc", // dc
            "v1%3A1LnjVlgfcmLpzkRTyvVg9drUER1%2BtdJTV1L4PXz3uw4" // ds
        );

        ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--no-sandbox");
		// options.addArguments("--headless");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
		options.setPageLoadStrategy(PageLoadStrategy.NONE);

		Path userDataDir = Files.createTempDirectory(java.util.UUID.randomUUID().toString());
		options.addArguments("--user-data-dir=" + userDataDir.toString());

		WebDriver driver = null;

        	driver = new ChromeDriver(options);

			System.out.println("Navigating to Naukri.com...");
			driver.get(url);

			System.out.println("Waiting for Login link...");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login"))).click();

			System.out.println("Entering email...");

			wait.until(ExpectedConditions.titleContains("Home | Mynaukri"));
              
    }
}
