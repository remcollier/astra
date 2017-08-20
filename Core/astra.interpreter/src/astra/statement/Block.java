package astra.statement;

import astra.core.Intention;

public class Block extends AbstractStatement {
	Statement[] statements;
	
	public Block(Statement[] statements) {
		this.statements = statements;
	}

	public Block(String clazz, int data[], Statement[] statements) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.statements = statements;
	}

	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int index = 0;
			
			public boolean execute(Intention context) {
				if (index < statements.length) {
					context.addStatement(statements[index++].getStatementHandler());
					context.execute();
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
				return Block.this;
			}

			public String toString() {
				return "block: " + index + " of " + statements.length;
			}
		};
	}
	
	public String toString() {
		String out = "{\n";
		for (int i=0; i<statements.length;i++) {
			out += statements[i].toString()+"\n";
		}
		return out+"}";
	}
}
