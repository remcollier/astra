package astra.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import astra.messaging.Utilities.PredicateState;

public class UDPMQService extends MessageService {
	private int port;
	private String hostname;
	private InetAddress address;
	private MulticastSocket socket;
	private boolean terminating;
	
    public UDPMQService() {
		hostname = "230.0.0.1";
		port = 2000;
		terminating = false;
		System.out.println("UDPMQService created...");
	}
	
	public void configure(String key, String value) {
		if (key.equals("port")) {
			port = Integer.parseInt(value);
		} else if (key.equals("host")) {
			hostname = value;
		} else {
			System.err.println("[UDPMQService] Unknown propery: " + key);
		}
	}
	
	public void start() {
		try {
			address = InetAddress.getByName(hostname);
			if (address.isMulticastAddress()) {
				socket = new MulticastSocket();
				socket.joinGroup(address);
			}
			
			new Thread() {
				public void run() {
					try {
						MulticastSocket socket = new MulticastSocket(port);
						socket.joinGroup(address);
			            while (!terminating) {
			                try {
			                    byte[] buf = new byte[64000];
			                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			                    socket.receive(packet);
			                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
			                    AstraMessage message = (AstraMessage) in.readObject();
			        			receiveMessage(message);
			                    in.close();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                } catch (ClassNotFoundException e) {
			        			e.printStackTrace();
			        		}
			            }
			            socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		        }
			}.start();
			
			System.out.println("[UDPMQService] Service Started...");
		} catch (UnknownHostException uhe) {
			System.err.println("[UDPMessageTransportService] Unknown host: " + hostname);
		} catch (IOException ioe) {
			System.err.println("[UDPMessageTransportService] Error occurred setting up connection for: " + hostname);
		}
	}

   /**
     * This method is responsible for transmitting messages using the FIPA
     * HTTP Message Transport Service.
     *
     * @param message a Message object
     */
	@Override
    public boolean sendMessage(AstraMessage message) {
		try {
			ByteArrayOutputStream baout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baout);
			out.writeObject(message);
	        byte[] buf = baout.toByteArray();
            socket.send(new DatagramPacket(buf, buf.length, this.address, port));
            out.close();
            return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
    }
}
