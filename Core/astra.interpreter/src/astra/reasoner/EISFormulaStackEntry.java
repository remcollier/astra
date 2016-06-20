package astra.reasoner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.eis.EISFormula;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.Utilities;
import astra.term.Term;

public class EISFormulaStackEntry implements ReasonerStackEntry {
	EISFormula formula;
	Queue<Formula> options = new LinkedList<Formula>();
	boolean solved = false;
	List<Map<Integer, Term>> solutions = new LinkedList<Map<Integer, Term>>();
	Formula nextFormula;
	Map<Integer, Term> initial;
	ResolutionBasedReasoner reasoner;
	
	public EISFormulaStackEntry(ResolutionBasedReasoner reasoner, EISFormula formula, Map<Integer, Term> bindings) {
		this.formula = formula;
		this.initial = bindings;
		this.reasoner = reasoner;
		
//		System.out.println("formula: " + formula);
		
		synchronized (this) {
			// Generate matching formulae
			for (Queryable source : reasoner.sources) {
				List<Formula> list = source.getMatchingFormulae(formula);
//				System.out.println("source: " + source + " / list: " + list);
				if (list != null) options.addAll(list);
			}
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
//		System.out.println("eis next:" + nextFormula);
//		System.out.println("match formula: " + (Predicate) formula.predicate().accept(visitor));
		if (Predicate.class.isInstance(nextFormula)) {
			Map<Integer, Term> bindings = Unifier.unify((Predicate) formula.predicate().accept(visitor), (Predicate) nextFormula, new HashMap<Integer, Term>(initial));
			if (bindings != null) {
//				System.out.println("\tmatch: "+ formula.predicate().accept(visitor));
				solved = true;
				reasoner.propagateBindings(Utilities.merge(initial, bindings));
				if (reasoner.singleResult) options.clear();
//			} else {
//				System.out.println("\tfailed to bind: "+ formula.predicate().accept(visitor));
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