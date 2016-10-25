package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class InlineVariableDeclaration extends AbstractElement implements ITerm {
	IType type;
	String identifier;
	private boolean returns;
	
	public InlineVariableDeclaration(IType type, String identifier, Token start, Token end, String source) {
		super(start, end, source);
		this.type = type;
		this.identifier = identifier;
		this.returns = false;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String identifier() {
		return identifier;
	}

	public IType type() {
		return type;
	}
	
	
	public String toString() {
		return type + " " + identifier;
	}

	public void returns(boolean returns) {
		this.returns = returns;
	}
	
	public boolean returns() {
		return returns;
	}
}
