package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class IsDoneFormula extends AbstractElement implements IFormula {
	public IsDoneFormula(Token start, Token end, String source) {
		super(start, end, source);
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	@Override
	public String toSignature() {
		return "is_done";
	}

	@Override
	public String toString() {
		return "is_done";
	}

}
