package com.naukri.testscript;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.openqa.selenium.By;

public class Naukri {

	public static WebDriver driver;

	@Test
	public void test() throws InterruptedException {
		driver = new ChromeDriver(); // Selenium Manager handles driver setup
//		driver.manage().window().maximize();
		driver.get("https://www.Naukri.com");

//		WebDriverWait wait = new WebDriverWait(driver, Duration.of(2l, 50));
		
		driver.findElement(By.linkText("Login")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//label[text()='Email ID / Username']/../input"))
			  .sendKeys("hitesh_p16@yahoo.com");
		driver.findElement(By.xpath("//input[@type='password']"))
			  .sendKeys("abc1234@");
		driver.findElement(By.xpath("//button[text()='Login']")).click();
//		wait.until(ExpectedConditions.titleIs("Home | Mynaukri"));

		driver.get("https://www.naukri.com/mnjuser/profile?id=&altresid");
		driver.findElement(By.xpath("//div[text()='UPDATE PROFILE']")).click();
		File f = new File("./data/Mr.Hithesh_Experienced_Tester_Resume.pdf");
		driver.findElement(By.id("attachCV")).sendKeys(f.getAbsolutePath());

		driver.quit();
	}

}
