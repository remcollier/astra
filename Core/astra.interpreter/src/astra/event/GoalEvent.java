package astra.event;

import astra.formula.Goal;

public class GoalEvent implements Event {
	public char type;
	public Goal goal;
	public Object source;
	
	public GoalEvent(char type, Goal goal) {
		this(type, goal, null);
	}

	public GoalEvent(char type, Goal goal, Object source) {
		this.type = type;
		this.goal = goal;
		this.source = source;
	}

	public char type() {
		return type;
	}

	public Goal goal() {
		return goal;
	}
	
	public String toString() {
		return type + goal.toString();
	}

	@Override
	public Object getSource() {
		return source;
	}
	
	@Override
	public String signature() {
		return "GE:" + type + ":" + goal.formula().id() + ":" + goal.formula().terms().length;
	}
}
