package astra.debugger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Client implements Runnable {
	BufferedInputStream in;
	PrintStream out;
	byte[] buffer = new byte[8096];

	public void run() {
		Socket socket = null;
		try {
			socket = new Socket("localhost", 8000);
			in = new BufferedInputStream(socket.getInputStream());
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			while (true) {
				boolean active = true;
				while (active) {
					active=read();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized boolean read() throws IOException {
		if (in.available() > 0) {
			int len = in.read(buffer, 0, buffer.length);
			if (len == -1) return false;
			String value = new String(buffer,0,len).trim();
			if (value.length() > 0) {
				System.out.println("CLIENT Received: "+value);
				out.println(value);
			}
		}
		return true;
	}

	public synchronized void write(String output) {
		out.println(output);
	}
}
