package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ComparisonFormula extends AbstractElement implements IFormula {
	String op;
	ITerm left;
	ITerm right;
	
	public ComparisonFormula(String op, ITerm left, ITerm right, Token start, Token end, String source) {
		super(start, end, source);
		
		this.op = op;
		this.left = left;
		this.right = right;
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

	public String toString() {
		return left.toString() + " " + op + " " + right.toString();
	}

	public String operator() {
		return op;
	}

	public String toSignature() {
		return "";
	}
}
