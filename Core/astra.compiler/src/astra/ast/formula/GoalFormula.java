package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class GoalFormula extends AbstractElement implements IFormula {
	PredicateFormula predicate;
	
	public GoalFormula(PredicateFormula predicate, Token start, Token end, String source) {
		super(start, end, source);
		this.predicate = predicate;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula predicate() {
		return predicate;
	}
	
	public String toString() {
		return "!" + predicate.toString();
	}

	public String toSignature() {
		return "goal:" + predicate.toSignature();
	}
}
