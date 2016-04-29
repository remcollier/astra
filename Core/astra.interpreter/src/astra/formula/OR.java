package astra.formula;

import astra.util.LogicVisitor;

public class OR implements Formula {
	Formula left;
	Formula right;
	
	public OR (Formula left, Formula right) {
		this.left = left;
		this.right = right;
	}
	
	public Formula left() {
		return left;
	}

	public Formula right() {
		return right;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public Formula[] formulae() {
		return new Formula[] { left, right };
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof OR) && ((OR) formula).left.matches(left) && ((OR) formula).right.matches(right);
	}
	
	public String toString() {
		return left + " | " + right;
	}
}
