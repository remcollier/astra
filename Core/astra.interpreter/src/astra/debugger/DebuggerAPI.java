package astra.debugger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DebuggerAPI {
	public static final int STATE_INIT = 0;
	public static final int STATE_ACTIVE = 1;
	private static DebuggerAPI api;
	
	private int state = STATE_INIT;
	private List<DebuggerListener> listeners = new LinkedList<DebuggerListener>();
	private DebuggerClient client;
	
	public static DebuggerAPI createInstance() {
		api = new DebuggerAPI("localhost", 8000);
		return api;
	}

	public static DebuggerAPI createInstance(int port) {
		api = new DebuggerAPI("localhost", port);
		return api;
	}

	public static DebuggerAPI createInstance(String host) {
		api = new DebuggerAPI(host, 8000);
		return api;
	}

	public static DebuggerAPI createInstance(String host, int port) {
		api = new DebuggerAPI(host, port);
		return api;
	}

	protected DebuggerAPI(String host, int port) {
	    try {
	        client = new DebuggerClient(host, port);
	        Thread t = new Thread(client);
	        t.setDaemon(true);
	        t.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean setBreakpoint(Breakpoint breakpoint) {
		if (state == STATE_INIT) {
	        DebuggerHandler handler = new DebuggerHandler(this);
	        try {
				client.send(("BP "+breakpoint.getTargetClass() + " " + breakpoint.line()).getBytes(), handler);
		        handler.waitForResponse();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean start() {
		if (state == STATE_INIT) {
			state = STATE_ACTIVE;
	        DebuggerHandler handler = new DebuggerHandler(this);
	        try {
				client.send("START".getBytes(), handler);
		        handler.waitForResponse();
		        
		        new Thread() {
		        	public void run() {
		    	        DebuggerHandler handler = new DebuggerHandler(DebuggerAPI.this);
		        		try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        		
		        		try {
							client.send("POLL".getBytes(), handler);
						} catch (IOException e) {
							e.printStackTrace();
						}
		        		handler.waitForResponse();
		        	}
		        }.start();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean stepIn(String name) {
		if (state == STATE_ACTIVE) {
			try {
		        DebuggerHandler handler = new DebuggerHandler(this);
				client.send(("STEPIN "+name).getBytes(), handler);
		        handler.waitForResponse();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean stepOver(String name) {
		if (state == STATE_ACTIVE) {
			try {
		        DebuggerHandler handler = new DebuggerHandler(this);
				client.send(("STEPOVER "+name).getBytes(), handler);
		        handler.waitForResponse();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void addListener(DebuggerListener listener) {
		listeners.add(listener);
		
	}

	public void notifyListeners(String event) {
		for (DebuggerListener listener : listeners) {
			listener.receive(event);
		}
		
	}
}
