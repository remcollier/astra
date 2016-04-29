package astra.messaging;


public class LocalMQService extends MessageService {
	public static void launch() {
		MessageService.installService("local", new LocalMQService());
	}
	
	public LocalMQService() {
		System.out.println("[LocalMQService] Service Created...");
	}
	
	public boolean sendMessage(AstraMessage message) {
		return this.receiveMessage(message);
	}

	@Override
	public void configure(String key, String value) {
		System.err.println("[UDPMQService] Unknown propery: " + key);
	}

	@Override
	public void start() {
		System.out.println("[LocalMQService] Service Started...");
	}
}
