package astra.formula;

import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.StringMapper;
import astra.term.Term;

public class Predicate implements Formula {
	public static final Predicate TRUE = new Predicate("true");
	public static final Predicate FALSE = new Predicate("false");

	private static StringMapper mapper;
	
	private int id;
	Term[] terms;
	
	public Predicate(String predicate, Term[] terms) {
		if (mapper == null) {
			mapper = new StringMapper();
		}

		this.id = mapper.toId(predicate);
		this.terms = terms;
	}

	public Predicate(String predicate) {
		this(predicate, Term.EMPTY_ARRAY);
	}

	public Term getTerm(int i) {
		return terms[i];
	}
	
	public int size() {
		return terms.length;
	}
	
	public int id() {
		return id;
	}

	public Term[] terms() {
		return terms;
	}
	
	public String toString() {
		String out = mapper.fromId(id);
		if (this.equals(TRUE) || this.equals(FALSE)) return out;
		out += "(";
		if (terms.length > 0) {
			for (int i=0;i<terms.length; i++) {
				if (i > 0) out += ",";
				out += terms[i].toString();
			}
		}
		out += ")";
		return out;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public String predicate() {
		return mapper.fromId(id);
	}

	@Override
	public boolean matches(Formula formula) {
		if (formula instanceof Predicate) {
			Predicate p = (Predicate) formula;
			if (p.id != id || p.size() != terms.length) return false;
			
			for (int i=0; i < terms.length; i++) {
				if (!terms[i].matches(p.getTerm(i))) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Predicate) {
			Predicate p = (Predicate) object;
			if (p.id != id || p.size() != terms.length) return false;
			
			for (int i=0; i < terms.length; i++) {
				if (!terms[i].equals(p.getTerm(i))) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	public Term termAt(int i) {
		return terms[i];
	}
}
