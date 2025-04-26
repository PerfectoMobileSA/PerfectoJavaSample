package com.perfecto.sampleproject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.PerfectoExecutionContext.PerfectoExecutionContextBuilder;
import com.perfecto.reportium.model.Project;

import io.perfecto.utils.ApplicationPropertiesUtil;

public class PerfectoLabUtils {
//	private static final String HTTPS = "https://";
//	private static final String MEDIA_REPOSITORY = "/services/repositories/media/";
//	private static final String UPLOAD_OPERATION = "operation=upload&overwrite=true";
//	private static final String UTF_8 = "UTF-8";

	/**
	 * fetches cloud name
	 * 
	 * @param cloudName
	 * @return
	 * @throws Exception
	 */
	public static String getPerfectoCloudName() throws Exception {

		String cloudName = ApplicationPropertiesUtil.getProperty("perfecto.cloud.name");

		if (null == cloudName) {
			throw new Exception(
					"Please provide cloud name using 'perfecto.cloud.name' property in application.properties file or Env or System variable.");
		} else {
			return cloudName;
		}

//		// Verifies if cloudName is hardcoded, else loads from Maven properties
//		String finalCloudName = cloudName.equalsIgnoreCase("<<cloud name>>") ? System.getProperty("cloudName")
//				: cloudName;
//		// throw exceptions if cloudName isnt passed:
//		if (finalCloudName == null || finalCloudName.equalsIgnoreCase("<<cloud name>>"))
//			throw new Exception(
//					"Please replace <<cloud name>> with your perfecto cloud name (e.g. demo) or pass it as maven properties: -DcloudName=<<cloud name>>");
//		else
//			return finalCloudName;
	}

	/**
	 * Fetches security token
	 * 
	 * @param securityToken
	 * @return
	 * @throws Exception
	 */
	public static String getSecurityToken() throws Exception {

		String securityToken = ApplicationPropertiesUtil.getProperty("perfecto.security.token");

		if (null == securityToken) {
			throw new Exception(
					"Please provide cloud name using 'perfecto.security.token' property in application.properties file or Env or System variable.");
		} else {
			return securityToken;
		}

//		// Verifies if securityToken is hardcoded, else loads from Maven properties
//		String finalSecurityToken = securityToken.equalsIgnoreCase("<<security token>>")
//				? System.getProperty("securityToken")
//				: securityToken;
//		// throw exceptions if securityToken isnt passed:
//		if (finalSecurityToken == null || finalSecurityToken.equalsIgnoreCase("<<security token>>"))
//			throw new Exception(
//					"Please replace <<security token>> with your perfecto security token or pass it as maven properties: -DsecurityToken=<<SECURITY TOKEN>>");
//		else
//			return finalSecurityToken;
	}

	/**
	 * Creates reportium client
	 * 
	 * @param driver
	 * @param reportiumClient
	 * @return
	 * @throws Exception
	 */
	public static ReportiumClient setReportiumClient(RemoteWebDriver driver, ReportiumClient reportiumClient)
			throws Exception {
		
		// Reporting client. For more details, see
		// https://developers.perfectomobile.com/display/PD/Java

		PerfectoExecutionContextBuilder perfectoExecutionContextBuilder = new PerfectoExecutionContext.PerfectoExecutionContextBuilder();

		String jobName = ApplicationPropertiesUtil.getProperty("reportium.job.name");

		if (null == driver) {
			throw new Exception("Set Report Client : Driver can't be null");
		}

		perfectoExecutionContextBuilder = perfectoExecutionContextBuilder.withWebDriver(driver);

		if (null != jobName) {

			int jobNumber = 0;

			String jobNumberStr = ApplicationPropertiesUtil.getProperty("reportium.job.number");

			if (null == null) {
				jobNumber = Integer.parseInt(jobNumberStr);
			}
			Job jobReference = new Job(jobName, jobNumber);
			perfectoExecutionContextBuilder = perfectoExecutionContextBuilder.withJob(jobReference);
		}

		String projectName = ApplicationPropertiesUtil.getProperty("reportium.project.name");

		if (null != projectName) {

			String projectNumber = ApplicationPropertiesUtil.getProperty("reportium.project.number");

			if (null == projectNumber) {
				projectNumber = "1.0";
			}

			Project project = new Project(projectName, projectNumber);
			perfectoExecutionContextBuilder = perfectoExecutionContextBuilder.withProject(project);
		}

		String tags = ApplicationPropertiesUtil.getProperty("reportium.tags");

		if (tags != null) {
			List<String> tagsList = Arrays.asList(tags.split(","));
			perfectoExecutionContextBuilder = perfectoExecutionContextBuilder.withContextTags(tagsList);
		}

		PerfectoExecutionContext perfectoExecutionContext = perfectoExecutionContextBuilder.build();

		reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
		if (reportiumClient == null) {
			throw new Exception("Reportium client not created!");
		}
		
		return reportiumClient;

//				if (System.getProperty("reportium-job-name") != null) {
//					perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
//							.withProject(new Project("My Project", "1.0"))
//							.withJob(new Job(System.getProperty("reportium-job-name"),
//									Integer.parseInt(System.getProperty("reportium-job-number"))))
//							.withContextTags("tag1").withWebDriver(driver).build();
//				} else {
//					perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
//							.withProject(new Project("My Project", "1.0")).withContextTags("tag1").withWebDriver(driver)
//							.build();
//				}
	}

