package astra.reasoner;

import java.util.Map;

import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.term.Term;

public class AcreFormulaStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new AcreFormulaStackEntry(reasoner, (AcreFormula) formula, bindings);
	}
}
