package com.pingidentity.pingone.sample.sso;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.pingidentity.pingone.sample.model.Principal;



public class Authenticate {
	private static Logger julLogger = Logger.getLogger(Authenticate.class.getName());
	//private static final String baseUrl = "https://sso.connect.pingidentity.com/sso/TXS/2.0/1/";
	private Client jerseyClient;
	private SSOConfig ssoConfig;
	public Authenticate(SSOConfig ssoConfig) {
		this.ssoConfig = ssoConfig;
		this.jerseyClient = ClientBuilder.newBuilder()
				.sslContext(getSSLContext("TLS"))
				.hostnameVerifier(getHostnameVerifier()).build();
		if (ssoConfig.getClientId() != null && ssoConfig.getClientSecret() != null) {
			jerseyClient.register(new HttpBasicAuthFilter(ssoConfig.getClientId(),ssoConfig.getClientSecret()));
		}
		this.jerseyClient.register(JacksonFeature.class).register(
				new LoggingFilter(julLogger, true));
	}

	public Object  getAuthentication(String tokenId) {
		WebTarget resource = jerseyClient.target(ssoConfig.getBaseUrl()
				+ tokenId);
		Response response = resource.request(MediaType.APPLICATION_JSON).get(
				Response.class);
		if(response.getStatus() == 200) {
			Map<String,Object> map = response.readEntity(new GenericType<Map<String,Object>>() {});
			Principal principal = new Principal();
			for(String key : map.keySet()) {
				Object values = map.get(key);	
				if(values instanceof String) {
					principal.setAttribute(key, (String)values);
				}
			}
			principal.setEmail((String)map.get("subject"));
			
			// If no roles were declared, add the "User" role
			if(principal.getRoles() == null || principal.getRoles().size() == 0) {
				principal.setRoles(new ArrayList<String>(){{add("user");}});
			}
			return principal;
		} else {
			String string = response.readEntity(String.class);
			return string;
		}
	}
	
	
	private static HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hv = new HostnameVerifier() {

			@Override
			public boolean verify(String hostName, SSLSession sslSession) {
				return true;
			}
		};

		return hv;
	}

	private static SSLContext getSSLContext(String version) {

		TrustManager[] trustAllCerts = getTrustManager();
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance(version);

			// set up a TrustManager that trusts everything
			sslContext.init(null, trustAllCerts, new SecureRandom());

			return sslContext;
		} catch (KeyManagementException ex) {
			julLogger.log(Level.SEVERE,
					null, ex);
			return null;
		} catch (NoSuchAlgorithmException ex) {
			julLogger.log(Level.SEVERE,
					null, ex);
			return null;
		}
	}

	private static TrustManager[] getTrustManager() {
		return new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
	}

}
