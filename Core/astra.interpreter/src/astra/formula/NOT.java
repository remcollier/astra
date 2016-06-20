package astra.formula;

import astra.reasoner.util.LogicVisitor;



public class NOT implements Formula {
	private Formula formula;
	
	public NOT(Formula formula) {
		this.formula = formula;
	}

	public Formula formula() {
		return formula;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof NOT) && ((NOT) formula).formula.matches(formula);
	}
	
	public String toString() {
		return "~" + formula;
	}
}
