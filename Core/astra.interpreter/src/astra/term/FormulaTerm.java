package astra.term;

import astra.formula.Formula;
import astra.type.Type;
import astra.util.LogicVisitor;

public class FormulaTerm implements Term {
	Formula formula;
	
	public FormulaTerm(Formula formula) {
		this.formula = formula;
	}

	public Formula value() {
		return formula;
	}

	@Override
	public Type type() {
		return Type.FORMULA;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term term) {
		return (term instanceof Variable);
	}

	@Override
	public String signature() {
		return "ft";
	}
	
	public String toString() {
		return formula.toString();
	}

}
