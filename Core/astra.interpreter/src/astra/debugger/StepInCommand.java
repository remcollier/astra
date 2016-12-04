package astra.debugger;

public class StepInCommand implements DebuggerCommand {
	@Override
	public String execute(DebuggerWorker worker, String[] bits) {
		Breakpoints.getInstance().stepIn(bits[1]);
		return OK;
	}

}
