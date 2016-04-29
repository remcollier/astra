package astra.ast.statement;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class SynchronizedBlockStatement extends AbstractElement implements IStatement {
	List<IStatement> statements;
	String token;
	
	public SynchronizedBlockStatement(String token, List<IStatement> statements, Token start, Token end, String source) {
		super(start, end, source);
		this.token = token;
		this.statements = statements;
	}

	public IStatement[] statements() {
		return statements.toArray(new IStatement[] {});
	}
	
	public String token() {
		return token;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String toString() {
		String out = "synchronized (" + token + ") { ";
		for (IStatement statement : statements) {
			out += statement + "; ";
		}
		return out + "}";
	}
}
