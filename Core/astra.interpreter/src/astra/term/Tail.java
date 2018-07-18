package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class Tail implements Term {
	Term term;
	
	public Tail(Term term) {
		this.term = term;
	}
	
	@Override
	public Type type() {
		return Type.LIST;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term right) {
		if (!Tail.class.isInstance(right)) return false;
		return term.matches(((Tail) right).term);
	}

	public boolean equals(Object object) {
		return (object instanceof Tail);
	}

	@Override
	public String signature() {
		return "TL:"+term.signature();
	}
	
	public Tail clone() {
		return this;
	}

	public Term term() {
		return term;
	}

	public String toString() {
		return "tail("+term+")";
	}
}
