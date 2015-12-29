package test;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.infobip.crossserver.IConnector;

public class SimpleTestCase {
	private static final Logger log = Logger.getLogger(SimpleTestCase.class);


	@Test
	public void lookupWildfly9() throws Exception {

		try {
			IConnector connector = (IConnector) lookupHttpRemoting("localhost", "8180");

			if (connector == null)
				log.error("Error while fetching deployed EJB ...");
			else {
				log.info("Calling wildfly9 EJB...");

				connector.hello("Infobip");
				
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			log.info("Could not connect to remote Infobip test connector");
		}
	}
	
	
	private static Object lookupHttpRemoting(String host, String port) throws NamingException {
		try {
			final Properties jndiProperties = new Properties();
			jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			jndiProperties.put(Context.PROVIDER_URL, "http-remoting://" + host + ":" + port);
			jndiProperties.put("jboss.naming.client.ejb.context", true);

			final Context context = new InitialContext(jndiProperties);

			return context.lookup(
					"cross-server-test/cross-server-ejb-wildfly9/ConnectorWildfly9Impl!com.infobip.crossserver.IConnector" );
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}


}
