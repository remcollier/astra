package astra.formula;

import astra.reasoner.util.LogicVisitor;

public class Inference implements Formula {
	Predicate head;
	Formula body;
	
	public Inference (Predicate head, Formula body) {
		this.head = head;
		this.body = body;
	}
	
	public Predicate head() {
		return head;
	}
	
	public Formula body() {
		return body;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		if (formula instanceof Inference) {
			return ((Inference) formula).head.matches(head) && ((Inference) formula).body.matches(body);
		}
		return false;
	}
	
	public String toString() {
		return head + " :- " + body;
	}
}
