package astra.term;

import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.StringMapper;
import astra.type.Type;

public class Funct implements Term {
	private static StringMapper mapper;
	
	private int id;
	Term[] terms;
	
	public Funct(String predicate, Term[] terms) {
		if (mapper == null) {
			mapper = new StringMapper();
		}

		this.id = mapper.toId(predicate);
		this.terms = terms;
	}

	private Funct(int id, Term[] terms) {
		this.id=id;
		this.terms=terms;
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

	public String functor() {
		return mapper.fromId(id);
	}

	@Override
	public boolean matches(Term term) {
		if (term instanceof Funct) {
			Funct f = (Funct) term;
			if (f.id != id || f.size() != terms.length) return false;
			
			for (int i=0; i < terms.length; i++) {
				if (!terms[i].matches(f.getTerm(i))) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Funct) {
			Funct p = (Funct) object;
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

	@Override
	public Type type() {
		return Type.FUNCTION;
	}

	@Override
	public String signature() {
		return this.id+":"+terms.length;
	}

	public void set(int i, Term term) {
		terms[i] = term;
	}

	public Funct clone() {
		Term[] values = new Term[terms.length];
		for (int i=0;i<terms.length; i++) {
			values[i] = terms[i].clone();
		}
		return new Funct(id, terms);
	}
}
