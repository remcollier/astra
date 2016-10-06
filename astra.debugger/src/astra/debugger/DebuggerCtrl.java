package astra.debugger;

import astra.core.Agent;
import astra.core.Module;
import astra.core.Scheduler;

public class DebuggerCtrl extends Module {
	@ACTION
	public boolean step(String name) {
		Agent agt = Agent.getAgent(name);
		if (Scheduler.getState(agt) == Scheduler.INACTIVE) {
			agt.execute();
			return true;
		}
		return false;
	}

	@ACTION
	public boolean suspend(String name) {
		Agent agt = Agent.getAgent(name);
		Scheduler.setState(agt, Scheduler.INACTIVE);
		return true;
	}

	@ACTION
	public boolean resume(String name) {
		Agent agt = Agent.getAgent(name);
		Scheduler.setState(agt, Scheduler.ACTIVE);
		Scheduler.schedule(agt);
		return true;
	}
}
