package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.ContextEvaluateVisitor;

public class BeliefUpdate extends AbstractStatement {
	public static final char ADDITION = '+';
	public static final char DELETION = '-';
	
	Predicate predicate;
	char op;
	
	public BeliefUpdate(char op, Predicate predicate) {
		this.op = op;
		this.predicate = predicate;
	}

	public BeliefUpdate(char op, String clazz, int[] data, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.op = op;
		this.predicate = predicate;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@Override
			public boolean execute(Intention context) {
				ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context);
				// construct belief to be added...
				Term[] terms = new Term[predicate.size()];
				for (int i=0; i < terms.length; i++) {
					terms[i] = (Term) predicate.getTerm(i).accept(visitor);
				}
				
				Predicate belief = new Predicate(predicate.predicate(), terms);
				
//				System.out.println("Adding: " + belief);
				if (op == ADDITION) {
					context.addBelief(belief);
				} else if (op == DELETION) {
					context.removeBelief(belief);
				}
				
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return BeliefUpdate.this;
			}
			
		};
	}
}
