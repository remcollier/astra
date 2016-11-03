package astra.statement;

import astra.core.Agent.Promise;
import astra.core.Intention;
import astra.formula.Formula;
import astra.formula.NOT;
import astra.reasoner.util.ContextEvaluateVisitor;

public class MaintainBlock extends AbstractStatement {
	Statement statement;
	Formula formula;
	
	public MaintainBlock(String clazz, int[] data, Formula formula, Statement statement) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.formula = formula;
		this.statement = statement;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			Promise promise;
			
			public boolean execute(Intention intention) {
				switch (state) {
				case 0:
					intention.makePromise(promise = new Promise((Formula) formula.accept(new ContextEvaluateVisitor(intention)), true) {
						@Override
						public void act() {
							intention.failed("Maintenance Condition False: "+ formula);
							intention.resume();
						}
					});
					intention.addStatement(statement.getStatementHandler());
					state=1;
					break;
				case 1:
					intention.dropPromise(promise);
					return false;
				}
				return true;
			}

			@Override
			public boolean onFail(Intention intention) {
				return false;
			}

			@Override
			public Statement statement() {
				return MaintainBlock.this;
			}
		};
	}

}
