package astra.tr;

import astra.formula.Predicate;

public class Function {
	public Predicate identifier;
	public TRRule[] rules;
	
	public Function(Predicate identifier) {
		this(identifier, new TRRule[0]);
	}

	public Function(Predicate identifier, TRRule rules[]) {
		this.identifier = identifier;
		this.rules = rules;
	}

	public TRRule[] rules() {
		return rules;
	}
	
	public String toString() {
		return identifier.toString();
	}
}
