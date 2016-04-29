package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ScopedStatement extends AbstractElement implements IStatement {
	String scope;
	IStatement statement;
	
	public ScopedStatement(String scope, IStatement statement, Token start, Token end, String source) {
		super(start, end, source);
		
		this.scope = scope;
		this.statement = statement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IStatement statement() {
		return statement;
	}
	
	public String scope() {
		return scope;
	}
	
	public void scope(String scope) {
		this.scope = scope;
	}
	
	public String toString() {
		return scope + "::" + statement;
	}
}
