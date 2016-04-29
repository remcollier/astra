package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class FormulaVariable extends AbstractElement implements IFormula,ITerm {
	String identifier;
	
	public FormulaVariable(String identifier, Token start, Token end, String source) {
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

	@Override
	public IType type() {
		return new BasicType(Token.FORMULA);
	}
	
	public String toString() {
		return "formula "+identifier;
	}

	public String toSignature() {
		return "variable";
	}
}
