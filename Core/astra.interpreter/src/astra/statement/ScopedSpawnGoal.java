package astra.statement;

import astra.core.Intention;
import astra.event.GoalEvent;
import astra.event.ScopedGoalEvent;
import astra.formula.Goal;
import astra.formula.ScopedGoal;
import astra.reasoner.util.ContextEvaluateVisitor;

public class ScopedSpawnGoal extends AbstractStatement {
	String scope;
	Goal goal;
	
	public ScopedSpawnGoal(String scope, Goal goal) {
		this.scope = scope;
		this.goal = goal;
	}
	
	public ScopedSpawnGoal(String clazz, int[] data, String scope, Goal goal) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.scope = scope;
		this.goal = goal;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			Goal gl;
			
			@Override
			public boolean execute(Intention intention) {
				intention.addScopedGoal(scope, gl = (Goal) goal.accept(new ContextEvaluateVisitor(intention)));
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				context.addEvent(new ScopedGoalEvent(GoalEvent.REMOVAL, new ScopedGoal(scope, gl)));
				return false;
			}
			
			@Override
			public Statement statement() {
				return ScopedSpawnGoal.this;
			}
			
			public String toString() {
				if (gl == null) return goal.toString();
				return gl.toString();
			}
		};
	}
	

}
