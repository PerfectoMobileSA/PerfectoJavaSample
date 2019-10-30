package com.perfecto.sampleproject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;


public class PerfectoAppium {

	@Test
	public void main() throws MalformedURLException {
		//Update cloudName variable with your perfecto cloud name
		String cloudName = System.getProperty("cloudName");
		//Update securityToken variable with your perfecto security token. 
		String securityToken = System.getProperty("securityToken");
		//A sample perfecto connect appium script to connect with a perfecto android device and perform addition validation in calculator app.
		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		capabilities.setCapability("securityToken", securityToken);
		capabilities.setCapability("model", "Galaxy.*");
		capabilities.setCapability("openDeviceTimeout", 2);
		capabilities.setCapability("appPackage", "com.sec.android.app.popupcalculator");

		WebDriver driver = new RemoteWebDriver(new URL("https://" + cloudName + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
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
		ReportiumClient reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);

		try {
			reportiumClient.testStart("My Calculator Test", new TestContext("tag2", "tag3"));
			reportiumClient.stepStart("Perform addition");
			driver.findElement(By.xpath("//*[contains(@resource-id,'popupcalculator:id/bt_01')]")).click();
			driver.findElement(By.xpath("//*[contains(@resource-id,'add')]")).click();
			driver.findElement(By.xpath("//*[contains(@resource-id,'popupcalculator:id/bt_01')]")).click();
			driver.findElement(By.xpath("//*[contains(@resource-id,'equal')]")).click();
			reportiumClient.stepEnd();

			reportiumClient.stepStart("Verify Total");
			WebElement results=driver.findElement(By.xpath("//*[contains(@class,'EditText')]"));
			if (!results.getText().equals("2"))
				throw new RuntimeException("Actual calculated number is : " + results.getText() + ". It did not match with expected value: 2");
			reportiumClient.stepEnd();

			//STOP TEST
			TestResult testResult = TestResultFactory.createSuccess();
			reportiumClient.testStop(testResult);

		} catch (Exception e) {
			TestResult testResult = TestResultFactory.createFailure(e);
			reportiumClient.testStop(testResult);
			e.printStackTrace();
		} finally {
			driver.close();
			driver.quit();
			// Retrieve the URL to the DigitalZoom Report 
			String reportURL = reportiumClient.getReportUrl();
			System.out.println(reportURL);
		}
	}
}

