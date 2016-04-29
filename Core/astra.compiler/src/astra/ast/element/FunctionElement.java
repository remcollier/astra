package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;
import astra.ast.tr.TRRuleElement;

public class FunctionElement extends AbstractElement {
	PredicateFormula signature;
	TRRuleElement[] rules;
	
	public FunctionElement(PredicateFormula signature, TRRuleElement[] rules, Token start, Token end, String source) {
		super(start, end, source);
		this.signature = signature;
		this.rules = rules;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula signature() {
		return signature;
	}
	
	public TRRuleElement[] rules() {
		return rules;
	}
}
