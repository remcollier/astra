package astra.ast.statement;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class BlockStatement extends AbstractElement implements IStatement {
	List<IStatement> statements;
	
	public BlockStatement(List<IStatement> statements, Token start, Token end, String source) {
		super(start, end, source);
		this.statements = statements;
	}

	public IStatement[] statements() {
		return statements.toArray(new IStatement[] {});
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}
	
	public String toString() {
		String out = "{ ";
		for (IStatement statement : statements) {
			out += statement + "; ";
		}
		return out + "}";
	}
}
