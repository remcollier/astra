package astra.tr;

import java.util.Map;

import astra.term.Term;

public interface ActionHandler {
	public boolean execute(TRContext context, Map<Integer, Term> bindings);
}
