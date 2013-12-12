package com.pingidentity.pingone.sample.app;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener{
	private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// Set up the config
		try {
			// Get the physical path to the app config file
			String configPath = event.getServletContext().getRealPath("/conf/app.properties");	
			logger.info("App properties file: " + configPath);
			FileReader reader = new FileReader(configPath);
			
			// Create the AppConfig instance and load the config file
			AppConfig config = AppConfig.getInstance();
			config.getProps().load(reader);
			
			// Rectify the database file path in the database url
			String dbPath = event.getServletContext().getRealPath(config.getProperty("database.path"));
			config.setProperty("database.path",dbPath);
			String dbUrl = config.getProperty("database.url").replace("{path}", dbPath);
			config.setProperty("database.url", dbUrl);
			dbUrl = config.getProperty("database.url");
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Failure loading app config file.  Could not find the file.", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failure loading app config file. Could not read the file.", e);
		}		
	}

}