	/**
	 * Asserts text
	 * 
	 * @param WebElement
	 * @param reportiumClient
	 * @param text
	 */
	public static void assertText(WebElement element, ReportiumClient reportiumClient, String text) {
		String elementText = element.getText();
		if (reportiumClient != null)
			reportiumClient.reportiumAssert("Verify Field: " + elementText, elementText.equals(text));
		assert elementText.equals(text)
				: "Actual text : " + elementText + ". It did not match with expected text: " + text;
	}

	/**
	 * Asserts contains text
	 * 
	 * @param WebElement
	 * @param reportiumClient
	 * @param text
	 */
	public static void assertContainsText(WebElement element, ReportiumClient reportiumClient, String text) {
		String elementText = element.getText();
		if (reportiumClient != null)
			reportiumClient.reportiumAssert("Verify Field: " + elementText, elementText.contains(text));
		assert elementText.contains(text)
				: "Actual text : " + elementText + " does not contain the expected text: " + text;
	}

	/**
	 * Assert title
	 * 
	 * @param title
	 * @param reportiumClient
	 */
	public static void assertTitle(String title, ReportiumClient reportiumClient) {
		if (!title.equals("Google")) {
			reportiumClient.reportiumAssert("Title is mismatched", false);
			throw new RuntimeException("Title is mismatched");
		} else {
			reportiumClient.reportiumAssert("Title is matching", true);
		}
	}

	public static String getDevicePhoneNumber(RemoteWebDriver driver) {
		Map<String, Object> params1 = new HashMap<>();
		params1.put("property", "phoneNumber");
		return (String) driver.executeScript("mobile:handset:info", params1);
	}

	/**
	 * Download the report. type - pdf, html, csv, xml Example:
	 * downloadReport(driver, "pdf", "C:\\test\\report");
	 *
	 * Note that this method is relevant only for local hosted device lab (AKA "On
	 * Premise") and not for DigitalZoom (AKA ReportiumClient) users
	 */
	public static void downloadReport(RemoteWebDriver driver, String type, String fileName) throws IOException {
		try {
			String command = "mobile:report:download";
			Map<String, Object> params = new HashMap<>();
			params.put("type", type);
			String report = (String) driver.executeScript(command, params);
			File reportFile = new File(fileName + "." + type);
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(reportFile));
			byte[] reportBytes = OutputType.BYTES.convertFromBase64Png(report);
			output.write(reportBytes);
			output.close();
		} catch (Exception ex) {
			System.out.println("Got exception " + ex);
		}
	}

	/**
	 * Download all the report attachments with a certain type. type - video, image,
	 * vital, network Examples: downloadAttachment(driver, "video",
	 * "C:\\test\\report\\video", "flv"); downloadAttachment(driver, "image",
	 * "C:\\test\\report\\images", "jpg");
	 *
	 * Note that this method is relevant only for local hosted device lab (AKA "On
	 * Premise") and not for DigitalZoom (AKA ReportiumClient) users
	 */
	public static void downloadAttachment(RemoteWebDriver driver, String type, String fileName, String suffix)
			throws IOException {
		try {
			String command = "mobile:report:attachment";
			boolean done = false;
			int index = 0;

			while (!done) {
				Map<String, Object> params = new HashMap<>();

				params.put("type", type);
				params.put("index", Integer.toString(index));

				String attachment = (String) driver.executeScript(command, params);

				if (attachment == null) {
					done = true;
				} else {
					File file = new File(fileName + index + "." + suffix);
					BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
					byte[] bytes = OutputType.BYTES.convertFromBase64Png(attachment);
					output.write(bytes);
					output.close();
					index++;
				}
			}
		} catch (Exception ex) {
			System.out.println("Got exception " + ex);
		}
	}

	/**
	 * Uploads a file to the media repository as per new API Example:
	 * uploadMedia("demo", "securityToken", "C:\\test\\ApiDemos.apk",
	 * "PRIVATE:apps/ApiDemos.apk");
	 * 
	 * @throws Exception
	 */
	public static void uploadMedia(String cloudName, String securityToken, String path, String artifactLocator)
			throws Exception {

		StopWatch stopwatch = new StopWatch();
		stopwatch.start();

		System.out.println("Upload Started");
		URIBuilder taskUriBuilder = new URIBuilder(
				"https://" + cloudName + ".app.perfectomobile.com/repository/api/v1/artifacts");
		// Set timeouts
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();

		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

		HttpPost httppost = new HttpPost(taskUriBuilder.build());
		httppost.setHeader("Perfecto-Authorization", securityToken);

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		File packagedFile = new File(path);
		ContentBody inputStream = new FileBody(packagedFile, ContentType.APPLICATION_OCTET_STREAM);

		JSONObject req = new JSONObject();
		req.put("artifactLocator", artifactLocator);
		req.put("override", true);
		String rp = req.toString();

		ContentBody requestPart = new StringBody(rp, ContentType.APPLICATION_JSON);
		mpEntity.addPart("inputStream", inputStream);
		mpEntity.addPart("requestPart", requestPart);
		httppost.setEntity(mpEntity.build());
		HttpResponse response = httpClient.execute(httppost);
		int statusCode = response.getStatusLine().getStatusCode();

		stopwatch.stop();

		long x = stopwatch.getTime(TimeUnit.SECONDS);

//				getTime();
		System.out.println("Status Code = " + statusCode);
		System.out.println("Upload Time = " + Long.toString(x));
		HttpEntity entity = response.getEntity();
		if (statusCode == 400) {
			if (entity != null) {
				String responseBody = EntityUtils.toString(entity);
				throw new Exception(responseBody);
			}
		}
		if (statusCode == 403) {
			if (entity != null) {
				String responseBody = EntityUtils.toString(entity);
				throw new Exception("Forbidden access! Response body: " + responseBody);
			}
		}
	}
}
