package astra.debugger;

import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		
		try {
			Server server = new Server();
			Client client = new Client();
			new Thread(server).start();
			new Thread(client).start();
			Thread.sleep(1000);
			client.write("PING");
			server.write("PONG");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
