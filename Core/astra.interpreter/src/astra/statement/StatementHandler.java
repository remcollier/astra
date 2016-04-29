package astra.statement;

import astra.core.Intention;

public interface StatementHandler {
	public boolean execute(Intention Intention);

	public boolean onFail(Intention intention);

	public Statement statement();
	
}
