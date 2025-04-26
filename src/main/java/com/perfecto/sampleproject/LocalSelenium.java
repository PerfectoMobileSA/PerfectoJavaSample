package com.perfecto.sampleproject;
import static org.testng.Assert.assertTrue;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;


public class LocalSelenium {

	@Test
	public void localTest() {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = null;
		
		try {
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
			
			driver.get("https://www.perfecto.io");
			String aTitle = driver.getTitle();
			//compare the actual title with the expected title
			assertTrue(aTitle.equals("Web & Mobile App Testing | Continuous Testing | Perfecto"), "Title verified as expected");
		}catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}finally {
			if(driver !=null) {
				driver.close();
				driver.quit();
			}
		}
		
		
	}

}

