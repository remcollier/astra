package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class WhenStatement extends AbstractElement implements IStatement {
	IFormula guard;
	IStatement statement;
	
	public WhenStatement(IFormula guard, IStatement statement, Token start, Token end, String source) {
		super(start, end, source);
		this.guard = guard;
		this.statement = statement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula guard() {
		return guard;
	}
	
	public IStatement statement() {
		return statement;
	}
}
