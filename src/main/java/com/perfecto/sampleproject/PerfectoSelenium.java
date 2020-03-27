package com.perfecto.sampleproject;
import com.perfecto.sampleproject.Utils;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Platform;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;


public class PerfectoSelenium {
	RemoteWebDriver driver;
	ReportiumClient reportiumClient;

	@Test
	public void seleniumTest() throws Exception {
		//Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>  
		String cloudName = "<<cloud name>>";
		//Replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>  More info: https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";

		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("securityToken", Utils.fetchSecurityToken(securityToken));
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("useAppiumForWeb", true);
		capabilities.setCapability("browserName","Safari");
		try{
			driver = new RemoteWebDriver(new URL("https://" + Utils.fetchCloudName(cloudName) + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		}catch(SessionNotCreatedException e){
			throw new RuntimeException("Driver not created with capabilities: " + capabilities.toString());
		}

		reportiumClient = Utils.setReportiumClient(driver, reportiumClient); //Creates reportiumClient
		reportiumClient.testStart("Perfecto mobile web test", new TestContext("tag2", "tag3")); //Starts the reportium test
		reportiumClient.stepStart("browser navigate to perfecto"); //Starts a reportium step
		driver.get("https://www.perfecto.io");
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Verify title");
		String aTitle = driver.getTitle();
		Utils.assertTitle(aTitle, reportiumClient); //compare the actual title with the expected title
		reportiumClient.stepEnd();
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		//STOP TEST
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

