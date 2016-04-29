package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class Operator extends AbstractElement implements ITerm {
	String op;
	IType type;
	ITerm left;
	ITerm right;
	
	public Operator(String op, ITerm left, ITerm right, Token start, Token end, String source) {
		super(start, end, source);
		
		this.op = op;
		this.left = left;
		this.right = right;
		updateType();
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm left() {
		return left;
	}

	public ITerm right() {
		return right;
	}

	@Override
	public IType type() {
		return type;
	}
	
	public String toString() {
		return left.toString() + " " + op + " " + right.toString();
	}

	public IType updateType() {
		if (left.type() == null) {
			this.type  = right.type();
		} else if (right.type() == null) {
			this.type = left.type();
		} else {
			this.type = Token.resolveTypes(left.type(), right.type());
		}
		return this.type;
	}

	public Object op() {
		return op;
	}
}
