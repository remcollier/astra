package astra.reasoner;

import java.util.Map;

import astra.formula.NOT;
import astra.term.Term;

public class NOTStackEntry implements ReasonerStackEntry {
	NOT not;
	boolean handled = false;
	boolean sr;
	boolean failed = false;
	Map<Integer, Term> bindings;
	ResolutionBasedReasoner reasoner;
	
	public NOTStackEntry(ResolutionBasedReasoner reasoner, NOT not, Map<Integer, Term> bindings) {
		this.not = not;
		this.bindings = bindings;
		this.reasoner = reasoner;
	}
	
	@Override
	public boolean solve() {
		if (!handled) {
			reasoner.stack.push(reasoner.newStackEntry(not.formula(), bindings));
			handled = true;
			sr = reasoner.singleResult;
			reasoner.singleResult = true;
		} else {
			if (!failed) {
//				//System.out.println("failed...");
				reasoner.stack.pop();
				// This means that the enclosed formula is true, so its
				// negation must be false...
				return false;
			}
//			//System.out.println("propagating: "+  bindings);
			reasoner.propagateBindings(bindings);
			reasoner.stack.pop();
		}
		return true;
	}

	public String toString() {
		return not.toString();
	}

	@Override
	public boolean failure() {
		// The enclosed formula could not be solved, so its negation
		// is true :o)
		// rest single result to whatever it was before the not was called
		// (this is because negation requires only to find a solution for
		// the enclosed formula)
		reasoner.singleResult = sr;
		failed = true;
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
		// Think about what to do here...
	}
}