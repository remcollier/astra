package astra.reasoner;

import java.util.Map;

import astra.formula.Formula;
import astra.formula.ModuleFormula;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;

public class ModuleFormulaStackEntry implements ReasonerStackEntry {
	private ModuleFormula formula;
	private Map<Integer, Term> bindings;
	private ResolutionBasedReasoner reasoner;
	public ModuleFormulaStackEntry(ResolutionBasedReasoner reasoner, ModuleFormula formula, Map<Integer, Term> bindings) {
		this.formula = formula;
		this.bindings = bindings;
		this.reasoner = reasoner;
	}

	@Override
	public boolean solve() {
		Formula f = formula.adaptor().invoke(
				new BindingsEvaluateVisitor(bindings, reasoner.agent), 
				formula.predicate());
		reasoner.stack.pop();
		reasoner.stack.push(reasoner.newStackEntry(f, bindings));
		return true;
	}

	@Override
	public boolean failure() {
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
		// NOT Used...
	}

}	