package astra.cartago;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class CartagoSignalEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		CartagoSignalEvent s = (CartagoSignalEvent) source;
		CartagoSignalEvent t = (CartagoSignalEvent) target;
	
		Map<Integer, Term> bindings = Unifier.unify(new Term[] {s.id()}, new Term[] {t.id()}, new HashMap<Integer, Term>(), agent);
		
		if (bindings != null) {
			bindings = Unifier.unify(s.content(), t.content(), bindings, agent);
		}

		return bindings;
	}

}
