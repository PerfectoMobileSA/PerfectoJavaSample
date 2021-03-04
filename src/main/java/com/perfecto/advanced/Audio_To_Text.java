
package com.perfecto.advanced;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
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
import com.perfecto.sampleproject.PerfectoLabUtils;

import io.appium.java_client.AppiumDriver;

public class Audio_To_Text {
	RemoteWebDriver driver;
	ReportiumClient reportiumClient;
	
	// MAKE SURE TO HAVE Audio validation and transcription commands license to run this successfully. Contact Support for more info.

	@Test
	public void appiumTest() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it
		// as maven properties: -DcloudName=<<cloud name>>
		String cloudName = "<<cloud name>>";
		// Replace <<security token>> with your perfecto security token or pass it as
		// maven properties: -DsecurityToken=<<SECURITY TOKEN>> More info:
		// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";

		// A sample perfecto connect appium script to connect with a perfecto android
		// device and perform addition validation in calculator app.
		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("appPackage", "com.android.settings");
		capabilities.setCapability("appActivity", "com.android.settings.Settings");
		driver = (RemoteWebDriver) (new AppiumDriver<>(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities));
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("Audio_2_Text", new TestContext("audio"));
		reportiumClient.stepStart("audio to text");

		Map<String, Object> params1 = new HashMap<>();
		params1.put("key", "PRIVATE:mysong.mp3");
		params1.put("language", "us-english");
		params1.put("rate", "broad");
		params1.put("profile", "accuracy");
		String text = ((String) driver.executeScript("mobile:audio:text", params1));

		FileUtils.deleteQuietly(new File("output.txt"));

		FileWriter myWriter = new FileWriter(new File("output.txt"));
		myWriter.write(text);
		myWriter.close();
		reportiumClient.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		TestResult testResult = null;
		if (result.getStatus() == result.SUCCESS) {
			testResult = TestResultFactory.createSuccess();
		} else if (result.getStatus() == result.FAILURE) {
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
