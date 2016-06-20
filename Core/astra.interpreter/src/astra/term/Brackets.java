package astra.term;

import astra.formula.Formula;
import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class Brackets implements Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531850681574434943L;
	private Term term;
	
	public Brackets(Term term) {
		this.term = term;
	}

	public Boolean value() {
		return null;
	}

	@Override
	public Type type() {
		return term.type();
	}
	
	public Term term() {
		return term;
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
		return "b";
	}
	
	public String toString() {
		return "(" + term + ")";
	}

}
