package astra.event;

import astra.formula.Predicate;

public class BeliefEvent implements Event {
	public char type;
	public Predicate belief;
	
	public BeliefEvent(char type, Predicate belief) {
		this.type = type;
		this.belief = belief;
	}

	public char type() {
		return type;
	}

	public Predicate belief() {
		return belief;
	}

	@Override
	public Object getSource() {
		return null;
	}
	
	public String toString() {
		return type + belief.toString();
	}

	@Override
	public String signature() {
		return "BE:" + type + ":" + belief.id() + ":" + belief.terms().length;
	}
}
