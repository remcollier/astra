package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class Head implements Term {
	Term term;
	Type type;
	
	public Head(Term term, Type type) {
		this.term = term;
		this.type = type;
	}
	
	@Override
	public Type type() {
		return type;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term right) {
		if (!Head.class.isInstance(right)) return false;
		return term.matches(((Head) right).term);
	}

	public boolean equals(Object object) {
		return (object instanceof Head);
	}

	@Override
	public String signature() {
		return "HD:"+term.signature();
	}
	
	public Head clone() {
		return this;
	}

	public Term term() {
		return term;
	}
	
	public String toString() {
		return "head("+term+","+type+")";
	}

}
