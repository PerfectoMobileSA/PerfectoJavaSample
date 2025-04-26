package com.perfecto.sampleproject;

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
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.ios.IOSDriver;

public class PerfectoAppiumiOS {
	IOSDriver driver;
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

		// Perfecto Media repository path
		String repositoryKey = "PRIVATE:ExpenseTracker/Native/iOS/InvoiceApp1.0.ipa";
		// Local apk/ipa file path
		String localFilePath = System.getProperty("user.dir") + "//libs//InvoiceApp1.0.ipa";
		// Uploads local apk file to Media repository
		PerfectoLabUtils.uploadMedia(cloudName, securityToken, localFilePath, repositoryKey);

		// Mobile: Auto generate capabilities for device selection:
		// https://developers.perfectomobile.com/display/PD/Select+a+device+for+manual+testing#Selectadeviceformanualtesting-genCapGeneratecapabilities
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("perfecto:browserName", "");
		capabilities.setCapability("perfecto:securityToken", securityToken);
		capabilities.setCapability("perfecto:platformName", "iOS");
		capabilities.setCapability("perfecto:model", "iPhone.*");
		capabilities.setCapability("perfecto:manufacturer", "Apple");
		capabilities.setCapability("perfecto:app", repositoryKey);
		// Set other capabilities.
		capabilities.setCapability("perfecto:bundleId", "io.perfecto.expense.tracker"); // Set your App's bundle Id here
		capabilities.setCapability("perfecto:enableAppiumBehavior", true);
		capabilities.setCapability("perfecto:autoLaunch", true); // Whether to install and launch the app automatically.
		capabilities.setCapability("perfecto:takesScreenshot", false);
		capabilities.setCapability("perfecto:screenshotOnError", true);
		capabilities.setCapability("perfecto:openDeviceTimeout", 5); // Waits for 5 minutes before device connection timeout
		capabilities.setCapability("perfecto:iOSResign", true); // https://help.perfecto.io/perfecto-help/content/perfecto/manual-testing/re_sign_an_application___ios.htm?Highlight=resign%20developer%20certificate
		// capabilities.setCapability("fullReset", false); // Reset app state by
		// uninstalling app.
		capabilities.setCapability("perfecto:automationName", "XCUITest");
		// The below capability is mandatory. Please do not replace it.
		capabilities.setCapability("perfecto:securityToken", securityToken);

		driver = new IOSDriver(
				new URL("https://" + cloudName + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"),
				capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient);
		reportiumClient.testStart("Native Java iOS Sample", new TestContext("tag2", "tag3"));

		reportiumClient.stepStart("Enter email");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement email =  wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_email"))));
		email.sendKeys("test@perfecto.com");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Enter password");
		WebElement password = wait
				.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_password"))));
		password.click();
		password.sendKeys("test123");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Click login");
		driver.hideKeyboard();
		WebElement login = driver.findElement(By.name("login_login_btn"));
		login.click();
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Login Successful");
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("list_add_btn"))));
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
	}

}
