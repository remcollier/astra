package astra.cartago;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class CartagoPropertyEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		CartagoPropertyEvent s = (CartagoPropertyEvent) source;
		CartagoPropertyEvent t = (CartagoPropertyEvent) target;
	
		if (!s.type().equals(t.type())) return null;

		Map<Integer, Term> bindings = Unifier.unify(new Term[] {s.id()}, new Term[] {t.id()}, new HashMap<Integer, Term>(), agent);
		
		if (bindings != null) {
			bindings = Unifier.unify(s.content(), t.content(), bindings, agent);
//			System.out.println("\tbindings: " + bindings);
			if (bindings != null) {
//				System.out.println("\t final  bindings: " + bindings);
				return bindings;
//			} else {
//				System.out.println("failed to bind..");
			}
		}

		return null;
	}

}
