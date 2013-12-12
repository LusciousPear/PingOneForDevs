package com.pingidentity.pingone.sample;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pingidentity.pingone.sample.app.AppConfig;
import com.pingidentity.pingone.sample.model.Principal;
import com.pingidentity.pingone.sample.sso.Authenticate;
import com.pingidentity.pingone.sample.sso.SSOConfig;

import java.util.logging.Logger;


/**
 * Servlet implementation class SSO
 */
@WebServlet("/sso")
public class SSO extends HttpServlet {
	private static final Logger logger = Logger.getLogger(SSO.class.getName());
	private static final long serialVersionUID = 1L;
	private AppConfig config;
	public final static String AUTHENTICATED_ATTR = "authenticated";
	public final static String PRINCIPAL_ATTR = "principal";
	public final static String LOGIN_ERROR_ATTR = "login_error";
	private static final String TOKEN_ID_PARAM ="tokenid";
    private static final String REST_API_CLIENT_ID="pingone.rest.client";
    private static final String REST_API_CLIENT_SECRET = "pingone.rest.secret";
    private static final String REST_API_BASE_URL = "pingone.rest.baseurl";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SSO() {
        super();
        config = AppConfig.getInstance();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGetOrPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGetOrPost(request,response);
	}

	/**
	 * This Servlet will be called whenever someone is redirected to the app via IdP-Init.  A tokenId 
	 * will be in the query parameters.  
	 * 
	 * The tokenId is actually a reference identifier that points to the attribute data associated
	 * with this event in PingOne.  The tokenId is passed to PingOne via a back-channel, ReST 
	 * call.  The call will return a JSON structure containing the user attributes of the 
	 * authenticated user, along with attributes about who the IdP is and how the user was
	 * actually authenticated.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void doGetOrPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession(true);		
		String tokenId = getTokenFromRequest(request,response);
		if(tokenId == null)
			return;
		logger.info("Received token id: " + tokenId);
		SSOConfig ssoConfig = getSSOConfig();
		Authenticate authN = new Authenticate(ssoConfig);
		Object obj = authN.getAuthentication(tokenId);
		if(obj != null && obj instanceof Principal) {
			session.setAttribute(AUTHENTICATED_ATTR, new Boolean(true));
			session.setAttribute(PRINCIPAL_ATTR, (Principal)obj);
    		RequestDispatcher disp = getServletContext(  ).getRequestDispatcher("/index.jsp");
    		disp.forward(request, response);       	
		} else {
			session.setAttribute("authenticated", false);
			session.removeAttribute(PRINCIPAL_ATTR);
			session.setAttribute(LOGIN_ERROR_ATTR, (String)obj);
    		RequestDispatcher disp = getServletContext(  ).getRequestDispatcher("/sso_error.jsp");
    		disp.forward(request, response);       	
			
		}		
	}
	
	private String getTokenFromRequest(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		String tokenId = request.getParameter(TOKEN_ID_PARAM);
		if(tokenId == null) {
			String err = "No token id was provided";
			logger.warning(err);
			sendError(request,response,err);
			return null;
		}else if ("".equals(tokenId)) {
			String err = "Empty token id was recieved";
			logger.warning(err);
			sendError(request,response,err);
			return null;
	    }
		return tokenId;
	}
	
	private void sendError(HttpServletRequest request, HttpServletResponse response, String error) {
		HttpSession session = request.getSession(true);
		session.setAttribute("sso_error", error);		
		RequestDispatcher disp = getServletContext(  ).getRequestDispatcher("/sso_error.jsp");
		try {
			disp.forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       		
	}
	
	/**
	 * Grab all the parameters that the Authenticate routine will need
	 * These are currently stored in the AppConfig object.
	 * @return
	 */
	private SSOConfig getSSOConfig() {
		SSOConfig ssoConfig = new SSOConfig();
		
		ssoConfig.setClientId( config.getProperty(REST_API_CLIENT_ID));
		ssoConfig.setClientSecret( config.getProperty(REST_API_CLIENT_SECRET));
		ssoConfig.setBaseUrl( config.getProperty(REST_API_BASE_URL));
		
		return ssoConfig;
	}
}
