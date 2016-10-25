package astra.lang;

import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Funct;

public class Logic extends Module {
	@FORMULA
	public Formula toPredicate(Funct funct) {
		return new Predicate(funct.functor(), funct.terms());
	}

	@FORMULA
	public Formula toPredicate(boolean value) {
		return value ? Predicate.TRUE:Predicate.FALSE;
	}
	
	@TERM
	public Funct toFunctor(Formula formula) {
		if (formula instanceof Predicate) {
			Predicate predicate = (Predicate) formula;
			return new Funct(predicate.predicate(), predicate.terms());
		}
		throw new RuntimeException("Invalid Formula Type: " + formula.getClass().getCanonicalName());
	}
}
