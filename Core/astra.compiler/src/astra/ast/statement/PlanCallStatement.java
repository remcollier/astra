package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class PlanCallStatement extends AbstractElement implements IStatement {
	PredicateFormula call;
	
	public PlanCallStatement(PredicateFormula call, Token start, Token end, String source) {
		super(start, end, source);
		this.call = call;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula call() {
		return call;
	}
	
	public String toString() {
		return call.toString();
	}
}
