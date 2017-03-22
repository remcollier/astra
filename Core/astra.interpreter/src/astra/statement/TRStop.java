package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;

public class TRStop extends AbstractStatement {
	public TRStop(String clazz, int[] data) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention context) {
				if (!context.stopFunction()) {
					context.failed("No TR Function running");
					return true;
				}
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
