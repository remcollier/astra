package astra.reasoner;

import java.util.Map;

import astra.term.Term;



public interface ReasonerStackEntry {
	public boolean solve();
	public boolean failure();
	public void addBindings(Map<Integer,Term> bindings);
}
