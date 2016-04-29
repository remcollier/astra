package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class TryRecoverStatement extends AbstractElement implements IStatement {
	IStatement tryStatement;
	IStatement recoverStatement;
	
	public TryRecoverStatement(IStatement tryStatement, IStatement recoverStatement, Token start, Token end, String source) {
		super(start, end, source);
		this.tryStatement= tryStatement;
		this.recoverStatement = recoverStatement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IStatement tryStatement() {
		return tryStatement;
	}

	public IStatement recoverStatement() {
		return recoverStatement;
	}
	
	public String toString() {
		return "try " + tryStatement + " recover " + recoverStatement;
	}
}
