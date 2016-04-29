package astra.execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import astra.core.Agent;
import astra.core.Task;

public class BasicSchedulerStrategy implements SchedulerStrategy {
	ExecutorService executor = Executors.newFixedThreadPool(20);
//	static ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void schedule(final Agent agent) {
		agent.state(Agent.ACTIVE);
//		System.out.println("[Scheduler] resuming: " + agent.name());
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					agent.execute();
				} catch (Throwable th) {
					th.printStackTrace();
				}
				synchronized (agent) {
					if (agent.state() == Agent.ACTIVE) {
						if (agent.isActive()) {
							executor.submit(this);
						} else {
							agent.state(Agent.INACTIVE);
						}
//					} else if (agent.state() == Agent.TERMINATING) {
//						agent.state(Agent.TERMINATED);
					}
				}
			}
		});
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
}
