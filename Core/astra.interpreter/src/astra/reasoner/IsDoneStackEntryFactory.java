package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.IsDone;
import astra.term.Term;

public class IsDoneStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new IsDoneStackEntry(reasoner, (IsDone) formula, bindings);
	}
}
