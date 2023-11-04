//package com.naukri.testscript;
//
//import org.testng.annotations.Test;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//public class Naukri {

//	@Test(enabled=false)
//	public void Test() throws InterruptedException {
//		WebDriverManager.chromedriver().setup();
//		WebDriver driver=new ChromeDriver();
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//		driver.get("https://www.Naukri.com");
//		
//		
//		
//		
//		
//		WebDriverWait wait=new WebDriverWait(driver, 20);
////		driver.manage().window().maximize();
////		driver.findElement(By.linkText("Login")).click();
////		driver.findElement(By.xpath("//label[text()='Email ID / Username']/../input")).sendKeys("hitesh_p16@yahoo.com");
////		driver.findElement(By.xpath("//input[@type='password']")).sendKeys("abc1234@");
////		driver.findElement(By.xpath("//button[text()='Login']")).click();	
////		wait.until(ExpectedConditions.titleIs("Home | Mynaukri"));
////		driver.get("https://www.naukri.com/mnjuser/profile?id=&altresid");
//////		driver.findElement(By.xpath("//div[text()='UPDATE PROFILE']")).click();
////		File f=new File("./data/Mr.Hithesh_Experienced_Tester_Resume.pdf");
////		String absolutePath = f.getAbsolutePath();
////		driver.findElement(By.id("attachCV")).sendKeys(absolutePath);
////		driver.close();
//	}
//}
