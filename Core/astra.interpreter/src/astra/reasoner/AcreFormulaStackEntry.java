package astra.reasoner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.Utilities;
import astra.term.Term;

public class AcreFormulaStackEntry implements ReasonerStackEntry {
	AcreFormula formula;
	Queue<Formula> options = new LinkedList<Formula>();
	boolean solved = false;
	List<Map<Integer, Term>> solutions = new LinkedList<Map<Integer, Term>>();
	Formula nextFormula;
	Map<Integer, Term> initial;
	ResolutionBasedReasoner reasoner;
	
	public AcreFormulaStackEntry(ResolutionBasedReasoner reasoner, AcreFormula formula, Map<Integer, Term> bindings) {
		this.formula = formula;
		this.initial = bindings;
		this.reasoner = reasoner;
		
//		System.out.println("acre formula: " + formula);
		// Generate matching formulae
		for (Queryable source : reasoner.sources) {
			List<Formula> list = source.getMatchingFormulae(formula);
//			System.out.println("source: " + source + " / list: " + list);
			if (list != null) options.addAll(list);
		}
	}
	
	public boolean solve() {
		BindingsEvaluateVisitor visitor = new BindingsEvaluateVisitor(initial, reasoner.agent);

		if (options.isEmpty()) {
			if (solved) {
				for (Map<Integer, Term> bindings : solutions) {
					reasoner.propagateBindings(Utilities.merge(initial, bindings));
				}
			}
			reasoner.stack.pop();
			return solved;
		}
		
		nextFormula = (Formula) options.remove().accept(visitor);
//		System.out.println("acre next:" + nextFormula);
		if (nextFormula instanceof AcreFormula) {
			Map<Integer, Term> bindings = Unifier.unify((AcreFormula) formula.accept(visitor), (AcreFormula) nextFormula, new HashMap<Integer, Term>(initial));
			if (bindings != null) {
//				System.out.println("bound: "+ formula.accept(visitor));
				solved = true;
				reasoner.propagateBindings(Utilities.merge(initial, bindings));
				if (reasoner.singleResult) options.clear();
//			} else {
//				System.out.println("failed to bind: "+ formula.accept(visitor));
			}
		}

		return true;
	}

	public String toString() {
		return formula.toString() + " / " + options;
	}

	@Override
	public boolean failure() {
		// Do nothing, and propagate failure (this is called when a
		// predicate cannot be solved)
		return true;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
		solutions.add(bindings);
	}
}