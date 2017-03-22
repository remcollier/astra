package astra.statement;

import java.util.List;
import java.util.Map;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;
import astra.term.Variable;

public class SpecialBeliefUpdate extends AbstractStatement {
	Predicate predicate;
	
	public SpecialBeliefUpdate(Predicate predicate) {
		this.predicate = predicate;
	}

	public SpecialBeliefUpdate(String clazz, int[] data, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
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
				Term[] variables = new Term[predicate.size()];
				for (int i=0; i < terms.length; i++) {
					terms[i] = (Term) predicate.getTerm(i).accept(visitor);
					variables[i] = new Variable(predicate.getTerm(i).type(), "_"+i);
				}
				
				// Match he variable version of the belief against all possible matches.
				// Remove the matching beliefs and replace them with the new belief.
				// NOTE: This is different to the Jason implementation, which removes
				// only the oldest matching belief.
				Predicate belief = new Predicate(predicate.predicate(), terms);
				Predicate matcher = new Predicate(predicate.predicate(), variables);

				List<Map<Integer, Term>> bindings = context.queryAll(matcher);
				for (Map<Integer, Term> binding : bindings) {
					context.removeBelief((Predicate) matcher.accept(new BindingsEvaluateVisitor(binding,context.agent)));
				}
				context.addBelief(belief);
				
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return SpecialBeliefUpdate.this;
			}
			
		};
	}
}
