package astra.core;

import astra.execution.SchedulerStrategy;

public class Scheduler {
	private static SchedulerStrategy strategy;
	
	public static void setStrategy(SchedulerStrategy s) {
		strategy = s;
	}
	
	public static boolean hasStrategy() {
		return strategy != null;
	}
	
	public static void schedule(final Agent agent) {
		strategy.schedule(agent);
	}
	
	public static void schedule(Task task) {
		strategy.schedule(task);
	}
	
	public static void setThreadPoolSize(int size) {
		strategy.setThreadPoolSize(size);
	}
}
