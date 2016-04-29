package astra.reasoner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import astra.formula.AND;
import astra.term.Term;

public class ANDStackEntry implements ReasonerStackEntry {
	AND and;
	int index = 0;
	Queue<Map<Integer, Term>> queue = new LinkedList<Map<Integer, Term>>();
	Queue<Map<Integer, Term>> next = new LinkedList<Map<Integer, Term>>();
	Map<Integer, Term> bindings;
	List<Map<Integer, Term>> solutions = new LinkedList<Map<Integer, Term>>();
	ResolutionBasedReasoner reasoner;
	
	public ANDStackEntry(ResolutionBasedReasoner reasoner,AND and, Map<Integer, Term> bindings) {
		this.and = and;
		this.reasoner = reasoner;
		queue.add(bindings);
	}
	
	@Override
	public boolean solve() {
		if (index < and.formulae().length) {
			if (!queue.isEmpty()) {
				bindings = queue.remove();
//				System.out.println("\tprocessing: " + and.formulae()[index] + " / bindings: " + bindings);
				try {
					reasoner.stack.push(reasoner.newStackEntry(and.formulae()[index], bindings));
				} catch (NullPointerException e) {
					System.err.println("Formula: " + and.formulae()[index]);
					e.printStackTrace();
				}
			} else {
//				System.out.println("\tnext [" + index + "]: " + next);
				queue = next;
				next = new LinkedList<Map<Integer, Term>>();
				index++;
			}
		} else {
//			System.out.println("\tpropogating final bindings: " + solutions);
			while (!solutions.isEmpty()) {
				reasoner.propagateBindings(solutions.remove(0));
			}
			reasoner.stack.pop();
		}
		
		return true;
	}

	public String toString() {
		return "[ANDStackEntry]: " + and.toString();
	}

	@Override
	public boolean failure() {
		// If a single formula is false, then the AND formula is
		// false...
		if (queue.isEmpty() && solutions.isEmpty()) {
//			System.out.println("No more solutions");
			return true;
		}
		
//		System.out.println("[ANDStackEntry]: " + and + " - More solutions to handle...");
		return false;
	}

	@Override
	public void addBindings(Map<Integer, Term> bindings) {
//		System.out.println("[ANDStackEntry]: Propogating Bindings: " + bindings);
		if (index < and.formulae().length-1) {
//			System.out.println("[" + index + "] adding to next:"  + bindings);
			next.add(bindings);
		} else {
//			System.out.println("adding to solutions:"  + bindings);
			solutions.add(bindings);
		}
	}
}
