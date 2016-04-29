package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class IfStatement extends AbstractElement implements IStatement {
	IFormula guard;
	IStatement ifStatement, elseStatement;
	
	public IfStatement(IFormula guard, IStatement ifStatement, IStatement elseStatement, Token start, Token end, String source) {
		super(start, end, source);
		this.guard = guard;
		this.ifStatement = ifStatement;
		this.elseStatement = elseStatement;
	}

	public IfStatement(IFormula guard, IStatement ifStatement, Token start, Token end, String source) {
		super(start, end, source);
		this.guard = guard;
		this.ifStatement = ifStatement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula guard() {
		return guard;
	}
	
	public IStatement ifStatement() {
		return ifStatement;
	}

	public IStatement elseStatement() {
		return elseStatement;
	}
	
	public String toString() {
		return "if ( " + guard + " ) " + ifStatement + (elseStatement == null ? "":" else " + elseStatement);
	}
}
