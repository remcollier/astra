package astra.reasoner.unifier;

import java.util.Map;

import astra.core.Agent;
import astra.event.BeliefEvent;
import astra.formula.Predicate;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class BeliefEventUnifier implements EventUnifier<BeliefEvent> {

	@Override
	public Map<Integer, Term> unify(BeliefEvent source, BeliefEvent target, Agent agent) {
		return (source.type() == target.type()) ? Unifier.unify((Predicate) source.belief(), (Predicate) target.belief(), agent):null;
	}

}
