package astra.reasoner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.formula.Formula;
import astra.formula.Inference;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;
import astra.util.RenameVisitor;
import astra.util.Utilities;

public class PredicateStackEntry implements ReasonerStackEntry {
	private static long counter 											= 0;

	Predicate predicate;
	Queue<Formula> options = new LinkedList<Formula>();
	boolean solved = false;
	List<Map<Integer, Term>> solutions = new LinkedList<Map<Integer, Term>>();
	Formula nextFormula;
	Map<Integer, Term> initial;
	ResolutionBasedReasoner reasoner;
	int total = 0;
	
	public PredicateStackEntry(ResolutionBasedReasoner reasoner, Predicate predicate, Map<Integer, Term> bindings) {
		this.predicate = predicate;
		this.initial = bindings;
		this.reasoner = reasoner;
		
		// Generate matching formulae
		for (Queryable source : reasoner.sources) {
			List<Formula> list = source.getMatchingFormulae(predicate);
			if (list != null) options.addAll(list);
		}
		
		total = options.size();
	}
	
	public boolean solve() {
		BindingsEvaluateVisitor visitor = new BindingsEvaluateVisitor(initial, reasoner.agent);

		if (predicate.equals(Predicate.FALSE)) {
			reasoner.stack.pop();
			return false;
		}
		if (predicate.equals(Predicate.TRUE)) {
			reasoner.propagateBindings(initial);
			reasoner.stack.pop();
			return true;
		}
		
		if (options.isEmpty()) {
//			System.out.println("\tFinished Predicate Matching");
			if (solved) {
//				System.out.println("\tSolved");
				for (Map<Integer, Term> bindings : solutions) {
					reasoner.propagateBindings(Utilities.merge(initial, bindings));
				}
			}
			reasoner.stack.pop();
			return solved;
		}
		
		nextFormula = (Formula) options.remove();
		if (Predicate.class.isInstance(nextFormula)) {
			Map<Integer, Term> bindings = Unifier.unify((Predicate) predicate.accept(visitor), (Predicate) nextFormula.accept(visitor), new HashMap<Integer, Term>(initial));
			if (bindings != null) {
				solved = true;
				reasoner.propagateBindings(Utilities.mgu(Utilities.merge(initial, bindings)));
				if (reasoner.singleResult) options.clear();
			}
		} else if (Inference.class.isInstance(nextFormula)) {
//			System.out.println("\tPredicate: " + predicate);
//			System.out.println("\tApplied Predicate: " + predicate.accept(visitor));
//			System.out.println("\tinference: " + nextFormula);
			Inference inference = (Inference) nextFormula;
			RenameVisitor rvisitor = new RenameVisitor("rn_" + (counter++) + "_");
			inference = (Inference) inference.accept(rvisitor);
//			System.out.println("\tinference: " + inference);
			Map<Integer, Term> bindings = Unifier.unify((Predicate) predicate.accept(visitor), inference.head(), new HashMap<Integer, Term>(initial));
//			System.out.println("\tbindings: " + bindings);
			if (bindings != null) {
//				System.out.println("SOLVED - " + toString());
//				System.out.println("\tmerged: " + Utilities.mgu(Utilities.merge(rvisitor.bindings(), bindings)));
				solved = true;
//				propagateBindings(Utilities.merge(initial, Utilities.merge(rvisitor.bindings(), bindings)));
//				System.out.println("\tpushing: " + inference.body().accept(new BindingsEvaluateVisitor(bindings, reasoner.agent)));
				reasoner.stack.push(reasoner.newStackEntry((Formula) inference.body().accept(new BindingsEvaluateVisitor(bindings, reasoner.agent)), 
						Utilities.mgu(Utilities.merge(initial, Utilities.merge(rvisitor.bindings(), bindings)))));
				if (reasoner.singleResult) options.clear();
			}
		}

		return true;
	}

	public String toString() {
		return "[PredicateStackEntry] solving: " + predicate.toString() + " / " + options.size() + " of " + total;
	}

	@Override
	public boolean failure() {
//		System.out.println("[PredicateStackEntry]: Failure: " + predicate + " / " + options.size() + " / " + solutions.size());
		if (options.isEmpty() && solutions.isEmpty()) return true;
		// Do nothing, and propagate failure (this is called when a
		// predicate cannot be solved)
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
//		System.out.println("[PredicateStackEntry]: Propogating bindings: " + bindings);
		solutions.add(bindings);
	}
}
