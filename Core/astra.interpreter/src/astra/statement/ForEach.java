package astra.statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.core.Intention;
import astra.formula.Formula;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.term.Term;
import astra.term.Variable;

public class ForEach extends AbstractStatement {
	Formula guard;
	Statement body;
	
	public ForEach(String clazz, int[] data, Formula guard, Statement body) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
		this.body = body;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			List<Map<Integer, Term>> results;
			VariableVisitor visitor;
			
			@Override
			public boolean execute(Intention intention) {
				switch(state) {
				case 0:
//					System.out.println("guard: " + guard);
					Formula g =(Formula) guard.accept(new ContextEvaluateVisitor(intention));
					visitor = new VariableVisitor();
					g.accept(visitor);
					try {
						results = intention.queryAll(g);
						if (results != null) {
	//						System.out.println("Results : " + results.size());
							if (results.isEmpty()) {
								intention.addStatement(body.getStatementHandler(), new HashMap<Integer, Term>());
								state = 2;
								return true;
							}
							state = 1;
						} else {
							return false;
						}
					} catch (Throwable th) {
						intention.failed("Failed to match guard", th);
					}
					return true;
				case 1:
//					System.out.println("processing: " + results.get(0) + " / size: " + results.size());
					intention.addStatement(body.getStatementHandler(), results.remove(0));
					if (results.isEmpty()) state = 2;
					return true;
				case 2:
					// Remove the variables that were added for this statement as they are
					// now out of scope...
					for (Variable variable : visitor.variables()) {
						intention.removeVariable(variable);
					}
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return ForEach.this;
			}
			
		};
	}

}
