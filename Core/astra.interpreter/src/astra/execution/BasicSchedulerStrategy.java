package astra.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import astra.core.Agent;
import astra.core.Scheduler;
import astra.core.Task;
import astra.execution.SchedulerStrategy;

public class BasicSchedulerStrategy implements SchedulerStrategy {
	private ExecutorService executor = Executors.newFixedThreadPool(2);
	private Map<String, Integer> agents = new HashMap<String, Integer>();
	private long sleepTime = 50;
	
	@Override
	public void schedule(final Agent agent) {
		Integer state = agents.get(agent.name());
		if (state == null) {
			agents.put(agent.name(), state = Scheduler.ACTIVE);
		}
		
		if (state == Scheduler.ACTIVE) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					try {
						agent.execute();
					} catch (Throwable th) {
						System.err.println("Major Error in Agent: " + agent.name());
						th.printStackTrace();
						System.exit(0);
					}
					
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Scheduler.schedule(agent);
				}
			});
		}
	}

	@Override
	public void schedule(Task task) {
		executor.submit(task);
	}

	public void setThreadPoolSize(int size) {
		ExecutorService oldExecutor = executor;
		executor = Executors.newFixedThreadPool(size);
		oldExecutor.shutdown();
	}

	@Override
	public void stop() {
	}

	@Override
	public void setState(Agent agent, int state) {
		agents.put(agent.name(), state);
		
	}

	@Override
	public int getState(Agent agent) {
		return agents.get(agent.name());
	}

	@Override
	public void setSleepTime(long sleepTime) {
		this.sleepTime=sleepTime;
	}

}
