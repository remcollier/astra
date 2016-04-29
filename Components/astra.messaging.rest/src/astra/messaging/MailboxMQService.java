package astra.messaging;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import astra.core.AbstractTask;
import astra.core.Scheduler;
import astra.core.Task;

public class MailboxMQService extends MessageService {
	public static void launch() {
		MessageService.installService("mailbox", new MailboxMQService());
	}
	
	private String target_url;
	private long frequency = 200;
	private String session = "test";
	
	private Gson gson;
	private MailboxClient client;
	
	public MailboxMQService() {
		this("http://astralangage.com/messaging");
	}
	
	public MailboxMQService(String url) {
		target_url = url;
		
		gson = new Gson();
		client = new MailboxClient();

		System.out.println("[MailboxMQService] Service Created...");
	}

	public boolean sendMessage(AstraMessage message) {
		try {
			client.post(
					target_url+"/mailbox/send/"+session, 
					gson.toJson(message)
			);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void configure(String key, String value) {
		if (key.equals("target_url")) {
			target_url = value;
		} else if (key.equals("frequency")) {
			frequency = Long.parseLong(value);
		} else if (key.equals("session")) {
			session = value;
		} else {
			System.err.println("[MailboxMQService] Unknown propery: " + key);
		}
	}

	@Override
	public void start() {
		Scheduler.schedule(new AbstractTask() {
			public void doTask() {
				try {
					List<MailboxMessage> list = gson.fromJson(
							client.get(target_url+"/mailbox/receive/" + session),
							new TypeToken<List<MailboxMessage>>(){}.getType());

					List<Integer> delivered = new LinkedList<Integer>();
					for (MailboxMessage message : list) {
						AstraMessage m = gson.fromJson(message.message, AstraMessage.class);
						if (receiveMessage(m)) delivered.add(message.id);
					}

					System.out.println("delivered: " + delivered);
					System.out.println(client.post(target_url+"/mailbox/delivered", gson.toJson(delivered)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(frequency);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Scheduler.schedule(this);
			}

			@Override
			public Object source() {
				return null;
			}
		});
		System.out.println("[MailboxMQService] Service Started...");
	}

	public boolean startSession() {
		try {
			client.post(target_url+"/session/start", "{ \"id\":\""+session+"\" }");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean clearMessages(String agent) {
		try {
			client.get(target_url+"/mailbox/clear/"+session+"/"+agent);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
