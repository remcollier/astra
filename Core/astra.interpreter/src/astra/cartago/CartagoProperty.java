package astra.cartago;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.util.LogicVisitor;
import astra.term.Term;

public class CartagoProperty implements Formula {
	Term target;
	Predicate content;
	
	public CartagoProperty(Term target, Predicate content) {
		this.target = target;
		this.content = content;
	}

	public CartagoProperty(Predicate content) {
		this.content = content;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public Predicate content() {
		return content;
	}

	public Term target() {
		return target;
	}

	@Override
	public boolean matches(Formula formula) {
		if (formula instanceof CartagoProperty) {
			CartagoProperty p = (CartagoProperty) formula;
			return target.matches(p.target) && content.matches(p.content);
		}
		return false;
	}
}
