package astra.formula;

import astra.util.LogicVisitor;

public class AND implements Formula {
	Formula left;
	Formula right;
	
	public AND (Formula left, Formula right) {
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
		return (formula instanceof AND) && ((AND) formula).left.matches(left) && ((AND) formula).right.matches(right);
	}
	
	public String toString() {
		return left + " & " + right;
	}

}
