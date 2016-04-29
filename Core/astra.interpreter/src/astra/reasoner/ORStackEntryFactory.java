package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.OR;
import astra.term.Term;

public class ORStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new ORStackEntry(reasoner, (OR) formula, bindings);
	}
}
