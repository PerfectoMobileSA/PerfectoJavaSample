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
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;

public class PerfectoAppium_Native {
	RemoteWebDriver driver;
	ReportiumClient reportiumClient;
	@Test
	public void appiumAppTest() throws Exception {
		//Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
//		//Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";
	
		//A sample perfecto connect appium script to connect with a perfecto iOS device in a sample native app.
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
		capabilities.setCapability("securityToken", Utils.fetchSecurityToken(securityToken));
		capabilities.setCapability("model", "iPhone.*");
		capabilities.setCapability("platformVersion", "14.*");
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("app", "PUBLIC:Genesis/Sample/iOSInvoiceApp1.0.ipa");
		capabilities.setCapability("bundleId", "io.perfecto.expense.tracker");
		capabilities.setCapability("fullReset",true); 
		capabilities.setCapability("autoLaunch",true); 
		capabilities.setCapability("autoInstrument", true);

		try{
			driver = (RemoteWebDriver)(new AppiumDriver<>(new URL("https://" + Utils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		}catch(SessionNotCreatedException e){
			throw new RuntimeException("Driver not created with capabilities: " + capabilities.toString());
		}

		reportiumClient = Utils.setReportiumClient(driver, reportiumClient); //Creates reportiumClient
		reportiumClient.testStart("Sample Invoice App", new TestContext("tag2", "tag3")); //Starts the reportium test

		reportiumClient.stepStart("Verify Sample App is loaded"); //Starts a reportium step
		WebElement email = driver.findElement(By.xpath("//*[@label='Email']"));
		Utils.assertText(email, reportiumClient, "Email");
		reportiumClient.stepEnd(); //Stops a reportium step

		reportiumClient.stepStart("Perform Login"); 
		WebElement email_txt = driver.findElement(By.xpath("//*[@name='login_email']"));
		email_txt.sendKeys("test@perfecto.com");
		driver.findElement(By.xpath("//*[@name='login_password']")).sendKeys("test123");
		driver.findElement(By.xpath("//*[@name='Login']")).click();
		WebElement expense = driver.findElement(By.xpath("//*[@label='Expenses']"));
		Utils.assertText(expense, reportiumClient, "Expenses");
		reportiumClient.stepEnd(); 

		reportiumClient.stepStart("Add an item");
		driver.findElement(By.xpath("//*[@name='list_add_btn']")).click();
		WebElement head_lbl = driver.findElement(By.xpath("//*[@label='Head']"));
		Utils.assertText(head_lbl, reportiumClient, "Head");
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

