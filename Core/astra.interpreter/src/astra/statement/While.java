package astra.statement;

import java.util.Map;

import astra.core.Intention;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public class While extends AbstractStatement {
	Formula guard;
	Statement body;
	
	public While(String clazz, int[] data, Formula guard, Statement body) {
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
						} else{
							state = 1;
						}
					} catch (Throwable th) {
						intention.failed("Failure matching guard", th);
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
				return While.this;
			}
			
		};
	}

}
