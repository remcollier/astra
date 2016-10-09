package astra.reasoner;

import java.util.Map;

import astra.cartago.CartagoProperty;
import astra.formula.Formula;
import astra.term.Term;

public class CartagoPropertyStackEntryFactory implements ReasonerStackEntryFactory {
	@Override
	public ReasonerStackEntry create(ResolutionBasedReasoner reasoner, Formula formula, Map<Integer, Term> bindings) {
		return new CartagoPropertyStackEntry(reasoner, (CartagoProperty) formula, bindings);
	}
}
