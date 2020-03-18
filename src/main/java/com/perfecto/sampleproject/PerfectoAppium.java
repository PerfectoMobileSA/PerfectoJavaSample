package com.perfecto.sampleproject;
import java.net.URL;
import java.util.Set;
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
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.AppiumDriver;

public class PerfectoAppium {
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
		capabilities.setCapability("securityToken", Utils.fetchSecurityToken(securityToken));
		capabilities.setCapability("model", "Galaxy S.*");
		capabilities.setCapability("enableAppiumBehavior", true);
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("appPackage", "com.sec.android.app.popupcalculator");
		driver = (RemoteWebDriver)(new AppiumDriver(new URL("https://" + Utils.fetchCloudName(cloudName)  + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities)); 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		PerfectoExecutionContext perfectoExecutionContext;
		// Reporting client. For more details, see https://developers.perfectomobile.com/display/PD/Java
		if(System.getProperty("reportium-job-name") != null) {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("My Project", "1.0"))
					.withJob(new Job(System.getProperty("reportium-job-name") , Integer.parseInt(System.getProperty("reportium-job-number"))))
					.withContextTags("tag1")
					.withWebDriver(driver)
					.build();
		} else {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("My Project", "1.0"))
					.withContextTags("tag1")
					.withWebDriver(driver)
					.build();
		}
		reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);

		reportiumClient.testStart("My Calculator Test", new TestContext("tag2", "tag3"));
		reportiumClient.stepStart("Testing Appium driver");
		String contextName = ((AppiumDriver)driver).getContext();
		Set<String> contextHandles = ((AppiumDriver)driver).getContextHandles();
		reportiumClient.reportiumAssert("Context:" +contextName , !contextName.isEmpty());
		reportiumClient.stepEnd();

		reportiumClient.stepStart("Perform addition");
		driver.findElement(By.xpath("//android.widget.Button[@text='1']")).click();
		driver.findElement(By.xpath("//android.widget.Button[@text='+']")).click();
		driver.findElement(By.xpath("//android.widget.Button[@text='1']")).click();
		driver.findElement(By.xpath("//android.widget.Button[@text='=']")).click();
		reportiumClient.stepEnd();

//		reportiumClient.stepStart("Verify Total");
//		WebDriverWait wait=new WebDriverWait(driver, 20);
//		WebElement results = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath( "//*[contains(@resource-id,'id/calc_edt_formula') or contains(@resource-id,'id/txtCalc')]")));
//		reportiumClient.reportiumAssert("Verify Result: " + results.getText() , results.getText().equals("2"));
//		reportiumClient.stepEnd();
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

