package astra.formula;

import astra.reasoner.util.LogicVisitor;

public class IsDone implements Formula {
	public IsDone() {
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return false;
	}
}
