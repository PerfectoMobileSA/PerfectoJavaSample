package com.perfecto.virtualdevices;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;

import io.appium.java_client.android.AndroidDriver;

public class LocalEmulatorAppium {
	AndroidDriver driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {
		// Local apk/ipa file path
		String localFilePath = System.getProperty("user.dir") + "//libs//ExpenseAppVer1.0.apk";
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("appium:deviceName", "Android Emulator");
		capabilities.setCapability("appium:platformVersion", "11");
		capabilities.setCapability("appium:platformName", "Android");
		capabilities.setCapability("appium:app", localFilePath); 
		
		driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities); 
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_email"))));
		email.sendKeys("test@perfecto.com");
		
		WebElement password = wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_password"))));
		password.sendKeys("test123");
		
		WebElement login =  wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_login_btn"))));
		login.click();

		wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("list_add_btn"))));
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		driver.quit();
	}



}

