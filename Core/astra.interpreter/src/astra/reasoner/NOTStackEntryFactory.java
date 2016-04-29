package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.NOT;
import astra.term.Term;

public class NOTStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new NOTStackEntry(reasoner, (NOT) formula, bindings);
	}
}
