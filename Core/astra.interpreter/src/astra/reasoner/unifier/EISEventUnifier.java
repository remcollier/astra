package astra.reasoner.unifier;

import java.util.HashMap;
import java.util.Map;

import astra.eis.EISEvent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class EISEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target) {
		EISEvent s = (EISEvent) source;
		EISEvent t = (EISEvent) target;
	
		if (s.type() != t.type()) return null;
		
		Map<Integer, Term> bindings = null;
		if (s.type() == EISEvent.ENVIRONMENT) {
			bindings = Unifier.unify(new Term[] {s.id()}, new Term[] {t.id()}, new HashMap<Integer, Term>());
		} else {
			bindings = Unifier.unify(new Term[] {s.id(),s.entity()}, new Term[] {t.id(),t.entity()}, new HashMap<Integer, Term>());
		}
		
		if (bindings != null) {
			bindings = Unifier.unify(s.content(), t.content(), bindings);
		}

		return bindings;
	}

}
