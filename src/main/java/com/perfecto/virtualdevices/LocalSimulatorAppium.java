package com.perfecto.virtualdevices;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;

public class LocalSimulatorAppium {
	IOSDriver<IOSElement> driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {
		
		String localFilePath = System.getProperty("user.dir") + "//libs//InvoiceApp.zip";
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone Simulator");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "13.7");
		capabilities.setCapability(MobileCapabilityType.APP, localFilePath); 

		driver = new IOSDriver<IOSElement>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		WebDriverWait wait = new WebDriverWait(driver, 30);
		IOSElement email = (IOSElement) wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_email"))));
		email.sendKeys("test@perfecto.com");
		
		IOSElement password = (IOSElement) wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_password"))));
		password.click();
		password.sendKeys("test123");
		
		driver.hideKeyboard();
		
		IOSElement login = driver.findElement(By.name("login_login_btn"));
		login.click();
		
		wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.name("list_add_btn"))));
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		driver.quit();
	}



}

