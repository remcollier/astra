package astra.tr;

import java.util.Map;

import astra.term.Term;

public class TRStopAction extends AbstractAction {
	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {
			@Override
			public boolean execute(TRContext context, Map<Integer, Term> bindings) {
//				System.out.println("stopping...");
				context.stopFunction();
				return false;
			}
		};
	}

}
