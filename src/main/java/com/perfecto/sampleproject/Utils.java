package com.perfecto.sampleproject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;

public class Utils {
	/**
	 * fetches cloud name
	 * @param cloudName
	 * @return
	 * @throws Exception
	 */
	public static String fetchCloudName(String cloudName) throws Exception {
		//Verifies if cloudName is hardcoded, else loads from Maven properties 
		String finalCloudName = cloudName.equalsIgnoreCase("<<cloud name>>") ? System.getProperty("cloudName") : cloudName;
		//throw exceptions if cloudName isnt passed:
		if(finalCloudName == null || finalCloudName.equalsIgnoreCase("<<cloud name>>"))
			throw new Exception("Please replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>");
		else
			return finalCloudName;
	}

	/**
	 * Fetches security token
	 * @param securityToken
	 * @return
	 * @throws Exception
	 */
	public static String fetchSecurityToken(String securityToken) throws Exception {
		//Verifies if securityToken is hardcoded, else loads from Maven properties
		String finalSecurityToken = securityToken.equalsIgnoreCase("<<security token>>") ? System.getProperty("securityToken") : securityToken;
		//throw exceptions if securityToken isnt passed:
		if(finalSecurityToken == null || finalSecurityToken.equalsIgnoreCase("<<security token>>"))
			throw new Exception("Please replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>");
		else
			return finalSecurityToken;
	}

	/**
	 * Creates reportium client
	 * @param driver
	 * @param reportiumClient
	 * @return
	 */
	public static ReportiumClient setReportiumClient(RemoteWebDriver driver, ReportiumClient reportiumClient) {
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
		return reportiumClient;
	}
	
	/**
	 * Asserts text
	 * @param WebElement
	 * @param reportiumClient
	 * @param text
	 */
	public static void assertText(WebElement data, ReportiumClient reportiumClient, String text) {
		assert data.getText().equals(text) : "Actual text : " + data.getText() + ". It did not match with expected text: " +  text;
		if(reportiumClient != null)
			reportiumClient.reportiumAssert("Verify Field: " + data.getText() , data.getText().equals(text));
	}
	
	/**
	 * Assert title
	 * @param title
	 * @param reportiumClient
	 */
	public static void assertTitle(String title, ReportiumClient reportiumClient) {
		if (!title.equals("Web & Mobile App Testing | Continuous Testing | Perfecto")) {
			reportiumClient.reportiumAssert("Title is mismatched", false);
			throw new RuntimeException("Title is mismatched");
		}else {
			reportiumClient.reportiumAssert("Title is matching", true);
		}
	}

}

