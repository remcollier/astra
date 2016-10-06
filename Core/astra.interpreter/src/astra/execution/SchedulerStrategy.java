package astra.execution;

import astra.core.Agent;
import astra.core.Task;

public interface SchedulerStrategy {
	public void schedule(Agent agent);
	public void schedule(Task task);
	public void setThreadPoolSize(int size);
	public void stop();
	public void setState(Agent agent, int state);
	public int getState(Agent agent);
	public void setSleepTime(long sleepTime);
}
