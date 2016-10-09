package astra.reasoner.unifier;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.eis.EISEvent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class EISEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		EISEvent s = (EISEvent) source;
		EISEvent t = (EISEvent) target;
//		System.out.println("source: " + source + " / " + s.type());
//		System.out.println("target: " + target + " / " + t.type());
		
		
		if (s.type() != t.type()) return null;
		
		Map<Integer, Term> bindings = new HashMap<Integer, Term>();
		if (s.type() != EISEvent.ENVIRONMENT) {
			bindings = Unifier.unify(new Term[] {s.entity()}, new Term[] {t.entity()}, bindings, agent);
		}
//		System.out.println("bindings:" + bindings);
		
		if (bindings != null) {
			bindings = Unifier.unify(s.content(), t.content(), bindings, agent);
		}

		return bindings;
	}

}
