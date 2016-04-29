package astra.reasoner;

import java.util.Map;

import astra.event.Event;
import astra.term.Term;

public interface EventUnifier {
	public Map<Integer, Term> unify(Event source, Event target);
}
