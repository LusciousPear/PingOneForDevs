package com.pingidentity.pingone.sample.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.pingidentity.pingone.sample.model.Principal;

import java.sql.*;

public class FormAuth {
	public final static String AUTHENTICATED_ATTR = "authenticated";
	public final static String PRINCIPAL_ATTR = "principal";
	public final static String LOGIN_ERROR_ATTR = "login_error";
	private static FormAuth instance = null;

	private static final String userSql = "SELECT User.id as id, User.firstName as firstName,"
			+ " User.lastName as lastName, User.email as email, User_Password.password as password,"
			+ " Account.name as company"
			+ " FROM User JOIN Account ON User.accountId=Account.id"
			+ " JOIN User_Password ON User_Password.userId=User.id"
			+ " WHERE email=?";
	private static final String rolesSql = "select Role.name FROM User_Role"
			+ " JOIN Role ON User_Role.roleId=Role.id"
			+ " WHERE User_Role.userId=?";

	private Connection conn = null;
	private PreparedStatement selectUser = null;
	private PreparedStatement selectRoles = null;
	private DataManager dataManager=DataManager.getInstance();
	/**
	 * This is a singleton. So nobody else can call it.
	 */
	private FormAuth() {
		// Open up the XML file to validate users
		try {
			conn = dataManager.getConnection();
			selectUser = conn.prepareStatement(userSql);
			selectRoles = conn.prepareStatement(rolesSql);
		} catch (SQLException se) {
			se.printStackTrace();
		} 
	}

	/**
	 * Because the authentication method uses a database, a singleton is created
	 * so that the cost of establishing the connection and compiling the
	 * prepared statements is minimized to startup.
	 * 
	 * @return the FormAuth singleton
	 */
	public static FormAuth getInstance() {
		if (instance == null) {
			instance = new FormAuth();
		}
		return instance;
	}

	/**
	 * Logs the current user out and revokes the session.
	 * 
	 * @param request
	 */
	public void Logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		try {
			session.removeAttribute(AUTHENTICATED_ATTR);
			session.removeAttribute(PRINCIPAL_ATTR);
			session.invalidate();
		} catch (IllegalStateException e) {
			return;
		}
	}

	/**
	 * Attemps to authenticate the user. If the user successfully authenticates,
	 * a Principal will be added to the session, along with the 'authenticated'
	 * attribute if the user properly authenticates.
	 * 
	 * If the user fails to authenticate, then Logout is called.
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * @param username
	 *            the user name provided by the user
	 * @param password
	 *            the password provided by the user
	 * @return True if the user successfully authenticates. False otherwise
	 */
	public Boolean Login(HttpServletRequest request, String username,
			String password) {
		HttpSession session = request.getSession(true);
		Principal principal = authenticate(username, password);

		if (principal != null) {
			session.setAttribute(AUTHENTICATED_ATTR, new Boolean(true));
			session.setAttribute(PRINCIPAL_ATTR, principal);
			return true;
		} else {
			session.setAttribute("authenticated", false);
			session.setAttribute(LOGIN_ERROR_ATTR, "Login Failed.  Try again");
			return false;
		}
	}

	/**
	 * Tests to see if the current session user is authenticated
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * @return True if the user is authenticated. False otherwise
	 */
	public Boolean isLoggedIn(HttpServletRequest request) {
		Boolean isValid = new Boolean(true);
		Boolean isNotValid = new Boolean(false);

		HttpSession session = request.getSession(false);

		if (session == null) {
			return isNotValid;
		}
		try {
			Boolean isAuthenticated = (Boolean) session
					.getAttribute(AUTHENTICATED_ATTR);
			if (isAuthenticated == null) {
				return isNotValid;
			}
			return isAuthenticated;
		} catch (IllegalStateException e) {
			return isNotValid;
		}
	}

	/**
	 * Validate a username and password
	 * 
	 * @param user
	 *            the user identifier. Generally an email address
	 * @param password
	 *            the user's password to validate against
	 * @return a valid Principal if the user has successfully authenticated.
	 *         Else null
	 */
	private Principal authenticate(String user, String password) {
		Principal principal = getPrincipal(user);
		if (principal == null) {
			return null;
		}
		if (principal.getPassword().equals(password)) {
			return principal;
		}
		return null;
	}

	/**
	 * Find the user in the user store
	 * 
	 * @param user
	 *            identifier to look up. Generally an email address
	 * @return if successful, a valid Principal, else null
	 */
	private Principal getPrincipal(String user) {
		Principal principal = null;

		try {
			selectUser.setString(1, user);
			ResultSet rs = selectUser.executeQuery();
			if (rs == null) {
				return null;
			}
			if (rs.next()) {
				// Found the user
				principal = new Principal();
				// Get the id so we can look up roles
				selectRoles.setInt(1, rs.getInt("id"));
				ResultSet roles_rs = selectRoles.executeQuery();
				principal.setFirstName(rs.getString("firstName"));
				principal.setLastName(rs.getString("lastName"));
				principal.setCompany(rs.getString("company"));
				principal.setEmail(rs.getString("email"));
				principal.setPassword(rs.getString("password"));

				while (roles_rs.next()) {
					principal.addRole(roles_rs.getString("name"));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return principal;
	}
}
