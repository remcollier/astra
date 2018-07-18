package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class AtIndex implements Term {
	Term term;
	Term index;
	Type type;
	
	public AtIndex(Term term, Term index, Type type) {
		this.term = term;
		this.index = index;
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
		if (!AtIndex.class.isInstance(right)) return false;
		return term.matches(((AtIndex) right).term);
	}

	public boolean equals(Object object) {
		return (object instanceof AtIndex);
	}

	@Override
	public String signature() {
		return "HD:"+term.signature();
	}
	
	public AtIndex clone() {
		return this;
	}

	public Term term() {
		return term;
	}

	public Term index() {
		return index;
	}
	
	public String toString() {
		return "at_index("+term+","+index+")";
	}

}
