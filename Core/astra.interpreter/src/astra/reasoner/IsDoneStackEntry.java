package astra.reasoner;

import java.util.Map;

import astra.formula.IsDone;
import astra.term.Term;

public class IsDoneStackEntry implements ReasonerStackEntry {
	Map<Integer, Term> bindings;
	IsDone formula;
	ResolutionBasedReasoner reasoner;
	
	public IsDoneStackEntry(ResolutionBasedReasoner reasoner, IsDone formula, Map<Integer, Term> bindings) {
		this.formula = formula;
		this.bindings = bindings;
		this.reasoner = reasoner;
	}

	@Override
	public boolean solve() {
		if (reasoner.agent.intention().isGoalCompleted()) {
			reasoner.stack.pop();
			return true;
		}
		
		reasoner.stack.pop();
		return false;
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