package astra.debugger;

public interface DebuggerCommand {
	public static final String FAIL = "FAIL";
	public static final String OK = "OK";
	public static final String UNKNOWN = "?";
	
	public String execute(DebuggerWorker worker, String[] arguments);
}
