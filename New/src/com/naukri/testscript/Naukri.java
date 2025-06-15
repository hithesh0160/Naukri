package com.naukri.testscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.annotations.Test;

public class Naukri {

	public static WebDriver driver;

	@Test
	public void test() throws IOException {

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

		// Generate a unique temp directory for user data
		Path userDataDir = Files.createTempDirectory("chrome-user-data");
		options.addArguments("--user-data-dir=" + userDataDir.toString());

		WebDriver driver = new ChromeDriver(options);

		driver.get("https://www.naukri.com");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login"))).click();
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

		driver.quit();
	}
}
