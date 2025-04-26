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

import io.appium.java_client.ios.IOSDriver;

public class LocalSimulatorAppium {
	IOSDriver driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {

		String localFilePath = System.getProperty("user.dir") + "//libs//InvoiceApp.zip";
		DesiredCapabilities capabilities = new DesiredCapabilities();

		capabilities.setCapability("appium:deviceName", "iPhone Simulator");
		capabilities.setCapability("appium:platformVersion", "13.7");
		capabilities.setCapability("appium:platformName", "iOS");
		capabilities.setCapability("appium:app", localFilePath);

		driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement email = wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_email"))));
		email.sendKeys("test@perfecto.com");

		WebElement password = wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_password"))));
		password.click();
		password.sendKeys("test123");

		driver.hideKeyboard();

		WebElement login = driver.findElement(By.name("login_login_btn"));
		login.click();

		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("list_add_btn"))));
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		driver.quit();
	}

}
