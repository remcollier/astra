package astra.core;

import astra.execution.SchedulerStrategy;

public class Scheduler {
	public static final int ACTIVE = 0;
	public static final int INACTIVE = 1;
	public static final int STEPPING = 2;

	private static SchedulerStrategy strategy;
	
	public static void setStrategy(SchedulerStrategy s) {
		if (strategy != null) {
			strategy.stop();
		}
		strategy = s;
	}
	
	public static boolean hasStrategy() {
		return strategy != null;
	}
	
	public static void schedule(Agent agent) {
		strategy.schedule(agent);
	}
	
	public static void schedule(Task task) {
		strategy.schedule(task);
	}
	
	public static void setThreadPoolSize(int size) {
		strategy.setThreadPoolSize(size);
	}

	public static void setState(Agent agent, int state) {
		strategy.setState(agent, state);
	}

	public static void setSleepTime(long sleepTime) {
		strategy.setSleepTime(sleepTime);
	}
	public static int getState(Agent agent) {
		return strategy.getState(agent);
	}
	
}
