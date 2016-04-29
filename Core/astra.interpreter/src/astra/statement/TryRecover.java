package astra.statement;

import astra.core.Intention;

public class TryRecover extends AbstractStatement {
	Statement tryStatement, recoverStatement;
	
	public TryRecover(String clazz, int[] data, Statement ifStatement, Statement elseStatement) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.tryStatement = ifStatement;
		this.recoverStatement = elseStatement;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			@Override
			public boolean execute(Intention intention) {
//				System.out.println("state: " + state);
				switch(state) {
				case 0:
//					System.out.println("adding try statement");
					intention.addStatement(tryStatement.getStatementHandler());
//					intention.dumpVariableTables();
//					intention.dumpStack();
					state = 2;
					break;
				case 1:
					intention.removeBindings();
//					System.out.println("adding recover statement");
					intention.addStatement(recoverStatement.getStatementHandler());
//					intention.dumpVariableTables();
//					intention.dumpStack();
					state = 3;
					break;
				case 2:
				case 3:
//					System.out.println("___________________________________________ UNWINDING");
//					intention.dumpVariableTables();
//					intention.dumpStack();
					return false;
				}
				
				return true;
			}

			@Override
			public boolean onFail(Intention context) {
				// Failure in try - switch to recovery plan
				if (state == 2) {
					state = 1;
					return true;
				}
				
				// This reflects failure of the recovery plan
				if (state == 3) return false;
				
				// default return value - failure handled here...
				return true;
			}

			@Override
			public Statement statement() {
				return TryRecover.this;
			}
			
		};
	}

}
