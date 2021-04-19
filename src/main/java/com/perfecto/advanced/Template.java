package com.perfecto.advanced;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.android.AndroidDriver;

public class Template {

	public static void main(String[] args) throws MalformedURLException, IOException {
		System.out.println("Run started");

		String browserName = "mobileOS";
		DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
		// TODO: change your cloud url
		String host = "<<CLOUD NAME>>.perfectomobile.com";
		// TODO: change your security Token
		capabilities.setCapability("securityToken", "<<YOUR SECURITY TOKEN>>");
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("automationName", "Appium");

		// Application settings examples.
		// capabilities.setCapability("app", "PRIVATE:applications/Errands.ipa");
		// For Android:
		// capabilities.setCapability("appPackage", "com.google.android.keep");
		// capabilities.setCapability("appActivity", ".activities.BrowseActivity");
		// For iOS:
		// capabilities.setCapability("bundleId", "com.yoctoville.errands");
		
		// Add a persona to your script (see https://community.perfectomobile.com/posts/1048047-available-personas)
		//capabilities.setCapability(WindTunnelUtils.WIND_TUNNEL_PERSONA_CAPABILITY, WindTunnelUtils.GEORGIA);
		
		// Name your script
		// capabilities.setCapability("scriptName", "AppiumTest");

		AndroidDriver driver = new AndroidDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"),
				capabilities);
// IOSDriver driver = new IOSDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

// Reporting client. For more details, see http://developers.perfectomobile.com/display/PD/Reporting
		PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
				.withProject(new Project("My Project", "1.0")).withJob(new Job("My Job", 45)).withContextTags("tag1")
				.withWebDriver(driver).build();
		ReportiumClient reportiumClient = new ReportiumClientFactory()
				.createPerfectoReportiumClient(perfectoExecutionContext);

		try {
			reportiumClient.testStart("My test name", new TestContext("tag2", "tag3"));

// write your code here

// reportiumClient.testStep("step1"); // this is a logical step for reporting
// add commands...
// reportiumClient.testStep("step2");
// more commands...

			reportiumClient.testStop(TestResultFactory.createSuccess());
		} catch (Exception e) {
			reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
			e.printStackTrace();
		} finally {
			try {
				driver.quit();
				System.out.println(reportiumClient.getReportUrl());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Run ended");
	}
}