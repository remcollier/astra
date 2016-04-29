package astra.formula;

import astra.term.Term;
import astra.term.Variable;
import astra.util.LogicVisitor;

public class Bind implements Formula {
	private Variable variable;
	private Term term;
	
	public Bind(Variable variable, Term term) {
		this.variable = variable;
		this.term = term;
	}

	public Variable variable() {
		return variable;
	}
	
	public Term term() {
		return term;
	}
	
	public String toString() {
		return "bind(" + variable + ", " + term + ")";
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		System.out.println("Bind.matches!!!");
		return false;
	}

}
