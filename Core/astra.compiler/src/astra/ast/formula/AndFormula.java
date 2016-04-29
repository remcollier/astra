package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AndFormula extends AbstractElement implements IFormula {
	IFormula left;
	IFormula right;
	
	public AndFormula(IFormula left, IFormula right, Token start, Token end, String source) {
		super(start, end, source);
		
		this.left = left;
		this.right = right;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula left() {
		return left;
	}

	public IFormula right() {
		return right;
	}

	public String toString() {
		return left.toString() + " & " + right.toString();
	}

	public String toSignature() {
		return "";
	}
}
