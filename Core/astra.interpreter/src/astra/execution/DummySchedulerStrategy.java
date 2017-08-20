package astra.execution;

import astra.core.Agent;
import astra.core.Scheduler;
import astra.core.Task;


/**
 * This strategy was developed for the unit testing framework which uses its own control
 * layer instead of the scheduler.
 * 
 * @author Rem
 *
 */
public class DummySchedulerStrategy implements SchedulerStrategy {
	@Override
	public void schedule(Agent agent) {}

	@Override
	public void schedule(Task task) {
		new Thread() {
			public void run() {
				task.doTask();
			}
		}.start();
	}

	@Override
	public void setThreadPoolSize(int size) {}

	@Override
	public void stop() {}

	@Override
	public void setState(Agent agent, int state) {}

	@Override
	public int getState(Agent agent) {
		return Scheduler.ACTIVE;
	}

	@Override
	public void setSleepTime(long sleepTime) {}

}
