package astra.formula;

import astra.util.LogicVisitor;

public class BracketFormula implements Formula {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8544417764206263107L;
	private Formula formula;
	
	public BracketFormula(Formula formula) {
		this.formula = formula;
	}

	public Formula formula() {
		return formula;
	}
	
	public String toString() {
		return "(" + formula.toString() + ")";
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof BracketFormula) && ((BracketFormula) formula).formula.matches(formula);
	}

}
