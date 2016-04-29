package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.ContextEvaluateVisitor;

public class ScopedBeliefUpdate extends AbstractStatement {
	String scope;
	Predicate predicate;
	char op;
	
	
	public ScopedBeliefUpdate(String scope, char op, Predicate predicate) {
		this.scope = scope;
		this.op = op;
		this.predicate = predicate;
	}
	
	public ScopedBeliefUpdate(String clazz, int[] data, String scope, char op, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.scope = scope;
		this.op = op;
		this.predicate = predicate;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			@Override
			public boolean execute(Intention intention) {
				ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(intention);
				// construct belief to be added...
				Term[] terms = new Term[predicate.size()];
				for (int i=0; i < terms.length; i++) {
					terms[i] = (Term) predicate.getTerm(i).accept(visitor);
				}
				
				Predicate belief = new Predicate(predicate.predicate(), terms);
				
				if (op == BeliefUpdate.ADDITION) {
					intention.addScopedBelief(scope, belief);
				} else if (op == BeliefUpdate.DELETION) {
					intention.removeScopedBelief(scope, belief);
				}
				
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}
			
			@Override
			public Statement statement() {
				return ScopedBeliefUpdate.this;
			}
		};
	}
}
