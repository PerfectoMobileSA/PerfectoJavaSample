package io.perfecto.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.ios.IOSDriver;

public class PerfectoIOSDriverFactory {

	public static RemoteWebDriver createPerfectoDriver(String cloudName, DesiredCapabilities capabilities)
			throws MalformedURLException {

		RemoteWebDriver driver = null;

		String cloudUrl = String.format("https://%s.perfectomobile.com/nexperience/perfectomobile/wd/hub", cloudName);

		driver = (RemoteWebDriver) (new IOSDriver(new URL(cloudUrl), capabilities));

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

		return driver;

	}

}
