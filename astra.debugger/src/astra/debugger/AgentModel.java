package astra.debugger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import astra.trace.TraceEvent;
import astra.trace.TraceEventListener;
import astra.trace.TraceManager;

public class AgentModel implements ListModel<String>, TraceEventListener {
	private List<ListDataListener> listeners = new LinkedList<ListDataListener>();
	private List<String> agents = new ArrayList<String>();
	
	public AgentModel() {
		TraceManager.getInstance().addListener(this);
	}
	
	@Override
	public void addListDataListener(ListDataListener listener) {
		listeners.add(listener);
	}

	@Override
	public String getElementAt(int index) {
		return agents.get(index);
	}

	@Override
	public int getSize() {
		return agents.size();
	}

	@Override
	public void removeListDataListener(ListDataListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized void update(TraceEvent event) {
		if (event.type().equals(TraceEvent.NEW_AGENT) ||
			(event.type().equals(TraceEvent.END_OF_CYCLE) && !agents.contains(event.source().name()))) {
			agents.add(event.source().name());
			
			for (ListDataListener listener : listeners) {
				listener.contentsChanged(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED, 0, agents.size()));
			}
		}
	}
}
