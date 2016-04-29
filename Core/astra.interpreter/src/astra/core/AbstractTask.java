package astra.core;



public abstract class AbstractTask implements Task {
	long start, duration;
	private boolean finished = false;
	private String name;
	
	public AbstractTask(String name) {
		this.name = name;
	}

	public AbstractTask() {
		this.name = "undefined";
	}

	public String toString() {
		return name;
	}
	
	public abstract void doTask();
	
	@Override
	public void run() {
		start = System.nanoTime();
		doTask();
		duration = System.nanoTime() - start;
		finished = true;
//		System.out.println("Finished: " + name + " / " + duration + "ns");
	}

	@Override
	public long duration() {
		return duration;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}
}
