package astra.reasoner.unifier;

import java.util.HashMap;
import java.util.Map;

import astra.acre.AcreEvent;
import astra.core.Agent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class AcreEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		AcreEvent s = (AcreEvent) source;
		AcreEvent t = (AcreEvent) target;
	
		if ( s.type().equals( AcreEvent.ADVANCED ) ) {
			return Unifier.unify(new Term[] {s.conversationId(), s.state(), s.length()}, new Term[] {t.conversationId(), t.state(), t.length()}, new HashMap<Integer, Term>(),agent);
		} else if ( s.type().equals( AcreEvent.MESSAGE ) ) {
			Map<Integer, Term> bindings = Unifier.unify(new Term[] {s.performative(), s.conversationId()}, new Term[] {t.performative(), t.conversationId()}, new HashMap<Integer, Term>(),agent);
			if ((bindings != null) && (s.content().id() == t.content().id()))  
				return Unifier.unify(s.content().terms(), t.content().terms(), bindings,agent);
			return null;
		} else {
			return Unifier.unify(new Term[] {s.type(), s.conversationId() }, new Term[] {t.type(), t.conversationId()}, new HashMap<Integer, Term>(),agent);
		}
	}

}
