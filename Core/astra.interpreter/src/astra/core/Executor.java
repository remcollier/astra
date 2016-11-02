package astra.core;

import astra.statement.StatementHandler;

public interface Executor {
	public boolean execute(Intention intention);
	public void addStatement(StatementHandler handler);
}
