package astra.reasoner;

import java.util.HashMap;
import java.util.Map;

import astra.formula.Bind;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;
import astra.util.Utilities;

public class BindStackEntry implements ReasonerStackEntry {
	Map<Integer, Term> bindings;
	Bind formula;
	ResolutionBasedReasoner reasoner;
	
	public BindStackEntry(ResolutionBasedReasoner reasoner, Bind formula, Map<Integer, Term> bindings) {
		this.formula = formula;
		this.bindings = bindings;
		this.reasoner = reasoner;
	}

	@Override
	public boolean solve() {
//		System.out.println("Solving: " + formula);
//		System.out.println("bindings: " + bindings);
		BindingsEvaluateVisitor visitor = new BindingsEvaluateVisitor(bindings, reasoner.agent);
		Map<Integer,Term> b = new HashMap<Integer,Term>();
		b.put(formula.variable().id(), (Term) formula.term().accept(visitor));
//		System.out.println("bind bindings: " + Utilities.mgu(Utilities.merge(bindings, b)));
		reasoner.propagateBindings(Utilities.mgu(Utilities.merge(bindings, b)));
		reasoner.stack.pop();
		return true;
	}

	@Override
	public boolean failure() {
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
//		System.out.println("[BindStackEntry]: Propogating bindings: " + bindings);
//		reasoner.solutions.add(bindings);
	}
	
	public String toString() {
		return formula.toString();
	}
}