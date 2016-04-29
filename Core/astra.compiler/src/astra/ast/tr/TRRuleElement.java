package astra.ast.tr;

import astra.ast.core.AbstractElement;
import astra.ast.core.IAction;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class TRRuleElement extends AbstractElement {
	IFormula formula;
	IAction action;
	
	public TRRuleElement(IFormula formula, IAction action, Token start, Token end, String source) {
		super(start, end, source);
		this.formula = formula;
		this.action = action;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula formula() {
		return formula;
	}
	
	public IAction action() {
		return action;
	}
	
	public String toString() {
		return formula.toString() + " -> " + action.toString();
	}
}
