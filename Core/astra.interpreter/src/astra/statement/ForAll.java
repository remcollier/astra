package astra.statement;

import astra.core.Intention;
import astra.term.ListTerm;
import astra.term.Term;
import astra.term.Variable;
import astra.util.ContextEvaluateVisitor;

public class ForAll extends AbstractStatement {
	Variable variable;
	Term list;
	Statement body;
	
	public ForAll(String clazz, int[] data, Variable variable, Term list, Statement body) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.variable = variable;
		this.list = list;
		this.body = body;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int index = 0;
			ListTerm l;
			
			@Override
			public boolean execute(Intention intention) {
				if (index == 0) {
					Object obj = list.accept(new ContextEvaluateVisitor(intention));
					if (obj instanceof ListTerm) {
						l = (ListTerm) obj;
					} else {
						intention.failed("Second argument of forall is not a list.");
						return false;
					}
				}
				
				if (index < l.size()) {
					intention.addStatement(body.getStatementHandler());
					intention.addVariable(variable, l.get(index++));
					return true;
				}
				
				intention.removeVariable(variable);
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return ForAll.this;
			}
			
		};
	}

}
