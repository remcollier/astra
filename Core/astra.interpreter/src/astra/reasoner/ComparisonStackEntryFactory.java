package astra.reasoner;

import java.util.Map;

import astra.formula.Comparison;
import astra.formula.Formula;
import astra.term.Term;

public class ComparisonStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new ComparisonStackEntry(reasoner, (Comparison) formula, bindings);
	}
}
