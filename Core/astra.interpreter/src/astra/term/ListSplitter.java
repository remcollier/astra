package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.type.Type;

public class ListSplitter implements Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531850681574434943L;
	Term head;
	Variable tail;
	
	public ListSplitter(Term head, Variable tail) {
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
	
	public Term head() {
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
	
	public ListSplitter clone() {
		return new ListSplitter((Variable) head.clone(), (Variable) tail.clone());
	}

}
