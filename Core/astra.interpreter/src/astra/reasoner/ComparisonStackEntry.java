package astra.reasoner;

import java.util.Map;

import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.term.Term;

public class ComparisonStackEntry implements ReasonerStackEntry {
	Comparison comparison;
	Map<Integer, Term> bindings;
	ResolutionBasedReasoner reasoner;
	
	public ComparisonStackEntry(ResolutionBasedReasoner reasoner, Comparison evaluable, Map<Integer, Term> bindings) {
		this.comparison = evaluable;
		this.bindings = bindings;
		this.reasoner = reasoner;
	}
	
	@Override
	public boolean solve() {
//		System.out.println("\tBindings: " + bindings);
		Formula result = (Formula) comparison.accept(new BindingsEvaluateVisitor(bindings, reasoner.agent));
//		System.out.println("\tcomparison result: " + result);
		if (result == Predicate.TRUE) {
//			System.out.println("propgating: " + bindings);
			reasoner.propagateBindings(bindings);
		}
		
		reasoner.stack.pop();
		return result == Predicate.TRUE;
	}

	public String toString() {
		return comparison.toString();
	}

	@Override
	public boolean failure() {
		// This should not be called (it is a leaf node)
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
		// This should not be called (it is a leaf node)
	}
}