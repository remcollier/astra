package astra.statement;

import astra.core.Intention;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.util.ContextEvaluateVisitor;

public class SpawnGoal extends AbstractStatement {
	Goal goal;
	
	public SpawnGoal(Goal goal) {
		this.goal = goal;
	}
	
	public SpawnGoal(String clazz, int[] data, Goal goal) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.goal = goal;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			Goal gl;
			
			@Override
			public boolean execute(Intention context) {
				context.addGoal(gl = (Goal) goal.accept(new ContextEvaluateVisitor(context)));
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				context.addEvent(new GoalEvent(GoalEvent.REMOVAL, gl));
				return false;
			}
			
			@Override
			public Statement statement() {
				return SpawnGoal.this;
			}
			
			public String toString() {
				if (gl == null) return goal.toString();
				return gl.toString();
			}
		};
	}
	

}
