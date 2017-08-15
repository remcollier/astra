package astra.core;

import java.io.Serializable;

public interface Task extends Runnable,Serializable {
	public boolean isFinished();
	public long duration();
	public Object source();
	public void doTask();

}
