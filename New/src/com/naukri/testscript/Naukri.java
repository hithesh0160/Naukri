package com.naukri.testscript;

import org.testng.annotations.Test;
import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Naukri {
	static {
		System.setProperty("webdriver.chrome.driver","./driver/chromedriver.exe");
	}
	@Test
	public void Test() throws InterruptedException {
		WebDriver driver=new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get("https://www.naukri.com");
		driver.manage().window().maximize();
		driver.findElement(By.linkText("Login")).click();
		driver.findElement(By.xpath("//label[text()='Email ID / Username']/../input")).sendKeys("hitesh_p16@yahoo.com");
		driver.findElement(By.xpath("//input[@type='password']")).sendKeys("abc1234@");
		driver.findElement(By.xpath("//button[text()='Login']")).click();		
		driver.findElement(By.xpath("//div[text()='UPDATE PROFILE']")).click();
		File f=new File("./data/Mr. Hithesh_Resume.pdf");
		String absolutePath = f.getAbsolutePath();
		driver.findElement(By.id("attachCV")).sendKeys(absolutePath);
		Thread.sleep(3000);
		driver.close();
	}
}
