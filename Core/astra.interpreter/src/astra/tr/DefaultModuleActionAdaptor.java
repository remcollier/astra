package astra.tr;

import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;

public abstract class DefaultModuleActionAdaptor implements ModuleActionAdaptor {
	protected Predicate evaluate(TRContext context, Predicate predicate) {
		Term[] terms = new Term[predicate.size()];
		for(int i=0; i<predicate.size(); i++) {
			terms[i] = Primitive.newPrimitive(context.getValue(predicate.termAt(i)));
			
		}
		return new Predicate(predicate.predicate(), terms);
	}

}
