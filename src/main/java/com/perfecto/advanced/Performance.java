package com.perfecto.advanced;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;
import com.perfecto.sampleproject.PerfectoLabUtils;

public class Performance {
	private static RemoteWebDriver driver;
	ReportiumClient reportiumClient;
	StopWatch pageLoad = new StopWatch();

	@Test
	public void appiumAppTest() throws Exception {
		// Replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it
		// as maven properties: -DcloudName=<<cloud name>>
		String cloudName = "<<cloud name>>";
		// //Replace <<security token>> with your perfecto security token or pass it as
		// maven properties: -DsecurityToken=<<SECURITY TOKEN>> More info:
		// https://developers.perfectomobile.com/display/PD/Generate+security+tokens
		String securityToken = "<<security token>>";
		String platformName = "Android";
		DesiredCapabilities capabilities = new DesiredCapabilities("mobileOS", "", Platform.ANY);
		capabilities.setCapability("securityToken", PerfectoLabUtils.fetchSecurityToken(securityToken));
		capabilities.setCapability("useAppiumForWeb", "true");
		capabilities.setCapability("model", "Galaxy.*");
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability("openDeviceTimeout", 15);
		capabilities.setCapability("appPackage", "com.samsung.android.messaging");
		capabilities.setCapability("autoLaunch", true);
		capabilities.setCapability("automationName", "Appium");
		driver = new RemoteWebDriver(new URL("https://" + PerfectoLabUtils.fetchCloudName(cloudName)
				+ ".perfectomobile.com/nexperience/perfectomobile/wd/hub"), capabilities);
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);

		reportiumClient = PerfectoLabUtils.setReportiumClient(driver, reportiumClient); // Creates reportiumClient
		reportiumClient.testStart("SUT Performance test", new TestContext("tag1")); // Starts the reportium test

		reportiumClient.stepStart("start virtual network");

		Map<String, Object> pars = new HashMap<>();
		pars.put("profile", "4g_lte_good");
		pars.put("generateHarFile", "true");
		driver.executeScript("mobile:vnetwork:start", pars);

		// Set location
		reportiumClient.stepStart("set location");
		Map<String, Object> params = new HashMap<>();
		params.put("address", "Chennai, India");
		driver.executeScript("mobile:location:set", params);

		// Run background application
		reportiumClient.stepStart("start background apps");
		String backGroundApps = "YouTube,Messages,Maps,Calculator,Calendar,Chrome";
		String[] bApps = backGroundApps.split(",");
		for (String i : bApps) {
			try {
				Map<String, Object> params1 = new HashMap<>();
				params1.put("name", i);
				driver.executeScript("mobile:application:open", params1);
			} catch (Exception e) {
			}
		}

		// Device Vitals
		reportiumClient.stepStart("start device vitals");
		Map<String, Object> params2 = new HashMap<>();
		params2.put("sources", "Device");
		driver.executeScript("mobile:monitor:start", params2);

		// Method 1: User experience timer with Visual Text
		// Launch Web application
		reportiumClient.stepStart("User experience timer with Visual text");
		switchToContext(driver, "WEBVIEW");
		driver.get("https://www.perfecto.io");
		Thread.sleep(2000);
		driver.get("https://www.etihad.com/en-us/book");

		TextValidation(driver, "Book a flight");

		// Measure UX timer 1 - Able to retrieve UX Timer value
		long AppLaunchTime = timerGet(driver, "ux");
		System.out.println("Captured UX time for App launch is: " + AppLaunchTime + "ms");

		// Wind Tunnel: Add timer to Wind Tunnel Report
		reportTimer(driver, AppLaunchTime, 10000, "Checkpoint load time of App launch.", "AppLaunchTime");

		// Method 2: Custom timer for xpaths
		reportiumClient.stepStart("Custom timer with xpath");
		driver.get("https://www.perfecto.io");
		Thread.sleep(2000);
		startTimer(pageLoad);
		driver.get("https://www.etihad.com/en-us/book");
		try {
			driver.findElement(By.xpath("//*[@class=\"header-text-logo\"]//a"));
			stopTimer(pageLoad);
			measureTimer(driver, pageLoad, 10000, "Checkpoint load time of App launch.", "AppLaunchTime-xpath");
		} catch (Exception e) {
		}
		// stop Network Virtualization
		reportiumClient.stepStart("stop virtual network and device vitals");
		Map<String, Object> pars1 = new HashMap<>();
		driver.executeScript("mobile:vnetwork:stop", pars1);

		// stop vitals
		Map<String, Object> params3 = new HashMap<>();
		driver.executeScript("mobile:monitor:stop", params3);
	}

	private static void switchToContext(RemoteWebDriver driver, String context) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", context);
		executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
	}

	private static long timerGet(RemoteWebDriver driver, String timerType) {
		String command = "mobile:timer:info";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", timerType);
		params.put("timerId", "myTime");
		long result = (long) driver.executeScript(command, params);
		return result;
	}

	private static void TextValidation(RemoteWebDriver driver, String content) {
		// verify that the correct page is displayed as result of signing in.
		Map<String, Object> params1 = new HashMap<>();
		// Check for the text that indicates that the sign in was successful
		params1.put("content", content);
		// allow up-to 30 seconds for the page to display
		params1.put("timeout", "40");
		// Wind Tunnel: Adding accurate timers to text checkpoint command
		params1.put("measurement", "accurate");
		params1.put("source", "camera");
		params1.put("analysis", "automatic");
		params1.put("threshold", "90");
		params1.put("index", "1");
		String resultString = (String) driver.executeScript("mobile:checkpoint:text", params1);
	}

	public static String reportTimer(RemoteWebDriver driver, long result, long threshold, String description,
			String name) {
		Map<String, Object> params = new HashMap<String, Object>(7);
		params.put("result", result);
		params.put("threshold", threshold);
		params.put("description", description);
		params.put("name", name);
		String status = (String) driver.executeScript("mobile:status:timer", params);
		return status;
	}

	public static void startTimer(StopWatch pageLoad) {
		pageLoad.start();
	}

	public static void stopTimer(StopWatch pageLoad) {
		pageLoad.stop();
	}

	public static void measureTimer(RemoteWebDriver driver, StopWatch pageLoad, long threshold, String description,
			String name) throws Exception {
		long result = pageLoad.getTime() > 820 ? pageLoad.getTime() - 820 : 0;
		System.out.println("Captured custom time for App launch is: " + result + "ms");
		reportTimer(driver, result, threshold, description, name);
		if (result > threshold) {
			throw new Exception("Timer for " + description + " failed!");
		}
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {
		try {
			TestResult testResult = null;
			if (result.getStatus() == result.SUCCESS) {
				testResult = TestResultFactory.createSuccess();
			} else if (result.getStatus() == result.FAILURE) {
				testResult = TestResultFactory.createFailure(result.getThrowable());
			}
			reportiumClient.testStop(testResult);
			// Retrieve the URL to the DigitalZoom Report
			String reportURL = reportiumClient.getReportUrl();
			System.out.println(reportURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver.quit();

	}

}
