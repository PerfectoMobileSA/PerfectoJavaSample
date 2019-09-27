package com.perfecto.sampleproject;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;


public class LocalSelenium {

	@Test
	public void main() {
		//Note: Download chromeDriver for windows and update the below if running from Windows.
		System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir") + "//libs//chromedriver");
		//A sample chrome driver script to access perfecto website and verify the title
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
		driver.get("https://www.perfecto.io");
		String aTitle = driver.getTitle();
		System.out.println(aTitle);
		//compare the actual title with the expected title
		if (aTitle.equals("Web & Mobile App Testing | Continuous Testing | Perfecto"))
		{
			System.out.println( "Test Passed") ;
		}
		else {
			System.out.println( "Test Failed" );
		}
		driver.close();
		driver.quit();
	}

}

