package com.naukri.testscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.testng.annotations.Test;

public class Naukri {

	public static WebDriver driver;

	@Test
	public void test() throws IOException {

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--headless");
		options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

		// Generate a unique temp directory for user data
		Path userDataDir = Files.createTempDirectory(java.util.UUID.randomUUID().toString());
		options.addArguments("--user-data-dir=" + userDataDir.toString());

		WebDriver driver = null;
		try {
			driver = new ChromeDriver(options);

			driver.get("https://www.naukri.com");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Login"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email ID / Username']/../input")))
				.sendKeys("hitesh_p16@yahoo.com");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
				.sendKeys("Flappybird@123");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']"))).click();
			// Explicit wait to ensure login is processed and profile link is clickable
			wait.until(ExpectedConditions.titleContains("Home | Mynaukri"));
			driver.get("https://www.naukri.com/mnjuser/profile");
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='dummyUpload typ-14Bold']"))).click();
			File f = new File("./data/Mr.Hithesh_Experienced_Tester_Resume.pdf");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("attachCV"))).sendKeys(f.getAbsolutePath());

			// Save screenshot to the workspace root or a known location
			File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			Path screenshotPath = Paths.get("headless-debug.png");
			Files.copy(screenshot.toPath(), screenshotPath);
			System.out.println("Screenshot saved at: " + screenshotPath.toAbsolutePath());

		} catch (Exception e) {
			try {
				if (driver != null) {
					File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
					Path screenshotPath = Paths.get("headless-debug.png");
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
