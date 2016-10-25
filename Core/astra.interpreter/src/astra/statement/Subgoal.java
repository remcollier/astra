package astra.statement;

import astra.core.Intention;
import astra.event.GoalEvent;
import astra.formula.Goal;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;

public class Subgoal extends AbstractStatement {
	Goal goal;
	
	public Subgoal(Goal goal) {
		this.goal = goal;
	}
	
	public Subgoal(String clazz, int[] data, Goal goal) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
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
//					System.out.println("goal: " + goal);
					ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(intention);
					gl = (Goal) goal.accept(visitor);
//					System.out.println("gl: " + gl);
					intention.addSubGoal(gl);
					intention.suspend();
					index = 1;
					return true;
				case 1:
					intention.addEvent(new GoalEvent(GoalEvent.REMOVAL, gl));
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
				return Subgoal.this;
			}
			
			public String toString() {
				if (gl == null) return goal.toString();
				return gl.toString();
			}
		};
	}
	

}
