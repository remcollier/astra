package astra.reasoner;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import astra.formula.OR;
import astra.term.Term;

public class ORStackEntry implements ReasonerStackEntry {
	boolean success = false;
	
	OR or;
	int index = 0;
	Queue<Map<Integer, Term>> queue = new LinkedList<Map<Integer, Term>>();
	Queue<Map<Integer, Term>> next = new LinkedList<Map<Integer, Term>>();
	Map<Integer, Term> bindings, initial;
//	List<Map<Integer, ITerm>> solutions = new LinkedList<Map<Integer, ITerm>>();
	ResolutionBasedReasoner reasoner;
	
	public ORStackEntry(ResolutionBasedReasoner reasoner, OR or, Map<Integer, Term> bindings) {
		this.or = or;
		this.initial = bindings;
		this.reasoner = reasoner;
		queue.add(bindings);
		//System.out.println("Handling: " + or.formulae()[index]);
	}
	
	@Override
	public boolean solve() {
		if (index < or.formulae().length) {
			// Here
			if (!queue.isEmpty()) {
				// Still more 
				bindings = queue.remove();
				try {
					//System.out.print("\tpushing: " + bindings);
					reasoner.stack.push(reasoner.newStackEntry(or.formulae()[index], bindings));
				} catch (NullPointerException e) {
					System.err.println("Formula: " + or.formulae()[index]);
					e.printStackTrace();
				}
			} else {
				// If we get any result, mark this step as successful...
				if (!next.isEmpty()) success = true;
				
//				System.out.println("\tsolutions: " + next.size());
				for (Map<Integer, Term> b : next) {
					//System.out.println("\tpropogating: " + b);
					reasoner.propagateBindings(b);
				}
				queue = new LinkedList<Map<Integer, Term>>();;
				next = new LinkedList<Map<Integer, Term>>();
				index++;
				queue.add(initial);
//				if (index < or.size()) System.out.println("\n\nHandling: " + or.formulae()[index]);
			}
			
			return true;
		} else {
			//System.out.println("FINISHED");
			reasoner.stack.pop();
			return success;
		}
	}

	public String toString() {
		return or.toString();
	}

	@Override
	public boolean failure() {
		//System.out.println("\tFAILED");
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
		//System.out.println("\tSUCCESS: "  + bindings);
		next.add(bindings);
	}
}
