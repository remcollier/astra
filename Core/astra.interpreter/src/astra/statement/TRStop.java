package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.util.ContextEvaluateVisitor;

public class TRStop extends AbstractStatement {
	Predicate function;
	
	public TRStop(String clazz, int[] data, Predicate function) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.function = function;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention context) {
				ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context); 
				context.stopFunction((Predicate) function.accept(visitor));
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return TRStop.this;
			}
		};
	}
}
