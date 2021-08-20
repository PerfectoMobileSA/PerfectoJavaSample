package com.perfecto.sampleproject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

public class PerfectoAppiumiOS {
	IOSDriver<IOSElement> driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
		
		// Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";
		
		cloudName = PerfectoLabUtils.fetchCloudName(cloudName);
		securityToken = PerfectoLabUtils.fetchSecurityToken(securityToken);
		// Perfecto Media repository path 
		String repositoryKey = "PRIVATE:ExpenseTracker/Native/iOS/InvoiceApp1.0.ipa";
		// Local apk/ipa file path
		String localFilePath = System.getProperty("user.dir") + "//libs//InvoiceApp1.0.ipa";
		// Uploads local apk file to Media repository
		PerfectoLabUtils.uploadMedia(cloudName, securityToken, localFilePath, repositoryKey);
		
		// Mobile: Auto generate capabilities for device selection: https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("securityToken", securityToken);
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("model", "iPhone.*");
		capabilities.setCapability("manufacturer", "Apple");
		capabilities.setCapability("app", repositoryKey);
		// Set other capabilities.
		capabilities.setCapability("bundleId", "io.perfecto.expense.tracker"); // Set your App's bundle Id here
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("autoLaunch", true); // Whether to install and launch the app automatically.
		capabilities.setCapability("takesScreenshot", false);
		capabilities.setCapability("screenshotOnError", true);
		capabilities.setCapability("openDeviceTimeout", 5); // Waits for 5 minutes before device connection timeout
		capabilities.setCapability("iOSResign",true);  // https://help.perfecto.io/perfecto-help/content/perfecto/manual-testing/re_sign_an_application___ios.htm?Highlight=resign%20developer%20certificate
		// capabilities.setCapability("fullReset", false); // Reset app state by  uninstalling app.
		
		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("securityToken", securityToken);
		
		driver = new IOSDriver<IOSElement>(new URL("https://" + cloudName  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); 
		reportiumClient.testStart("Native Java iOS Sample", new TestContext("tag2", "tag3")); 

		reportiumClient.stepStart("Enter email");
		WebDriverWait wait = new WebDriverWait(driver, 30);
		IOSElement email = (IOSElement) wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_email"))));
		email.sendKeys("test@perfecto.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Enter password");
		IOSElement password = (IOSElement) wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_password"))));
		password.click();
		password.sendKeys("test123");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Click login");
		driver.hideKeyboard();
		IOSElement login = driver.findElement(By.name("login_login_btn"));
		login.click();
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Login Successful");
		wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.name("list_add_btn"))));
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

		driver.quit();
		// Retrieve the URL to the DigitalZoom Report 
		String reportURL = reportiumClient.getReportUrl();
		System.out.println(reportURL);
	}



}

