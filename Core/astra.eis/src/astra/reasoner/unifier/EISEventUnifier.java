package astra.reasoner.unifier;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.eis.EISEvent;
import astra.event.Event;
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
			bindings = Unifier.unify(new Term[] {s.entity(), s.content()}, new Term[] {t.entity(), t.content()}, bindings, agent);
		}

		return bindings;
	}

}
