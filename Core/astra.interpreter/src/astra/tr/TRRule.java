package astra.tr;

import java.util.Map;

import astra.formula.Formula;
import astra.term.Term;

public class TRRule {
	Formula condition;
	Action action;
	
	public TRRule(Formula condition, Action action) {
		this.condition = condition;
		this.action = action;
	}

	public void execute(TRContext Context, Map<Integer, Term> bindings) {
		action.getStatementHandler().execute(Context, bindings);
	}
}
