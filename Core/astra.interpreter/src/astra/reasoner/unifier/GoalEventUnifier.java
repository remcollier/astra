package astra.reasoner.unifier;

import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class GoalEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		GoalEvent s = (GoalEvent) source;
		GoalEvent t = (GoalEvent) target;
	
		if  (s.type() == t.type()) {
			return Unifier.unify(s.goal, t.goal, agent);
		}
		return null;
	}

}
