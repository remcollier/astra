package astra.event;

import astra.formula.ScopedGoal;

public class ScopedGoalEvent extends GoalEvent {
	public ScopedGoal scopedGoal;
	
	public ScopedGoalEvent(char type, ScopedGoal goal) {
		this(type, goal, null);
	}

	public ScopedGoalEvent(char type, ScopedGoal scopedGoal, Object source) {
		super(type, scopedGoal.formula(), source);
		this.scopedGoal = scopedGoal;
	}

	public ScopedGoal scopedGoal() {
		return scopedGoal;
	}
}
