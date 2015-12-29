package com.infobip.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infobip.util.Util;





public class JndiHandler {
	
	public static final int LOCAL_JNDI_PORT = 1099;
	
	private static final Logger logger = LoggerFactory.getLogger(JndiHandler.class);
	
	@SuppressWarnings("unchecked")
	public static <T> T lookupCrossServerWildfly9(String jndiName) throws NamingException {
		
		if (Util.isEmpty(jndiName)) {
			throw new IllegalArgumentException("Invalid jndiName");
		}
				    
		Properties properties = new Properties();    
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");    
		InitialContext ctx = null;
		Context ejbRootNamingContext = null;

		try {

			ctx = new InitialContext(properties);
			ejbRootNamingContext = (Context) ctx.lookup("ejb:");
			jndiName = "ejb:" + jndiName;
			T service = (T) ctx.lookup(jndiName);
			logger.info("Connected to service on ", new Object[] { jndiName });
			return service;
		} catch (NamingException e) {
			logger.error(e.getMessage());
		} finally {
			if (ejbRootNamingContext != null) {
				ejbRootNamingContext.close();
			}
			if (ctx != null) {
				ctx.close();
			}

		}

		throw new NamingException("Service " + jndiName + " not available");
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T lookupCrossServerWildfly9HttpRemoting(String jndiName, String[] jndiHosts, int port) throws NamingException {
		
		if (Util.isEmpty(jndiName)) {
			throw new IllegalArgumentException("Invalid jndiName");
		}
		if (jndiHosts == null || jndiHosts.length == 0) {
			throw new IllegalArgumentException("Invalid jndiHosts");
		}
		
		Properties properties = new Properties();
		
		InitialContext ctx = null;
		for (int i = 0; i < jndiHosts.length; i++) {
			String host = jndiHosts[i];
			try {
				
				properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
				properties.put(Context.PROVIDER_URL, "http-remoting://" + host + ":" + port);
				properties.put("jboss.naming.client.ejb.context", true);
				// The environment property jboss.naming.client.ejb.context indicates that the InitialContext implementation 
				// of the remote naming project will also create an internal EJB client context via the EJB client library. 
				// This allows the invocation of EJB components with the remote naming project.
				ctx = new InitialContext(properties);
				
				T service = (T) ctx.lookup(jndiName);
				
				logger.info("Connected to service {} on {}:{}", new Object[] { jndiName, host, port });
				return service;
			} catch (NamingException e) {
				logger.error(e.getMessage() + " on " + host);
				continue;
			} 
			// All proxies become invalid if .close() for the related javax.naming.InitalContext is invoked,
			// or the InitialContext is not longer referenced and gets garbage-collected.
			
		}
		throw new NamingException("Service not available on specified servers " + jndiHosts.toString());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T lookupCrossServerJBoss6(String jndiName, String[] jndiHosts, int port) throws NamingException {
		
		if (Util.isEmpty(jndiName)) {
			throw new IllegalArgumentException("Invalid jndiName");
		}
		if (jndiHosts == null || jndiHosts.length == 0) {
			throw new IllegalArgumentException("Invalid jndiHosts");
		}
		
		Properties properties = new Properties();
		
		InitialContext ctx = null;
		for (int i = 0; i < jndiHosts.length; i++) {
			String host = jndiHosts[i];
			try {
				properties.setProperty("java.naming.provider.url", host + ":" + port);
				properties.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
				properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
				ctx = new InitialContext(properties);
				T service = (T) ctx.lookup(jndiName);
				logger.info("Connected to service {} on {}:{}", new Object[] { jndiName, host, port });
				return service;
			} catch (NamingException e) {
				logger.error(e.getMessage() + " on " + host);
				continue;
			} finally {
				if (ctx != null) {
					ctx.close();
				}
			}
		}
		throw new NamingException("Service not available on specified servers " + jndiHosts.toString());
	}
}
