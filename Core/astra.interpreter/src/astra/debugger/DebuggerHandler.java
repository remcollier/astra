package astra.debugger;

public class DebuggerHandler {
	private byte[] rsp = null;
	private DebuggerAPI api;
	
	public DebuggerHandler(DebuggerAPI api) {
		this.api = api;
	}

	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}
	
	public synchronized void waitForResponse() {
		while(this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		
		api.notifyListeners(new String(this.rsp));
	}
}