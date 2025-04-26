package com.perfecto.advanced;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

import io.perfecto.utils.PerfectoAndroidDriverFactory;
import io.perfecto.utils.PerfectoIOSDriverFactory;

public class Perfecto_OTP_Sample {

	RemoteWebDriver driver;
	RemoteWebDriver driver2;
	ReportiumClient reportiumClient;
	ReportiumClient reportiumClient2;

	@Test
	public void appiumAppTest() throws Exception {

		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) in
		// application.properties file or pass it
		// as maven properties: -Dperfecto.cloud.name=<<cloud name>>
		String cloudName = PerfectoLabUtils.getPerfectoCloudName();

		// //Replace <<security token>> with your perfecto security token in
		// application.properties file or pass it as
		// maven properties: -Dperfecto.security.token=<<SECURITY TOKEN>> More info:
		// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = PerfectoLabUtils.getSecurityToken();

		String msg = "OTP:123123";

		// Driver 1
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("perfecto:browserName", "");
		capabilities.setCapability("perfecto:securityToken", securityToken);
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		capabilities.setCapability("perfecto:model", "Galaxy.*");
		capabilities.setCapability("perfecto:platformName", "Android");
		capabilities.setCapability("perfecto:openDeviceTimeout", 4);
		capabilities.setCapability("perfecto:appPackage", "com.samsung.android.messaging");
		capabilities.setCapability("perfecto:autoLaunch", true);
		capabilities.setCapability("perfecto:automationName", "UiAutomator2");

		driver = PerfectoAndroidDriverFactory.createPerfectoDriver(cloudName, capabilities);

		// Driver 2
		capabilities = new DesiredCapabilities();
		capabilities.setCapability("perfecto:browserName", "");
		capabilities.setCapability("perfecto:securityToken", securityToken);
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		// Make sure to select a device with phone number!!!!
		capabilities.setCapability("perfecto:description", "iPhoneWithSim");
		capabilities.setCapability("perfecto:platformName", "iOS");
		capabilities.setCapability("perfecto:openDeviceTimeout", 4);
		capabilities.setCapability("perfecto:bundleId", "com.apple.MobileSMS");
		capabilities.setCapability("perfecto:autoLaunch", true);
		capabilities.setCapability("perfecto:automationName", "XCUITest");
		driver2 = PerfectoIOSDriverFactory.createPerfectoDriver(cloudName, capabilities);

		String phoneNumber = PerfectoLabUtils.getDevicePhoneNumber(driver2);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient2 = PerfectoLabUtils.setReportiumClient(driver2, reportiumClient); // Creates reportiumClient

		reportiumClient.testStart("send SMS", new TestContext("tag1")); // Starts the reportium test
		reportiumClient2.testStart("check SMS", new TestContext("tag2")); // Starts the reportium test

		reportiumClient.stepStart("Send SMS"); // Starts a reportium step

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/fab\"]")))
				.click();
		By to = By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/recipients_editor_to\"]");

		wait.until(ExpectedConditions.visibilityOfElementLocated(to)).click();

		System.out.println("phone number is: " + phoneNumber);
		if (phoneNumber.isEmpty()) {
			throw new RuntimeException("Phone number of second device is empty!");
		}

		driver.findElement(to).sendKeys(phoneNumber);

		Map<String, Object> params = new HashMap<>();
		params.put("label", "Next");
		driver.executeScript("mobile:button-text:click", params);

		WebElement msgTxt = driver
				.findElement(By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/message_edit_text\"]"));
		wait.until(ExpectedConditions.elementToBeClickable(msgTxt));
		msgTxt.isDisplayed();
		msgTxt.click();
		msgTxt.sendKeys(msg);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//*[@content-desc='Send' and @enabled='true'] | //*[contains(@resource-id,\"send_buttons\")]")))
				.click();

		reportiumClient.stepEnd();

		reportiumClient2.stepStart("Verify SMS in other device");
		reportiumClient2.reportiumAssert("OTP received",
				driver2.findElement(By.xpath("(//*[contains(@label,'" + msg + "')])[1]")).isDisplayed());
		reportiumClient2.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		try {
			TestResult testResult = null;
			if (result.getStatus() == ITestResult.SUCCESS) {
				testResult = TestResultFactory.createSuccess();
			} else if (result.getStatus() == ITestResult.FAILURE) {
				testResult = TestResultFactory.createFailure(result.getThrowable());
			}
			
			if(null != reportiumClient) {
				reportiumClient.testStop(testResult);
				String reportURL = reportiumClient.getReportUrl();
				System.out.println(reportURL);
			}
			
			if(null != reportiumClient2) {
				reportiumClient2.testStop(testResult);
				String reportURL2 = reportiumClient2.getReportUrl();
				System.out.println(reportURL2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != driver) {
			driver.quit();
		}
		
		if(null != driver2 ) {
			driver2.quit();
		}
	}
}
