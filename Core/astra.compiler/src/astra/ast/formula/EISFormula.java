package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class EISFormula extends AbstractElement implements IFormula {
	ITerm id;
	ITerm entity;
	IFormula formula;
	
	public EISFormula(ITerm id, ITerm entity, IFormula formula, Token start, Token end, String source) {
		super(start, end, source);
		this.id=id;
		this.entity = entity;
		this.formula = formula;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula formula() {
		return formula;
	}
	
	public ITerm id() {
		return id;
	}

	public ITerm entity() {
		return entity;
	}
	
	public String toString() {
		String out = "EIS";
		if (id != null) {
			out += "( " + id;
			if (entity != null) {
				out += ", " + entity;
			}
			out += " )";
		}
		return out +" -> " + formula;
	}

	public String toSignature() {
		return "";
	}
}
