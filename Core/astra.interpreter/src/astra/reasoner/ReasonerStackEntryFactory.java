package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.term.Term;

public interface ReasonerStackEntryFactory {
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings);
}
