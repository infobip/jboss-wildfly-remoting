package com.infobip.crossserver.impl;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.ejb.Stateless;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.infobip.mpayments.util.classloader.AluniteClassLoader;
import org.infobip.mpayments.util.jndi.JndiHandler;
import org.infobip.mplatform.common.util.StringUtils;


import com.infobip.crossserver.IConnector;

@Stateless(mappedName= "ConnectorJBoss6")
public class ConnectorJBoss6Impl implements IConnector, Serializable {

	private static final long serialVersionUID = -7020325092416187557L;
	
	private static Logger logger = Logger.getLogger(ConnectorJBoss6Impl.class);
	
	public String hello(String name) {
		
		if (StringUtils.isEmpty(name))
		{
			logger.info("Hello, this is JBoss6. Please, identify yourself!");
			return "Please, fill in your name and try again.";
		}
		
		logger.info("Hello, you have reached JBOSS6, " + name);

		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		
		try {
			String jbossHome = "D:/wildfly-9.0.2.Final - instance1/bin";
			
			// add wildfly client library			
			// don't set a parent, so we run in complete isolation.
			// "file:///home/ashraf/Desktop/simple-bean-1.0.jar")};
			URLClassLoader urlCl = new URLClassLoader(new URL[]{new URL(new File(jbossHome).toURI().toURL(), "client/jboss-client.jar")}, null);
			//  since we're running in isolation my own interface needs to be added.
			ClassLoader cl = new AluniteClassLoader(urlCl, previous);

			Thread.currentThread().setContextClassLoader(cl);
			
			// use mpayments-util library
			String jndiname = "/cross-server-test/cross-server-ejb-wildfly9/ConnectorWildfly9Impl!com.infobip.crossserver.IConnector";
			IConnector connector = (IConnector)JndiHandler.lookupCrossServerWildfly9HttpRemoting(jndiname, new String[] {"localhost"}, 8180);
			
			if (connector == null)
				logger.error("Error while fetching deployed EJB on Wildfly 9...");
			else {
				
				String answer = connector.answer(name);
				logger.info("Wildfly is answering: " + answer);
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			
		} catch (NamingException e) {
			logger.error(e.getMessage());
		}
		finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
		return name;
	}

	public String answer(String name) {
		
		logger.info("Hello, " + name + " is calling ... ");
		String answer = "What's up, " + name + "?";
		logger.info("JBoss6 is answering: " + answer);
		return answer;
	}

}
