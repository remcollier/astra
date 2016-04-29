package astra.execution;

import astra.core.Agent;
import astra.core.Task;

public interface SchedulerStrategy {
	public void schedule(Agent agent);
	public void schedule(Task task);
	public void setThreadPoolSize(int size);
}
