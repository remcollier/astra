package astra.statement;

import astra.core.Agent.Promise;
import astra.core.Intention;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;

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
			@Override
			public boolean execute(Intention intention) {
				switch (state) {
				case 1:
					state = 0;
					return true;
				case 0:
					intention.makePromise(new Promise((Formula) guard.accept(new ContextEvaluateVisitor(intention))) {
						@Override
						public void act() {
							intention.resume();
						}
					});
					intention.suspend();

					state = 1;
				}
				return false;
//				try {
//					Formula query = (Formula) guard.accept(new ContextEvaluateVisitor(intention));
//					Map<Integer, Term> bindings = intention.query(query);
//					if (bindings == null) return true;
//				} catch (Throwable th) {
//					intention.failed("query cannot be resolved " + guard, th);
//				}
//				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return Wait.this;
			}
			
		};
	}

}
