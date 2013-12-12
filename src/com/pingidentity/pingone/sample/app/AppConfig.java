package com.pingidentity.pingone.sample.app;

import java.util.Properties;
import java.util.logging.Logger;

public class AppConfig {
	private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
	private static AppConfig instance=null;
	private Properties props = new Properties();
	
	private AppConfig() {
		// Privatize for singleton behavior
	}
	
	public static AppConfig getInstance() {
		if(instance == null) {
			// Initialize singletone
			instance = new AppConfig();
		}
		return instance;
	}
	
	public Properties getProps() {
		return props;
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	public String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}
}
