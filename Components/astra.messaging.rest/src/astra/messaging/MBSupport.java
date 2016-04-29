package astra.messaging;

import astra.core.Module;

public class MBSupport extends Module {
	@ACTION
	public boolean launch() {
		MessageService.installService("mailbox", new MailboxMQService());
		return true;
	}

	@ACTION
	public boolean launch(String url) {
		MessageService.installService("mailbox", new MailboxMQService(url));
		return true;
	}

	@ACTION
	public boolean startSession(String session) {
		MailboxMQService service = (MailboxMQService) MessageService.getService("mailbox");
		service.configure("session", session);
		service.startSession();
		return true;
	}

	@ACTION
	public boolean clear() {
		MailboxMQService service = (MailboxMQService) MessageService.getService("mailbox");
		service.clearMessages(agent.name());
		return true;
	}

	@ACTION
	public boolean start() {
		MessageService.getService("mailbox").start();
		return true;
	}

	@ACTION
	public boolean setFrequency(long frequency) {
		MessageService.getService("mailbox").configure("frequency", ""+frequency);
		return true;
	}
}
