package astra.tr;

import java.util.Map;

import astra.formula.Predicate;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;

public class TRStopAction extends AbstractAction {
	Predicate call;
	
	public TRStopAction(Predicate call) {
		this.call = call;
	}
	
	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {
			@Override
			public boolean execute(TRContext context, Map<Integer, Term> bindings) {
//				System.out.println("stopping...");
				context.stopFunction((Predicate) call.accept(new BindingsEvaluateVisitor(bindings, context.agent)));
				return false;
			}
		};
	}

}
