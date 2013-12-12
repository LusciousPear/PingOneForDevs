package com.pingidentity.pingone.sample.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pingidentity.pingone.sample.model.Account;

public class DataManager {
	private static final Logger logger = Logger.getLogger(DataManager.class.getName());
	private static DataManager instance=null;
	private static AppConfig config;
	private static final String DB_DRIVER = "database.driver";
	private static final String DB_URL = "database.url";
	private static final String DB_USER = "database.user";
	private static final String DB_PWD = "database.password";
	private Connection conn = null;

	protected DataManager() {
		config = AppConfig.getInstance();
		try {
			String dbDriver = config.getProperty(DB_DRIVER);
			String dbUrl = config.getProperty(DB_URL);
			String dbUser = config.getProperty(DB_USER);
			String dbPwd = config.getProperty(DB_PWD);
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			
			// Create prepared statements
			selectAccounts = conn.prepareStatement(selectAccountsSQL);
		} catch (SQLException se) {
			logger.log(Level.SEVERE,null,se);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE,null, e);
		}
		
	}
	
	public static DataManager getInstance() {
		if(instance == null) {
			instance = new DataManager();
		}
		return instance;
	}
	
	public Connection getConnection() {
		return this.conn;
	}

	private final String selectAccountsSQL="SELECT * FROM Account";
	private PreparedStatement selectAccounts = null;

	public List<Account> getAccountsList() {
		List<Account> accounts = new ArrayList<Account>();
	
		try {
			ResultSet rs = selectAccounts.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			if (rs != null) {
				while(rs.next()) {
					Account account = new Account();
					account.setId(rs.getInt("id"));
					account.setName(rs.getString("name"));
					account.setSsoEnabled(rs.getBoolean("ssoEnabled"));
					if(account.isSsoEnabled()) {
						account.setSsoIdpId(rs.getString("ssoIdpId"));
					}
					accounts.add(account);
				}
			}
			
		} catch (SQLException e) {
			logger.log(Level.SEVERE,null,e);
		}
		return accounts;
	}
}
