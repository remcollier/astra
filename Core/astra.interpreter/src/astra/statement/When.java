package astra.statement;

import java.util.List;
import java.util.Map;

import astra.core.Intention;
import astra.core.Agent.Promise;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public class When extends AbstractStatement {
	Formula guard;
	Statement body;
	
	public When(String clazz, int[] data, Formula guard, Statement body) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
		this.body = body;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			@Override
			public boolean execute(Intention intention) {
				switch(state) {
				case 0:
					intention.makePromise(new Promise((Formula) guard.accept(new ContextEvaluateVisitor(intention))) {
						@Override
						public void act(List<Map<Integer, Term>> bindings) {
							intention.addStatement(body.getStatementHandler(), bindings.get(0));
							intention.resume();
						}
					});
					intention.suspend();
					state = 1;
					return true;
				case 1:
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return When.this;
			}
			
		};
	}

}
