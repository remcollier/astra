package astra.formula;

import astra.term.Term;
import astra.util.LogicVisitor;



public class IsNull implements Formula {
	private Term term;
	
	public IsNull(Term term) {
		this.term = term;
	}

	public Term formula() {
		return term;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof IsNull) && ((IsNull) formula).term.equals(formula);
	}
}
