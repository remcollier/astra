package astra.tr;

import java.util.Map;

import astra.term.Term;

public class CompositeAction extends AbstractAction {
	Action[] actions;
	
	public CompositeAction(Action...actions) {
		this.actions = actions;
	}
	
	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {

			@Override
			public boolean execute(TRContext context, Map<Integer, Term> bindings) {
				for (Action action : actions) {
					if (!action.getStatementHandler().execute(context, bindings)) return false;
				}
				return true;
			}
		};
	}

}
