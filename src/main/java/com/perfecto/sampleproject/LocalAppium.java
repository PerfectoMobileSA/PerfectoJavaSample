package com.perfecto.sampleproject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;


public class LocalAppium {

	@Test
	public void add() throws Exception {
		//Connect to an emulator and open calculator app. Note: appium server and simulator should be running
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("BROWSER_NAME", "Android");
		capabilities.setCapability("VERSION", "6.0"); 
		capabilities.setCapability("deviceName","Emulator");
		capabilities.setCapability("platformName","Android");
		capabilities.setCapability("appPackage", "com.android.calculator2");
		capabilities.setCapability("appActivity", "com.android.calculator2.Calculator");
		try {
			WebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			//A sample appium script to connect with a local emulator and perform addition validation in calculator app.
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.findElement(By.xpath("//*[contains(@resource-id,'1')]")).click();
			driver.findElement(By.xpath("//*[contains(@text,'+')]")).click();
			driver.findElement(By.xpath("//*[contains(@resource-id,'1')]")).click();
			driver.findElement(By.xpath("//*[contains(@text,'=')]")).click();
			WebElement results=driver.findElement(By.xpath("//*[contains(@resource-id,'formula')]"));
			assert results.getText().equals("2") : "Actual calculated number is : "+results.getText() + ". It did not match with expected value: 2";
			driver.quit();
		}catch(Exception UnreachableBrowserException) {
			System.out.println("Kindly start the appium server first!");
		}
	}

}