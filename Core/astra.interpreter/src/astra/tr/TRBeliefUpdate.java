package astra.tr;

import java.util.Map;

import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.term.Term;

public class TRBeliefUpdate extends AbstractAction {
	public static final char ADDITION = '+';
	public static final char DELETION = '-';
	
	Predicate predicate;
	char op;
	
	public TRBeliefUpdate(char op, String clazz, int[] data, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.op = op;
		this.predicate = predicate;
	}

	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {
			@Override
			public boolean execute(TRContext context, Map<Integer, Term> bindings) {
				BindingsEvaluateVisitor visitor = new BindingsEvaluateVisitor(bindings, context.agent);
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
				return true;
			}
		};
	}

}
