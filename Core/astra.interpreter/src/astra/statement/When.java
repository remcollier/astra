package astra.statement;

import java.util.Map;

import astra.core.Intention;
import astra.formula.Formula;
import astra.term.Term;
import astra.util.ContextEvaluateVisitor;

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
					try {
						Map<Integer, Term> bindings = intention.query((Formula) guard.accept(new ContextEvaluateVisitor(intention)));
						if (bindings != null) {
							intention.addStatement(body.getStatementHandler(), bindings);
							intention.execute();
							state = 1;
						}
					} catch (Throwable th) {
						intention.failed("Failed matching guard", th);
					}
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
