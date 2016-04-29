package astra.statement;

import astra.core.Intention;

public class SynchronizedBlock extends AbstractStatement {
	Statement statement;
	String token;
	
	public SynchronizedBlock(String clazz, int[] data, String token, Statement statement) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.token = token;
		this.statement = statement;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			
			public boolean execute(Intention context) {
				if (state == 0 && context.hasLock(token, context)) {
					// Here, the agent already has the lock for the token, so we treat
					// the synchronized block as a normal block...
					context.addStatement(statement.getStatementHandler());
				} else {
					// Here the agent does not have a lock, so it must request the lock
					// perform the steps, and then release the lock...
					switch (state) {
					case 0:
						state++;
						if (!context.requestLock(token, context)) {
							break;
						}
					case 1:
						state++;
						context.addStatement(statement.getStatementHandler());
						break;
					case 2:
						context.releaseLock(token, context);
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean onFail(Intention context) {
				if (state == 2) {
					context.releaseLock(token, context);
				} else {
					context.unrequestLock(token, context);
				}
				return false;
			}

			@Override
			public Statement statement() {
				return SynchronizedBlock.this;
			}
			
		};
	}

}
