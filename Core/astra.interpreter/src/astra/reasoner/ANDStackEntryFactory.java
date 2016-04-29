package astra.reasoner;

import java.util.Map;

import astra.formula.AND;
import astra.formula.Formula;
import astra.term.Term;

public class ANDStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new ANDStackEntry(reasoner, (AND) formula, bindings);
	}
}
