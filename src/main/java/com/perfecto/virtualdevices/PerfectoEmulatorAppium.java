package com.perfecto.virtualdevices;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;
import com.perfecto.sampleproject.PerfectoLabUtils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;

public class PerfectoEmulatorAppium {
	AndroidDriver<AndroidElement> driver;
	ReportiumClient reportiumClient;
	Boolean debug = false;
	
	@Test
	public void appiumTest() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
		// Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";
		cloudName = PerfectoLabUtils.fetchCloudName(cloudName);
		securityToken = PerfectoLabUtils.fetchSecurityToken(securityToken);
		// Perfecto Media repository path 
		String repositoryKey = "PRIVATE:ExpenseTracker/Native/android/ExpenseAppVer1.0.apk";
		// Local apk/ipa file path
		String localFilePath = System.getProperty("user.dir") + "//libs//ExpenseAppVer1.0.apk";
		// Uploads local apk file to Media repository
		PerfectoLabUtils.uploadMedia(cloudName, securityToken, localFilePath, repositoryKey);
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("securityToken", securityToken);
		capabilities.setCapability("deviceName", "pixel 4");
		capabilities.setCapability("platformVersion", "11");
		capabilities.setCapability("manufacturer", "Google");
        capabilities.setCapability("useVirtualDevice", true);
		capabilities.setCapability("app", repositoryKey); 

		driver = new AndroidDriver<AndroidElement>(new URL("https://" + cloudName  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); 
		reportiumClient.testStart("Native Java Emulator Sample", new TestContext("tag2", "tag3")); 
		
		// Installs and opens app if Device session sharing is enabled.
		if(debug == true) {
			Map<String, Object> params = new HashMap<>();
			params.put("file", repositoryKey);
			driver.executeScript("mobile:application:install", params); 
			
			params.put("identifier", "io.perfecto.expense.tracker");
			driver.executeScript("mobile:application:open", params);
			driver.context("NATIVE_APP");
		}
		
		reportiumClient.stepStart("Enter email");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		AndroidElement email = (AndroidElement) wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_email"))));
		email.sendKeys("test@perfecto.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Enter password");
		AndroidElement password = (AndroidElement) wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_password"))));
		password.sendKeys("test123");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Click login");
		AndroidElement login = (AndroidElement) wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("login_login_btn"))));
		login.click();
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Login Successful");
		wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.id("list_add_btn"))));
		reportiumClient.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		TestResult testResult = null;
		if(result.getStatus() == result.SUCCESS) {
			testResult = TestResultFactory.createSuccess();
		}
		else if (result.getStatus() == result.FAILURE) {
			testResult = TestResultFactory.createFailure(result.getThrowable());
		}
		reportiumClient.testStop(testResult);

		// Skips closing the emulator for debugging session.
		if(!(debug)) {
			driver.quit();
		}
		// Retrieve the URL to the DigitalZoom Report 
		String reportURL = reportiumClient.getReportUrl();
		System.out.println(reportURL);
	}



}

