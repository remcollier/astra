package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class Count implements Term {
	Term term;
	
	public Count(Term term) {
		this.term = term;
	}
	
	@Override
	public Type type() {
		return Type.INTEGER;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term right) {
		if (!Count.class.isInstance(right)) return false;
		return term.matches(((Count) right).term);
	}

	public boolean equals(Object object) {
		return (object instanceof Count);
	}

	@Override
	public String signature() {
		return "CT:"+term.signature();
	}
	
	public Count clone() {
		return this;
	}

	public Term term() {
		return term;
	}

}
