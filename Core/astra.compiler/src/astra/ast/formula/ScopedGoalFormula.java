package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ScopedGoalFormula extends AbstractElement implements IFormula {
	String scope;
	GoalFormula goal;
	
	public ScopedGoalFormula(String scope, GoalFormula goal, Token start, Token end, String source) {
		super(start, end, source);
		this.scope = scope;
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
		return scope + "::" + goal.toString();
	}

	public String scope() {
		return scope;
	}

	public void scope(String scope) {
		this.scope = scope;
	}

	public String toSignature() {
		return goal.toSignature();
	}
}
