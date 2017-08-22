package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class NullTerm implements Term {

	@Override
	public Type type() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matches(Term right) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean equals(Object object) {
		return (object instanceof NullTerm);
	}

	@Override
	public String signature() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public NullTerm clone() {
		return this;
	}

}
