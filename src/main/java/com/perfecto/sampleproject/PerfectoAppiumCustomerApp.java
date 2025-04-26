package com.perfecto.sampleproject;

import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.android.AndroidDriver;

public class PerfectoAppiumCustomerApp {
	AndroidDriver driver;
	ReportiumClient reportiumClient;

	@Test
	public void appiumTest() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) in
		// application.properties file or pass it
		// as maven properties: -Dperfecto.cloud.name=<<cloud name>>
		String cloudName = PerfectoLabUtils.getPerfectoCloudName();

		// //Replace <<security token>> with your perfecto security token in
		// application.properties file or pass it as
		// maven properties: -Dperfecto.security.token=<<SECURITY TOKEN>> More info:
		// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = PerfectoLabUtils.getSecurityToken();

		// Mobile: Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("perfecto:browserName", "");
		capabilities.setCapability("perfecto:model", "Galaxy S.*|LG.*");
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		capabilities.setCapability("perfecto:openDeviceTimeout", 2);
		capabilities.setCapability("perfecto:appPackage", "<<MY App package>>"); // Set the unique identifier of your app
		capabilities.setCapability("perfecto:autoLaunch", true); // Whether to install and launch the app automatically.
		capabilities.setCapability("perfecto:takesScreenshot", false);
		capabilities.setCapability("perfecto:screenshotOnError", true); // Take screenshot only on errors
		capabilities.setCapability("perfecto:automationName", "UiAutomator2");
		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("perfecto:securityToken", securityToken);

		driver = new AndroidDriver(
				new URL("https://" + cloudName + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"),
				capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Android Java Native Sample", new TestContext("tag2", "tag3")); // Starts the
																									// reportium test

		reportiumClient.stepStart("first step");
//		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

		// Enter your code here
		reportiumClient.stepEnd();
		// Add as many test steps as needed

	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		TestResult testResult = null;
		if (result.getStatus() == ITestResult.SUCCESS) {
			testResult = TestResultFactory.createSuccess();
		} else if (result.getStatus() == ITestResult.FAILURE) {
			testResult = TestResultFactory.createFailure(result.getThrowable());
		}
		if(null != reportiumClient) {
			reportiumClient.testStop(testResult);
			// Retrieve the URL to the DigitalZoom Report
			String reportURL = reportiumClient.getReportUrl();
			System.out.println(reportURL);
		}
		
		if(null != driver) {
			driver.quit();
		}
		
	}

}
