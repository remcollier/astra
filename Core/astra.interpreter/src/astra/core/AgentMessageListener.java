package astra.core;

import astra.messaging.AstraMessage;

public interface AgentMessageListener {
	public void receive(AstraMessage message);
}
