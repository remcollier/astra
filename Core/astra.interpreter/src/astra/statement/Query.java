package astra.statement;

import java.util.Map;
import java.util.Map.Entry;

import astra.core.Intention;
import astra.formula.Formula;
import astra.term.Term;
import astra.term.Variable;
import astra.util.ContextEvaluateVisitor;

public class Query extends AbstractStatement {
	Formula guard;
	
	public Query(String clazz, int[] data, Formula guard) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.guard = guard;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			
			@Override
			public boolean execute(Intention intention) {
				try { 
					Map<Integer, Term> result = intention.query((Formula) guard.accept(new ContextEvaluateVisitor(intention)));
					if (result != null) {
						for(Entry<Integer, Term> entry: result.entrySet()) {
							intention.addVariable(new Variable(entry.getValue().type(), entry.getKey()), entry.getValue());
						}
					} else {
						intention.failed("Could not resolve query: " + guard, null);
					}
				} catch (Throwable th) {
					intention.failed("Failed to match guard", th);
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return Query.this;
			}
			
			public String toString() {
				return Query.this.toString();
			}
			
		};
	}

	public String toString() {
		return "query(" + guard + ")";
	}
}
