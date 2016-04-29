package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class CartagoFormula extends AbstractElement implements IFormula {
	IFormula formula;
	ITerm artifact;
	
	public CartagoFormula(ITerm artifact, IFormula formula, Token start, Token end, String source) {
		super(start, end, source);
		this.artifact = artifact;
		this.formula = formula;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula formula() {
		return formula;
	}
	
	public ITerm artifact() {
		return artifact;
	}

	public String toString() {
		String out = "CARTAGO";
		if (artifact != null) {
			out += "( " + artifact + ")";
		}
		return out +" -> " + formula;
	}

	public String toSignature() {
		return "";
	}
}
