package astra.core;

import astra.term.FormulaTerm;
import astra.term.ListTerm;
import astra.term.NullTerm;
import astra.term.Primitive;
import astra.term.Term;

public class ActionParam<T> {
	T value;
	
	public void set(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}

	public Term toLogic() {
		if (value == null) return new NullTerm();
		if (value instanceof ListTerm || value instanceof FormulaTerm) {
			return (Term) value;
		}
		return Primitive.newPrimitive(value);
	}
}
