package com.perfecto.advanced;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import io.appium.java_client.AppiumDriver;

public class PerfectoAppiumiOSTurnOffWifi {
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
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));
		capabilities.setCapability("model", "iPhone.*");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("bundleId", "com.apple.mobilesafari");
		driver = (RemoteWebDriver)(new AppiumDriver<>(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); //Creates reportiumClient
		reportiumClient.testStart("My iOS wifi turn on & off Test", new TestContext("tag2", "tag3")); //Starts the reportium test

		reportiumClient.stepStart("Verify iOS Settings App is loaded"); //Starts a reportium step
		Map<String, Object> params = new HashMap<>();
		params.put("identifier", "com.apple.Preferences");
		driver.executeScript("mobile:application:open", params);
		driver.executeScript("mobile:application:close", params);
		driver.executeScript("mobile:application:open", params);
		reportiumClient.stepEnd(); //Stops a reportium step

		reportiumClient.stepStart("Verify wifi turn off and on");
		driver.findElementByXPath("//*[@value=\"Wi-Fi\"]").click();
		WebDriverWait wait = new WebDriverWait(driver, 15);
		Thread.sleep(5000);
		try{
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@label=\"Wi-Fi\" and @value=\"1\"]"))).click();
		}catch(Exception e){}
		Thread.sleep(5000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@label=\"Wi-Fi\" and @value=\"0\"]"))).click();
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

