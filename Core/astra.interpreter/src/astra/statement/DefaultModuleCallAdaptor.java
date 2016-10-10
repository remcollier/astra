package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public abstract class DefaultModuleCallAdaptor implements ModuleCallAdaptor {
	@Override
	public boolean suppressNotification() {
		return false;
	}
	
	protected Predicate evaluate(Intention intention, Predicate predicate) {
		return (Predicate) predicate.accept(new ContextEvaluateVisitor(intention));
	}
}
