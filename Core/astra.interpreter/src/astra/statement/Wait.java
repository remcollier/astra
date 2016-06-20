package astra.statement;

import java.util.Map;

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
			@Override
			public boolean execute(Intention intention) {
				try {
					Formula query = (Formula) guard.accept(new ContextEvaluateVisitor(intention));
					Map<Integer, Term> bindings = intention.query(query);
					if (bindings == null) return true;
				} catch (Throwable th) {
					intention.failed("query cannot be resolved " + guard, th);
				}
				return false;
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
