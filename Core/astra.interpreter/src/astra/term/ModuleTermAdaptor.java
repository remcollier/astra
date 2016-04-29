package astra.term;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.util.BindingsEvaluateVisitor;

public abstract class ModuleTermAdaptor {
	public abstract Object invoke(Intention context, Predicate atom);
	public abstract Object invoke(BindingsEvaluateVisitor visitor, Predicate atom);

	@SuppressWarnings({ "unchecked" })
	public <T> T evaluate(BindingsEvaluateVisitor visitor, Term term) {
		if (term instanceof Primitive) {
			T val =  ((Primitive<T>) term).value();
			return val;
		}
		
		if (term instanceof Variable) {
			return evaluate(visitor, (Term) term.accept(visitor));
		}
		
		if (term instanceof Operator) {
			return ((Primitive<T>) term.accept(visitor)).value();
		}
		System.out.println("[ModuleTermAdaptor] FAILED TO EVALUATE: " + term.getClass().getName());

		return null;
	}	
}
