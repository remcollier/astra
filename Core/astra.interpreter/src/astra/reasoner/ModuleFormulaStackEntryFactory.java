package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.ModuleFormula;
import astra.term.Term;

public class ModuleFormulaStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new ModuleFormulaStackEntry(reasoner, (ModuleFormula) formula, bindings);
	}
}
