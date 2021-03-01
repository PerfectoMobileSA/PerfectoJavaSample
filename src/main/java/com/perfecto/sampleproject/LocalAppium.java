package com.perfecto.sampleproject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;


public class LocalAppium {
	RemoteWebDriver driver;

	@Test
	public void addTest() throws Exception {
		//Connect to an emulator and open calculator app. Note: appium server and simulator should be running
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("BROWSER_NAME", "Android");
		capabilities.setCapability("VERSION", "8.0"); 
		capabilities.setCapability("deviceName","Emulator");
		capabilities.setCapability("platformName","Android");
		capabilities.setCapability("appPackage", "com.android.settings");
		capabilities.setCapability("appActivity", "com.android.settings.Settings");	
		try {
			driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}catch(SessionNotCreatedException e){
			throw new RuntimeException("Driver not created with capabilities: " + capabilities.toString());
		}
		
		driver.findElement(By.xpath("//*[contains(@resource-id,':id/collpasing_app_bar_extended_title') or contains(@resource-id,'settings:id/search')] | //*[contains(@text,'Search') | //*[@content-desc='Search']]")).isDisplayed();
		driver.findElement(By.xpath("//*[contains(@text,'Data usage')]")).click();
		WebElement data = driver.findElement(By.xpath("//*[contains(@resource-id, 'action_bar')]//*[@text='Data usage']"));
		PerfectoLabUtils.assertText(data, null, "Data usage");
		driver.quit();
	}

}
