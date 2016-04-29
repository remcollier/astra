package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class QueryTerm extends AbstractElement implements ITerm {
	IFormula formula;
	IType type = new BasicType(Token.BOOLEAN);
	
	public QueryTerm(IFormula formula, Token start, Token end, String source) {
		super(start, end, source);
		this.formula = formula;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula formula() {
		return formula;
	}

	@Override
	public IType type() {
		return type;
	}
	
	@Override
	public String toString() {
		return "query( " + formula + " )";
	}
}
