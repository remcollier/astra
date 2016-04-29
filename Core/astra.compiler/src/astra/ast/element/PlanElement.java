package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class PlanElement extends AbstractElement {
	PredicateFormula signature;
	IStatement statement;
	
	public PlanElement(PredicateFormula signature, IStatement statement, Token start, Token end, String source) {
		super(start, end, source);
		this.signature = signature;
		this.statement = statement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula signature() {
		return signature;
	}
	
	public IStatement statement() {
		return statement;
	}
}
