package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class QueryStatement extends AbstractElement implements IStatement {
	IFormula formula;
	
	public QueryStatement(IFormula formula, Token start, Token end, String source) {
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
	
	public String toString() {
		return "query( " + formula + " )";
	}
}
