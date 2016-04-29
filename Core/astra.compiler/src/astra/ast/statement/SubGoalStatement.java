package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.GoalFormula;

public class SubGoalStatement extends AbstractElement implements IStatement {
	GoalFormula goal;
	
	public SubGoalStatement(GoalFormula goal, Token start, Token end, String source) {
		super(start, end, source);
		
		this.goal = goal;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public GoalFormula goal() {
		return goal;
	}
	
	public String toString() {
		return goal.toString();
	}
}
