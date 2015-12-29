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
	public void lookupJBoss6() throws Exception {


		try {
			IConnector connector = (IConnector) lookup("localhost", "8080");

			if (connector == null)
				log.error("Error while fetching deployed EJB ...");
		
			
			else {
				log.info("Calling JBoss6 EJB...");

				String answer = connector.hello("Centili");
				
				log.info(answer);
				
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			log.error("Could not connect to remote Infobip test connector");
		}
	}
		
	private static Object lookup(String host, String port) throws NamingException {
		try {
			Properties properties = new Properties();
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
			
			properties.put(Context.PROVIDER_URL, "localhost:1099");

			final Context context = new InitialContext(properties);

			return context.lookup(
					"ConnectorJBoss6" );
		} catch (Exception e) {
			log.error(e.getMessage());
			System.out.println(e.getMessage());
			return null;
		}
	}

}
