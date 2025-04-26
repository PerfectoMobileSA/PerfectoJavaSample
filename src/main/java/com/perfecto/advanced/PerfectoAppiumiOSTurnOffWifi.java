package com.perfecto.advanced;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;
import com.perfecto.sampleproject.PerfectoLabUtils;

import io.perfecto.utils.PerfectoIOSDriverFactory;

public class PerfectoAppiumiOSTurnOffWifi {
	RemoteWebDriver driver;
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

		// A sample perfecto connect appium script to connect with a perfecto android
		// device and perform addition validation in calculator app.
		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("securityToken", securityToken);
		capabilities.setCapability("model", "iPhone.*");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("bundleId", "com.apple.mobilesafari");
		capabilities.setCapability("automationName", "Appium");
		driver = PerfectoIOSDriverFactory.createPerfectoDriver(cloudName, capabilities);
		
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("My iOS wifi turn on & off Test", new TestContext("tag2", "tag3")); // Starts the
																										// reportium
																										// test

		reportiumClient.stepStart("Verify iOS Settings App is loaded"); // Starts a reportium step
		Map<String, Object> params = new HashMap<>();
		params.put("identifier", "com.apple.Preferences");
		driver.executeScript("mobile:application:open", params);
		driver.executeScript("mobile:application:close", params);
		driver.executeScript("mobile:application:open", params);
		reportiumClient.stepEnd(); // Stops a reportium step

		reportiumClient.stepStart("Verify wifi turn off and on");
		driver.findElement(By.xpath("//*[@value=\"Wi-Fi\"]")).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		Thread.sleep(5000);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@label=\"Wi-Fi\" and @value=\"1\"]")))
					.click();
		} catch (Exception e) {
		}
		Thread.sleep(5000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@label=\"Wi-Fi\" and @value=\"0\"]"))).click();
		reportiumClient.stepEnd();
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
		// Retrieve the URL to the DigitalZoom Report
		String reportURL = reportiumClient.getReportUrl();
		System.out.println(reportURL);
	}

}
