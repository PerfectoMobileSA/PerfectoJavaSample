package com.perfecto.sampleproject;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class PerfectoSelenium {
	private RemoteWebDriver driver;
	private ReportiumClient reportiumClient;

	private String cloudName;

	private String securityToken;

	public PerfectoSelenium() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) in
		// application.properties file or pass it
		// as maven properties: -Dperfecto.cloud.name=<<cloud name>>
		cloudName = PerfectoLabUtils.getPerfectoCloudName();

		// //Replace <<security token>> with your perfecto security token in
		// application.properties file or pass it as
		// maven properties: -Dperfecto.security.token=<<SECURITY TOKEN>> More info:
		// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		securityToken = PerfectoLabUtils.getSecurityToken();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void androidTest() throws Exception {
		// Mobile: Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		String browserName = "chrome";
				//"mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("browserName", browserName);
		capabilities.setCapability("perfecto:platformName", "Android");
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		capabilities.setCapability("perfecto:openDeviceTimeout", 2);
		capabilities.setCapability("perfecto:automationName", "UiAutomator2");
		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("perfecto:securityToken", securityToken);

		driver = new AndroidDriver(new URL("https://" + cloudName
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
//		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Perfecto Android mobile web test", new TestContext("tag2", "tag3"));
		reportiumClient.stepStart("browser navigate to perfecto"); // Starts a reportium step
		driver.get("https://www.google.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Verify title");
		String aTitle = driver.getTitle();
		PerfectoLabUtils.assertTitle(aTitle, reportiumClient); // compare the actual title with the expected title
		reportiumClient.stepEnd();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void iOSTest() throws Exception {
		// Mobile: Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		// browserName should be set to safari by default to open safari browser.
		String browserName = "safari";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("perfecto:platformName", "iOS");
		
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		capabilities.setCapability("perfecto:model", "iPhone.*");
		capabilities.setCapability("perfecto:openDeviceTimeout", 2);
		capabilities.setCapability("perfecto:automationName", "Appium");
		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("perfecto:securityToken", securityToken);
		

		driver = new IOSDriver(new URL("https://" + cloudName
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Perfecto iOS mobile web test", new TestContext("tag2", "tag3"));
		reportiumClient.stepStart("browser navigate to perfecto"); // Starts a reportium step
		driver.get("https://www.google.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Verify title");
		String aTitle = driver.getTitle();
		PerfectoLabUtils.assertTitle(aTitle, reportiumClient); // compare the actual title with the expected title
		reportiumClient.stepEnd();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void webTest() throws Exception {
		// Web: Make sure to Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("platformName", "Windows");
		
		capabilities.setCapability("browserName", "Chrome");
		capabilities.setCapability("browserVersion", "beta");

		Map<String,String> perfectoOptions = new HashMap<String, String>();
		perfectoOptions.put("securityToken", securityToken);
//		perfectoOptions.put("location", "US East");
		perfectoOptions.put("resolution", "1920x1080");
		perfectoOptions.put("platformVersion", "11");
		capabilities.setCapability("perfecto:options", perfectoOptions);
		
		
		driver = new RemoteWebDriver(new URL("https://" + cloudName
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Perfecto desktop web test", new TestContext("tag2", "tag3"));
		reportiumClient.stepStart("browser navigate to perfecto"); // Starts a reportium step
		driver.get("https://www.google.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Verify title");
		String aTitle = driver.getTitle();
		PerfectoLabUtils.assertTitle(aTitle, reportiumClient); // compare the actual title with the expected title
		reportiumClient.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		// STOP TEST
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
