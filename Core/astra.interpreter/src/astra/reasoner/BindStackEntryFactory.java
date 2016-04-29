package astra.reasoner;

import java.util.Map;

import astra.formula.Bind;
import astra.formula.Formula;
import astra.term.Term;

public class BindStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new BindStackEntry(reasoner, (Bind) formula, bindings);
	}
}
