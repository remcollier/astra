package astra.eis;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.LogicVisitor;

public class EISFormula implements Formula {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9137787685136946200L;
	private Term id;
	private Term entity;
	private Predicate predicate;
	
	public EISFormula(Predicate predicate) {
		this.predicate = predicate;
	}

	public EISFormula(Term entity, Predicate predicate) {
		this.entity = entity;
		this.predicate = predicate;
	}

	public EISFormula(Term id, Term entity, Predicate predicate) {
		this.id = id;
		this.entity = entity;
		this.predicate = predicate;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		if (formula instanceof EISFormula) {
			EISFormula f = (EISFormula) formula;
			return (f.id == null || f.id.matches(id)) && f.predicate.matches(predicate); 
		}
		return false;
	}

	public Term id() {
		return id;
	}
	
	public Predicate predicate() {
		return predicate;
	}
	
	public String toString() {
		return "EIS" + (entity == null ? "":"< " + ((id == null) ? "":id + ", ") + entity + " >") + "." + predicate;
	}

	public Term entity() {
		return entity;
	}
}
