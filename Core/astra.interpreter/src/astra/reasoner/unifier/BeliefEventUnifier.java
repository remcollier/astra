package astra.reasoner.unifier;

import java.util.Map;

import astra.core.Agent;
import astra.event.BeliefEvent;
import astra.event.Event;
import astra.formula.Predicate;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class BeliefEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		BeliefEvent s = (BeliefEvent) source;
		BeliefEvent t = (BeliefEvent) target;
	
		if  (s.type() == t.type()) {
			return Unifier.unify((Predicate) s.belief(), (Predicate) t.belief(), agent);
		}
		return null;
	}

}
