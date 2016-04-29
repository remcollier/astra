package astra.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.core.Agent;


public abstract class MessageService {
	static Map<String, MessageService> services = new HashMap<String, MessageService>();
	static List<String> serviceIds = new LinkedList<String>();
	
	public static boolean hasService(String id) {
		return services.containsKey(id);
	}

	public static void installService(String id, MessageService service) {
		services.put(id, service);
		serviceIds.add(id);
	}
	
	public static MessageService getService(String id) {
		return services.get(id);
	}
	
	public static boolean send(AstraMessage message) {
		if (serviceIds.isEmpty()) {
			// Install LocalMQService as Default...
			System.out.println("[MessageService] No service installed - using LocalMQService");
			installService("local", new LocalMQService());
		}
		
		for (String id : serviceIds) {
			if (services.get(id).sendMessage(message)) {
				return true;
			}
		}
		return false;
	}
	
	public abstract boolean sendMessage(AstraMessage message);
	public abstract void configure(String key, String value);
	public abstract void start();

	public boolean receiveMessage(AstraMessage message) {
		for (String name : message.receivers) {
            Agent receiver = Agent.getAgent(name);
            if ( receiver != null ) {
               receiver.receive(message);
               return true;
            }
		}
		return false;
	}

}