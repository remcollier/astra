package astra.reasoner;

import java.util.Map;

import astra.eis.EISFormula;
import astra.formula.Formula;
import astra.term.Term;

public class EISFormulaStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new EISFormulaStackEntry(reasoner, (EISFormula) formula, bindings);
	}
}
