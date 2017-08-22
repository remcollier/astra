package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.StringMapper;
import astra.type.Type;

public class Performative implements Term {
	private static StringMapper mapper = new StringMapper();

	private int stringId;
	
	public Performative(String value) {
		this.stringId = mapper.toId(value);
	}
	
	public int stringId() {
		return stringId;
	}
	
	public String value() {
		return mapper.fromId(stringId);
	}

	@Override
	public Type type() {
		return Type.PERFORMATIVE;
	}

	public String toString() {
		return "\"" + mapper.fromId(stringId) + "\"";
	}
	
	public boolean equals(Object obj) {
		if (Performative.class.isInstance(obj)) {
			return ((Performative) obj).stringId == stringId;
		}
		return false;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term term) {
		if (term instanceof Performative) {
			return stringId == ((Performative) term).stringId;
		}
		return (term instanceof Variable);
	}

	@Override
	public String signature() {
		return "PE:"+stringId;
	}

	public Performative clone() {
		return this;
	}
}
