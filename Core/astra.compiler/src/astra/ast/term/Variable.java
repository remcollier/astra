package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class Variable extends AbstractElement implements ITerm {
	String identifier;
	IType type;
	
	public Variable(String identifier, Token start, Token end, String source) {
		super(start, end, source);
		this.identifier = identifier;
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
		return identifier;
	}

	public void setType(IType type) {
		this.type = type;
	}
}
