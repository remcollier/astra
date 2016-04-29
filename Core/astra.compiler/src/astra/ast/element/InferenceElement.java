package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class InferenceElement extends AbstractElement {
	PredicateFormula head;
	IFormula body;
	
	public InferenceElement(PredicateFormula head, IFormula body, Token start, Token end, String source) {
		super(start, end, source);
		this.head = head;
		this.body = body;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula head() {
		return head;
	}
	
	public IFormula body() {
		return body;
	}
	
	public String toString() {
		return head + " :- " + body;
	}
}
