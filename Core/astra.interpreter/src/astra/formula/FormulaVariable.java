package astra.formula;

import astra.term.Variable;
import astra.util.LogicVisitor;

public class FormulaVariable implements Formula {
	private Variable variable;
	private Formula value;
	
	public FormulaVariable(Variable variable) {
		this.variable = variable;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public Variable variable() {
		return variable;
	}
	
	public Formula value() {
		return value;
	}
	
	public void value(Formula value) {
		this.value = value;
	}

	@Override
	public boolean matches(Formula formula) {
		throw new UnsupportedOperationException("Should not get this");
	}
}
