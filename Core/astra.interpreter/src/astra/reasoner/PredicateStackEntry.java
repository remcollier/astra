package astra.reasoner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.formula.Formula;
import astra.formula.Inference;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.RenameVisitor;
import astra.reasoner.util.Utilities;
import astra.term.Term;

public class PredicateStackEntry implements ReasonerStackEntry {
	private static long counter 											= 0;

	Predicate predicate;
	Queue<Formula> options = new LinkedList<Formula>();
	boolean solved = false;
	int solutionCount = 0;
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
		
//		System.out.println("singleResult: " + reasoner.singleResult);
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

//		System.out.println("SOLVING: " + predicate);
//		predicate = (Predicate) predicate.accept(visitor);
//		System.out.println("SOLVING (bound): " + predicate);
		
		if (options.isEmpty()) {
//			System.out.println("\tFinished Predicate Matching:" + solutionCount);
			if (solutionCount > 0) {
//				System.out.println("\tSolved");
				for (Map<Integer, Term> bindings : solutions) {
					reasoner.propagateBindings(Utilities.merge(initial, bindings));
				}
			}
			reasoner.stack.pop();
			return solutionCount > 0;
		}
		
		nextFormula = (Formula) options.remove();
		if (Predicate.class.isInstance(nextFormula)) {
//			System.out.println("predicate: " + predicate);
//			System.out.println("attempting to match: " + nextFormula);
			Map<Integer, Term> bindings = Unifier.unify((Predicate) predicate.accept(visitor), (Predicate) nextFormula.accept(visitor), new HashMap<Integer, Term>(initial),reasoner.agent);
			if (bindings != null) {
//				System.out.println("\tmatched");
				solved = true;
				solutionCount++;
				reasoner.propagateBindings(Utilities.mgu(Utilities.merge(initial, bindings)));
//				System.out.println("singleResult: " + reasoner.singleResult);
				if (reasoner.singleResult) {
//					System.out.println("Clearing options");
					options.clear();
				}
			}
		} else if (Inference.class.isInstance(nextFormula)) {
//			System.out.println("\tPredicate: " + predicate);
//			System.out.println("\tApplied Predicate: " + predicate.accept(visitor));
//			System.out.println("\tinference: " + nextFormula);
			Inference inference = (Inference) nextFormula;
			RenameVisitor rvisitor = new RenameVisitor("rn_" + (counter++) + "_");
			inference = (Inference) inference.accept(rvisitor);
//			System.out.println("\tinference (applied): " + inference);
			Map<Integer, Term> bindings = Unifier.unify((Predicate) predicate.accept(visitor), inference.head(), new HashMap<Integer, Term>(initial),reasoner.agent);
//			System.out.println("\tbindings: " + bindings);
			if (bindings != null) {
//				System.out.println("SOLVED - " + toString());
//				System.out.println("\tmerged: " + Utilities.mgu(Utilities.merge(rvisitor.bindings(), bindings)));
				solutionCount++;
				
				// Removed the bindings evaluation here because it causes the module terms 
				// to be evaluated...
				// this should be delayed until necessary...
				// but need to test this..
//				System.out.println("\tpushing: " + inference.body());//.accept(new BindingsEvaluateVisitor(bindings, reasoner.agent)));
				reasoner.stack.push(reasoner.newStackEntry(
						(Formula) inference.body(),//.accept(new BindingsEvaluateVisitor(bindings, reasoner.agent)), 
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
		solutionCount--;
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
