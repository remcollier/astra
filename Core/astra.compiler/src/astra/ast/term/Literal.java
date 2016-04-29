package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class Literal extends AbstractElement implements ITerm {
	String value;
	IType type;
	
	public Literal(String value, IType type, Token start, Token end, String source) {
		super(start, end, source);
		this.value = value;
		this.type = type;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	@Override
	public IType type() {
		return type;
	}

	public String toString() {
		return value;
	}

	public String value() {
		return value;
	}
}
