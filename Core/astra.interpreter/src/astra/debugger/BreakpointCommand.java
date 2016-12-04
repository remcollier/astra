package astra.debugger;

public class BreakpointCommand implements DebuggerCommand {
	@Override
	public String execute(DebuggerWorker worker, String[] bits) {
		Breakpoints.getInstance().set(bits[1], Integer.parseInt(bits[2]));
		return OK;
	}
}
