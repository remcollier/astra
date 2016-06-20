package astra.statement;

import astra.core.Intention;
import astra.event.GoalEvent;
import astra.event.ScopedGoalEvent;
import astra.formula.Goal;
import astra.formula.ScopedGoal;
import astra.reasoner.util.ContextEvaluateVisitor;

public class ScopedSubgoal extends AbstractStatement {
	String scope;
	Goal goal;
	
	public ScopedSubgoal(String scope, Goal goal) {
		this.goal = goal;
	}
	
	public ScopedSubgoal(String clazz, int[] data, String scope, Goal goal) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.scope = scope;
		this.goal = goal;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			Goal gl;
			
			int index = 0;
			@Override
			public boolean execute(Intention intention) {
				switch (index) {
				case 0:
					ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(intention);
					gl = (Goal) goal.accept(visitor);

					intention.addScopedSubGoal(scope, gl);
					intention.suspend();
					index = 1;
					return true;
				case 1:
					intention.addEvent(new ScopedGoalEvent(GoalEvent.REMOVAL, new ScopedGoal(scope, gl)));
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				context.addEvent(new GoalEvent(GoalEvent.REMOVAL, gl));
				return false;
			}
			
			@Override
			public Statement statement() {
				return ScopedSubgoal.this;
			}
			
			public String toString() {
				if (gl == null) return goal.toString();
				return gl.toString();
			}
		};
	}
	

}
