package astra.debugger;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DebuggerWorker implements Runnable {
	static Map<String, DebuggerCommand> commands = new HashMap<String, DebuggerCommand>();
	static {
		commands.put("BP", new BreakpointCommand());
		commands.put("START", new StartCommand());
		commands.put("IDLE", new StartCommand());
		commands.put("STEPIN", new StepInCommand());
		commands.put("STEPOVER", new StepOverCommand());
	}
	
	private ConcurrentLinkedQueue<String> notifications = new ConcurrentLinkedQueue<String>();
	
	public static class ServerDataEvent {
		private DebuggerServer server;
		private SocketChannel socket;
		private byte[] data;

		public ServerDataEvent(DebuggerServer server, SocketChannel socket, byte[] data) {
			 this.server = server;
			 this.socket = socket;
			 this.data = data;
		}
	}

	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	private String clazz;
	
	public DebuggerWorker(String clazz) {
		this.clazz=clazz;
	}

	public void processData(DebuggerServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}

	public void run() {
		ServerDataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = queue.remove(0);
			}


		    // Convert the buffer into a line
		    String line = new String( dataEvent.data );
		    String[] bits = line.split(" ");

		    String response = DebuggerCommand.UNKNOWN;
		    DebuggerCommand command = commands.get(bits[0]);
		    if (command != null) {
		    	response = command.execute(this, bits);
		    }

		    while (!notifications.isEmpty()) {
				dataEvent.server.send(dataEvent.socket, (notifications.poll()+"\n").getBytes());
		    }
			dataEvent.server.send(dataEvent.socket, response.getBytes());
		}
	}

	public void notify(String event) {
		notifications.add(event);
	}

	public String getMainClass() {
		return clazz;
	}
}
