package com.naukri.testscript;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

		driver = new ChromeDriver(options);

		driver.get("https://www.naukri.com");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text()='Email ID / Username']/../input")))
			.sendKeys("hitesh_p16@yahoo.com");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
			.sendKeys("Flappybird@123");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']"))).click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Wait for 5 seconds to ensure login is processed
		driver.get("https://www.naukri.com/mnjuser/profile");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='dummyUpload typ-14Bold']"))).click();
		File f = new File("./data/Mr.Hithesh_Experienced_Tester_Resume.pdf");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("attachCV"))).sendKeys(f.getAbsolutePath());

		driver.quit();
	}
}
