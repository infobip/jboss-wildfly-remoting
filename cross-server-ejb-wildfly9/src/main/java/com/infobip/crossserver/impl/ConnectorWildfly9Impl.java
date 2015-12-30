package com.infobip.crossserver.impl;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.infobip.classloader.AluniteClassLoader;
import com.infobip.crossserver.IConnector;
import com.infobip.jndi.JndiHandler;
import com.infobip.util.Util;

/**
 * 
 * @author jlazic
 *
 */
@Stateless(mappedName = "ConnectorWildfly9")
public class ConnectorWildfly9Impl implements IConnector, Serializable {

	private static Logger logger = Logger.getLogger(ConnectorWildfly9Impl.class);

	private static final long serialVersionUID = -7020325092416187557L;

	public String hello(String name) {
		ClassLoader previous = Thread.currentThread().getContextClassLoader();

		if (Util.isEmpty(name)) {
			logger.info("Hello, this is Wildfly9. Please, identify yourself!");
			return "Please, fill in your name and try again.";
		}
	
		try {

			Properties properties = new Properties(); 
			InputStream is = (ConnectorWildfly9Impl.class.getClassLoader().getResourceAsStream("location.properties"));
			
			properties.load(is);
			String jbossHome = properties.getProperty("serverHome");
			
			// don't set a parent, so we run in complete isolation.
			URLClassLoader urlCl = new URLClassLoader(
					new URL[] { new URL(new File(jbossHome).toURI().toURL(), "jbossall-client.jar") });
			// since we're running in isolation my own interface needs to be
			// added.
			ClassLoader cl = new AluniteClassLoader(urlCl, previous);

			Thread.currentThread().setContextClassLoader(cl);

			IConnector connector = (IConnector) JndiHandler.lookupCrossServerJBoss6("ConnectorJBoss6",
					new String[] { "localhost" }, JndiHandler.LOCAL_JNDI_PORT);
			String answer = connector.answer(name);
			logger.info("JBoss6 is answering: " + answer);
		} catch (Exception e) {
			logger.error("Could not reach JBoss6!");
			logger.error(e.getMessage());

		} finally {
			// in the end, we return to previous classloader
			Thread.currentThread().setContextClassLoader(previous);
		}

		return name;

	}

	public String answer(String name) {
		logger.info("Hello, " + name + " is calling ... ");
		String answer = "What's up, " + name + "?";
		logger.info("Wildfly9 is answering: " + answer);
		return answer;

	}

}
