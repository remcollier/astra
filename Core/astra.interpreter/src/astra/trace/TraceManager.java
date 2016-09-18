package astra.trace;

import java.util.LinkedList;
import java.util.List;

public class TraceManager {
	private static TraceManager manager;
	
	public static TraceManager getInstance() {
		if (manager == null) {
			manager = new TraceManager();
		}
		return manager;
	}
	
	private List<TraceEventListener> listeners = new LinkedList<TraceEventListener>();
	private TraceRecorder recorder;

	public void setTraceRecorder(TraceRecorder _recorder) {
		this.recorder = _recorder;
	}
	
	public void addListener(TraceEventListener listener) {
		listeners.add(listener);
	}
	
	public  void removeListener(TraceEventListener listener) {
		listeners.remove(listener);
	}
	
	public void recordEvent(TraceEvent event) {
		if (recorder != null) {
			recorder.record(event);
		}

		for (TraceEventListener listener : listeners) {
			listener.update(event);
		}
	}
	
}
