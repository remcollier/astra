package astra.debugger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
	ServerSocket socket;
	BufferedInputStream in;
	PrintStream out;
	
	public Server() throws IOException {
		socket = new ServerSocket(8000);
	}
	
	byte[] buffer = new byte[8096];
	
	public void run() {
		while (true) {
			try {
				Socket s = socket.accept();
				System.out.println("accepted connection...");
				
				in = new BufferedInputStream(s.getInputStream());
				out = new PrintStream(s.getOutputStream());
				
				boolean active = true;
				while (active) {
					active=read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized boolean read() throws IOException {
		if (in.available() > 0) {
			int len = in.read(buffer, 0, buffer.length);
			if (len == -1) return false;
			
			String value = new String(buffer, 0, len).trim();
			if (value.length() > 0) {
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("SERVER Received: '" + value +"'");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
				out.println(value);
			}
		}
		return true;
	}

	public synchronized void write(String output) {
		out.println(output);
	}
}
