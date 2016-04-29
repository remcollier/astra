package astra.formula;

import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;
import astra.util.LogicVisitor;

public class AcreFormula implements Formula {
	Term cid;
	Term index;
	Term type;
	Term performative;
	Formula content;
	
	public AcreFormula(Term cid, Term index, Term type, Term performative, Formula content) {
		this.cid = cid;
		this.index = index;
		this.type = type;
		this.performative = performative;
		this.content = content;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return false;
	}

	public Term cid() {
		return cid;
	}
	
	public Term index() {
		return index;
	}
	
	public Term type() {
		return type;
	}
	
	public Term performative() {
		return performative;
	}
	
	public Formula content() {
		return content;
	}
}
