package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.term.Variable;

public class BindFormula extends AbstractElement implements IFormula {
	Variable variable;
	ITerm term;
	
	public BindFormula(Variable variable, ITerm term, Token start, Token end, String source) {
		super(start, end, source);
		this.variable = variable;
		this.term = term;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public Variable variable() {
		return variable;
	}
	
	public ITerm term() {
		return term;
	}

	public String toSignature() {
		return "";
	}
}
