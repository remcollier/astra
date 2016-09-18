package astra.lang;

import astra.core.Module;
import astra.messaging.MessageService;
/**
 * This class implements support for the management of ASTRA's lightweight messaging infrastructure.
 * 
 * <p>
 * The methods provided in this class deliver basic functionality to manage ASTRAs messaging infrastructure.
 * This infrastructure is lightweight in that, it does not require extensive configuration.
 * </p>
 * <p>
 * When deploying a message service, the standard usage of the methods in this class is as follows:
 * 
 * <ol>
 * <li>Install the message service</li>
 * <li>Configure the service by setting properties</li>
 * <li>Start the service</li>
 * </ol>
 * 
 * This deployment is typically done as one of the first tasks by  a "main" agent so that other agents
 * can communicate using the infrastructure.
 * 
 * The ASTRA equivalent of this is:
 * </p>
 * <code>
 * rule +!main(list args) {<br/>
 *     messaging.installService("local", "astra.messaging.LocalMQService");<br/>
 *     messaging.startService();<br/>
 * }<br/>
 * </code>
 *  	
 * @author Rem Collier
 *
 */
public class Messaging extends Module {
	/**
	 * Action that installs a Message Service.
	 * 
	 * <p>
	 * This method installs a message service in to the local JVM. Message services route
	 * messages to agents on the local platform or via a remote connection to another platform.
	 * </p>
	 * <p>
	 * All message services must implement the <b>astra.messaging.MessageService</b> interface.
	 * </p> 
	 *
	 * @see astra.messaging.MessageService
	 * @param id a unique identifier by which the service can be referenced
	 * @param clazz the canonical name of the class that implements the service
	 * @return
	 */
	@ACTION
	public boolean installService(String id, String clazz) {
		try {
			if (!MessageService.hasService(id)) {
				MessageService.installService(id, (MessageService) Class.forName(clazz).newInstance());
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Action that sets a property for a message service.
	 * 
	 * <p>
	 * This method allows you to configure a message service by setting a property of that
	 * service
	 * </p>
	 *  
	 * @param id the id of the service
	 * @param key the key of the property
	 * @param value the value of the property
	 * @return
	 */
	@ACTION
	public boolean setProperty(String id, String key, String value) {
		MessageService.getService(id).configure(key, value);
		return true;
	}

	/**
	 * Action that starts the message service.
	 * 
	 * @param id the id of the service.
	 * @return
	 */
	@ACTION
	public boolean startService(String id) {
		MessageService.getService(id).start();
		return true;
	}
	
}
