package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class TRStatement extends AbstractElement implements IStatement {
	IFormula guard;
	String type;
	
	public TRStatement(String type, IFormula guard, Token start, Token end, String source) {
		super(start, end, source);
		this.guard = guard;
		this.type = type;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula function() {
		return guard;
	}
	
	public String type() {
		return type;
	}
}
