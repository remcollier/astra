package astra.formula;

import astra.term.Variable;
import astra.util.LogicVisitor;

public class Goal implements Formula {
	private Predicate predicate;
	
	public Goal(Predicate predicate) {
		this.predicate = predicate;
	}

	public Predicate formula() {
		return predicate;
	}
	
	public String toString() {
		return "!" + predicate.toString();
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof Goal) && ((Goal) formula).predicate.matches(predicate);
	}

}
