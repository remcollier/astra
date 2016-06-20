package astra.formula;

import astra.reasoner.util.LogicVisitor;

public class ScopedGoal implements Formula {
	private String scope;
	private Goal goal;
	
	public ScopedGoal(String scope, Goal goal) {
		this.scope = scope;
		this.goal = goal;
	}

	public Goal formula() {
		return goal;
	}
	
	public String toString() {
		return "!" + goal.toString();
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof ScopedGoal) && ((ScopedGoal) formula).goal.matches(goal);
	}

	public String scope() {
		return scope;
	}

}
