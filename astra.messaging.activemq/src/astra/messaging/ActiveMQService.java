package astra.messaging;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ActiveMQService extends MessageService implements Runnable {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private Connection connection;
	private Session session;
	private XStream xstream;
	private boolean active = true;
	Map<String, Destination> destinations = new HashMap<String, Destination>();
	Map<String, MessageProducer> producers = new HashMap<String, MessageProducer>();

	
	private ActiveMQService() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();

        // Creating session for sending messages
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		xstream = new XStream(new DomDriver());
		xstream.alias("Message", Message.class);
	}

	public void run() {
		Map<String, MessageConsumer> consumers = new HashMap<String, MessageConsumer>();
		while (active) {
			for (String name : astra.core.Agent.agentNames()) {
				try {
					MessageConsumer consumer = consumers.get(name);
					if (consumer == null) {
						Destination destination = destinations.get(name);
						if (destination == null) {
							destination = session.createQueue(name);
							destinations.put(name, destination);
						}
						consumer = session.createConsumer(destination);
						consumers.put(name, consumer);
					}
					Message message = consumer.receiveNoWait();
					
			        if (message instanceof TextMessage) {
			            TextMessage textMessage = (TextMessage) message;
			            astra.core.Agent.getAgent(name).receive((AstraMessage) xstream.fromXML(textMessage.getText()));
			        }
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
		
        try {
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean sendMessage(AstraMessage message) {
		try {
			for (String name : message.receivers) {
				MessageProducer producer = producers.get(name);
				if (producer == null) {
					Destination destination = destinations.get(name);
					if (destination == null) {
						destination = session.createQueue(name);
						destinations.put(name, destination);
					}
					producer = session.createProducer(destination);
					producers.put(name, producer);
				}
		        TextMessage msg = session.createTextMessage(xstream.toXML(message));
		        producer.send(msg);
			}
			
			return true;
		} catch (JMSException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void configure(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		try {
			new Thread(new ActiveMQService(), "Active MQ Message Service").start();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
