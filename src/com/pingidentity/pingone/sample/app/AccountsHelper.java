package com.pingidentity.pingone.sample.app;

import java.util.List;
import java.util.logging.Logger;

import com.pingidentity.pingone.sample.model.Account;



public class AccountsHelper {
	private static final Logger logger = Logger.getLogger(AccountsHelper.class.getName());

	private DataManager dm;

	public AccountsHelper() {
		dm = DataManager.getInstance();
		List<Account> accounts = dm.getAccountsList();
	}
}
