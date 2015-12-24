/**
 * 
 */
package com.infobip.crossserver;

import javax.ejb.Remote;

/**
 * @author jlazic
 *
 */
@Remote
public interface IConnector {
	
	public String hello(String name);
	public String answer(String name);

}
