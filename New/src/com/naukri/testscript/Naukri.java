package com.naukri.testscript;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.PageLoadStrategy;

import org.testng.annotations.Test;

public class Naukri {

	public static WebDriver driver;

	@Test
	public void test() throws IOException {

		Properties props = new Properties();
		props.load(new FileInputStream("config.properties"));
		String username = props.getProperty("username");
		String password = props.getProperty("password");

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--no-sandbox");
		options.addArguments("--headless=new");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
		options.setPageLoadStrategy(PageLoadStrategy.NONE); // or NONE

		// Generate a unique temp directory for user data
		Path userDataDir = Files.createTempDirectory(java.util.UUID.randomUUID().toString());
		options.addArguments("--user-data-dir=" + userDataDir.toString());

		WebDriver driver = null;
		try {
			driver = new ChromeDriver(options);

			System.out.println("Navigating to Naukri.com...");
			driver.get("https://www.naukri.com");

			System.out.println("Waiting for Login link...");
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login"))).click();

			System.out.println("Entering email...");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email ID / Username']/../input")))
				.sendKeys(username);

			System.out.println("Entering password...");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
				.sendKeys(password);

			System.out.println("Clicking Login button...");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']"))).click();

			System.out.println("Waiting for Home page to load...");
			wait.until(ExpectedConditions.titleContains("Home | Mynaukri"));

			System.out.println("Navigating to profile page...");
			driver.get("https://www.naukri.com/mnjuser/profile");

			System.out.println("Clicking upload resume button...");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='dummyUpload typ-14Bold']"))).click();

			File f = new File("./data/Mr.Hithesh_Experienced_Tester_Resume.pdf");
			System.out.println("Uploading resume: " + f.getAbsolutePath());
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("attachCV"))).sendKeys(f.getAbsolutePath());

			Path screenshotPath = Paths.get("headless-debug.png");
			// Delete previous screenshot if it exists
			Files.deleteIfExists(screenshotPath);

			// Take new screenshot
			File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			Files.copy(screenshot.toPath(), screenshotPath);
			System.out.println("Screenshot saved at: " + screenshotPath.toAbsolutePath());

		} catch (Exception e) {
			try {
				if (driver != null) {
					Path screenshotPath = Paths.get("headless-debug.png");
					// Delete previous screenshot if it exists
					Files.deleteIfExists(screenshotPath);

					// Take new screenshot
					File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
					Files.copy(screenshot.toPath(), screenshotPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Screenshot saved at: " + screenshotPath.toAbsolutePath());
				}
			} catch (Exception ex) {
				System.out.println("Failed to capture screenshot: " + ex.getMessage());
			}
			e.printStackTrace();
			throw e; // rethrow to let the test fail
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
}
