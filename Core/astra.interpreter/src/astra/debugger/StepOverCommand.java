package astra.debugger;

public class StepOverCommand implements DebuggerCommand {
	@Override
	public String execute(DebuggerWorker worker, String[] bits) {
		Breakpoints.getInstance().stepOver(bits[1]);
		return OK;
	}

}
