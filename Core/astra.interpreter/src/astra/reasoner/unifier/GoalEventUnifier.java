package astra.reasoner.unifier;

import java.util.Map;

import astra.core.Agent;
import astra.event.GoalEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class GoalEventUnifier implements EventUnifier<GoalEvent> {
	@Override
	public Map<Integer, Term> unify(GoalEvent source, GoalEvent target, Agent agent) {
		return (source.type() == target.type()) ? Unifier.unify(source.goal, target.goal, agent):null;
	}

}
