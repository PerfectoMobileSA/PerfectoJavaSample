package com.perfecto.sampleproject;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.AppiumDriver;

public class PerfectoAppium {
	RemoteWebDriver driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {
		//Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
		//Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";

		//A sample perfecto connect appium script to connect with a perfecto android device and perform addition validation in calculator app.
		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("securityToken", Utils.fetchSecurityToken(securityToken));
		capabilities.setCapability("model", "Galaxy S.*");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("appPackage", "com.android.settings");
		capabilities.setCapability("appActivity", "com.android.settings.Settings");	
		try{
			driver = (RemoteWebDriver)(new AppiumDriver<>(new URL("https://" + Utils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}catch(SessionNotCreatedException e){
			throw new RuntimeException("Driver not created with capabilities: " + capabilities.toString());
		}

		reportiumClient = Utils.setReportiumClient(driver, reportiumClient); //Creates reportiumClient
		reportiumClient.testStart("My Settings Test", new TestContext("tag2", "tag3")); //Starts the reportium test

		reportiumClient.stepStart("Verify Settings App is loaded"); //Starts a reportium step
		driver.findElement(By.xpath("//*[contains(@resource-id,':id/collpasing_app_bar_extended_title') or contains(@resource-id,'settings:id/search')] | //*[contains(@text,'Search')]")).isDisplayed();
		reportiumClient.stepEnd(); //Stops a reportium step

		reportiumClient.stepStart("Verify Data usage validation");
		driver.findElement(By.xpath("//*[contains(@text,'Data usage')]")).click();
		WebElement data = driver.findElement(By.xpath("//*[contains(@resource-id, 'action_bar')]//*[@text='Data usage']"));
		Utils.assertText(data, reportiumClient, "Data usage");
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

		driver.close();
		driver.quit();
		// Retrieve the URL to the DigitalZoom Report 
		String reportURL = reportiumClient.getReportUrl();
		System.out.println(reportURL);
	}



}

