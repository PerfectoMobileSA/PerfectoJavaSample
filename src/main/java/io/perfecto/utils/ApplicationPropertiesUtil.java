package io.perfecto.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ApplicationPropertiesUtil {
	
	private static Properties applicationProperties;
	
	public static String getProperty(String propertyKey) throws FileNotFoundException, IOException {
		
		if(applicationProperties == null) {
			
			applicationProperties = new Properties();
			
			File propertiesFile = new File("application.properties");
			
			applicationProperties.load(new FileReader(propertiesFile));
		}
		
		if(applicationProperties.containsKey(propertyKey)) {
			return applicationProperties.getProperty(propertyKey);
		}else {
			return System.getProperty(propertyKey, System.getenv(propertyKey));
		}
		
	}

}
