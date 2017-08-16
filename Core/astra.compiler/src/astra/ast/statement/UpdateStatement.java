package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class UpdateStatement extends AbstractElement implements IStatement {
	String op;
	PredicateFormula formula;
	
	public UpdateStatement(String op, PredicateFormula formula, Token start, Token end, String source) {
		super(start, end, source);
		
		this.op = op;
		this.formula = formula;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String op() {
		return op;
	}
	
	public PredicateFormula formula() {
		return formula;
	}
	
	public String toString() {
		return op + formula;
	}
}
