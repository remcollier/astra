package astra.event;


public class ScopedBeliefEvent implements Event {
	String scope;
	BeliefEvent event;
	
	public ScopedBeliefEvent(String scope, BeliefEvent event) {
		this.scope = scope;
		this.event = event;
	}

	public String scope() {
		return scope;
	}
	
	public BeliefEvent beliefEvent() {
		return event;
	}
	
	@Override
	public Object getSource() {
		return null;
	}
	
	public String toString() {
		return scope + "::" + event;
	}

	@Override
	public String signature() {
		return event.signature();
	}
}
