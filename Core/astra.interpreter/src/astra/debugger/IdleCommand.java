package astra.debugger;

public class IdleCommand implements DebuggerCommand {
	@Override
	public String execute(DebuggerWorker worker, String[] bits) {
		return OK;
	}
}
