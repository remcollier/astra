package astra.statement;

import java.util.List;
import java.util.Map;

import astra.core.Agent.Promise;
import astra.core.Intention;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public class Wait extends AbstractStatement {
	Formula guard;
	
	public Wait(String clazz, int[] data, Formula guard) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			Promise promise;
			
			@Override
			public boolean execute(Intention intention) {
				switch (state) {
				case 1:
					state = 0;
					return true;
				case 0:
					intention.makePromise(promise=new Promise((Formula) guard.accept(new ContextEvaluateVisitor(intention))) {
						@Override
						public void act(List<Map<Integer, Term>> bindings) {
							intention.addBindings(bindings.get(0));
							intention.resume();
						}
					});
					intention.suspend();

					state = 1;
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				context.dropPromise(promise);
				return false;
			}

			@Override
			public Statement statement() {
				return Wait.this;
			}
			
		};
	}

}
