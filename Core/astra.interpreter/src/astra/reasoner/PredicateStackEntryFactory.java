package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Term;

public class PredicateStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new PredicateStackEntry(reasoner, (Predicate) formula, bindings);
	}

}
