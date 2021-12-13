package com.perfecto.advanced;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
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

import io.appium.java_client.AppiumDriver;

public class Perfecto_OTP_Sample {
	RemoteWebDriver driver;
	RemoteWebDriver driver2;
	ReportiumClient reportiumClient;
	ReportiumClient reportiumClient2;
	@Test
	public void appiumAppTest() throws Exception {
		//Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
		//		//Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";
		String msg = "OTP:123123";
		//		Driver 1
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("model", "Galaxy S10");
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("openDeviceTimeout", 4);
		capabilities.setCapability("appPackage", "com.samsung.android.messaging");
		capabilities.setCapability("autoLaunch",true); 
		driver = (RemoteWebDriver)(new AppiumDriver<>(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);


		//		Driver 2
		capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));
		capabilities.setCapability("enableAppiumBehavior", true);
		//Make sure to select a device with phone number!!!!
		capabilities.setCapability("description", "iPhoneWithSim");
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("openDeviceTimeout", 4);
		capabilities.setCapability("bundleId", "com.apple.MobileSMS");
		capabilities.setCapability("autoLaunch",true); 
		driver2 = (RemoteWebDriver)(new AppiumDriver<>(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
		driver2.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		String phoneNumber = PerfectoLabUtils.getDevicePhoneNumber(driver2);

		WebDriverWait wait = new WebDriverWait(driver, 60);
		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); //Creates reportiumClient
		reportiumClient2 = PerfectoLabUtils.setReportiumClient(driver2, reportiumClient); //Creates reportiumClient
		reportiumClient.testStart("send SMS", new TestContext("tag1")); //Starts the reportium test
		reportiumClient2.testStart("check SMS", new TestContext("tag2")); //Starts the reportium test

		reportiumClient.stepStart("Send SMS"); //Starts a reportium step
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/fab\"]"))).click();
		By to = By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/recipients_editor_to\"]");
		wait.until(ExpectedConditions.visibilityOfElementLocated(to)).click();
		System.out.println("phone number is: "+ phoneNumber);
		if(phoneNumber.isEmpty()) {
			throw new RuntimeException("Phone number of second device is empty!");
		}
		driver.findElement(to).sendKeys(phoneNumber);
		Map<String, Object> params = new HashMap<>();
		params.put("label", "Next");
		driver.executeScript("mobile:button-text:click", params);
		WebElement msgTxt = driver.findElement(By.xpath("//*[@resource-id=\"com.samsung.android.messaging:id/message_edit_text\"]"));
		wait.until(ExpectedConditions.elementToBeClickable(msgTxt));
		msgTxt.isDisplayed();
		msgTxt.click();
		msgTxt.sendKeys(msg);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@content-desc='Send' and @enabled='true'] | //*[contains(@resource-id,\"send_buttons\")]"))).click();
		reportiumClient.stepEnd(); 

		reportiumClient2.stepStart("Verify SMS in other device"); 
		reportiumClient2.reportiumAssert("OTP received", driver2.findElement(By.xpath("(//*[contains(@label,'" + msg + "')])[1]")).isDisplayed());
		reportiumClient2.stepEnd(); 
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		try {
			TestResult testResult = null;
			if(result.getStatus() == result.SUCCESS) {
				testResult = TestResultFactory.createSuccess();
			}
			else if (result.getStatus() == result.FAILURE) {
				testResult = TestResultFactory.createFailure(result.getThrowable());
			}
			reportiumClient.testStop(testResult);
			reportiumClient2.testStop(testResult);
			// Retrieve the URL to the DigitalZoom Report 
			String reportURL = reportiumClient.getReportUrl();
			System.out.println(reportURL);
			String reportURL2 = reportiumClient2.getReportUrl();
			System.out.println(reportURL2);
		}catch(Exception e) {
			e.printStackTrace();
		}
		driver.quit();
		driver2.quit();

	}



}

