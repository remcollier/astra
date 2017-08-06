package astra.reasoner;

import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.term.Term;

public interface EventUnifier<T extends Event> {
	public Map<Integer, Term> unify(T source, T target, Agent agent);
}
