package astra.term;

import astra.formula.Formula;
import astra.type.Type;
import astra.util.LogicVisitor;

public class ListSplitter implements Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531850681574434943L;
	Variable head;
	Variable tail;
	
	public ListSplitter(Variable head, Variable tail) {
		this.head = head;
		this.tail = tail;
	}

	public Boolean value() {
		return null;
	}

	@Override
	public Type type() {
		return Type.LIST;
	}
	
	public Variable head() {
		return head;
	}

	public Variable tail() {
		return tail;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term term) {
		return (term instanceof Variable);
	}

	@Override
	public String signature() {
		return "ls";
	}
	
	public String toString() {
		return "[" + head + "|" + tail+ "]";
	}

}
