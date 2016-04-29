package astra.term;

import astra.type.Type;
import astra.util.LogicVisitor;

public class Primitive<T> implements Term {
	private Type type;
	private T value;
	
	public Primitive(Type type, T value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public Type type() {
		return type;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public T value() {
		return value;
	}
	
	public static <T> Primitive<T> newPrimitive(T value) {
		return new Primitive<T>(Type.getType(value), value);
	}

	public String toString() {
		if (type.equals(Type.STRING)) {
			return "\"" + value.toString() + "\"";
		}
		return value.toString();
	}

	@Override
	public boolean matches(Term term) {
		return type.equals(term.type());
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		if (object instanceof Primitive) {
			return type.equals(((Primitive<?>) object).type()) && value.equals(((Primitive<T>) object).value);
		}
		
		return false;
	}

	@Override
	public String signature() {
		return "PR:"+value;
	}
}
