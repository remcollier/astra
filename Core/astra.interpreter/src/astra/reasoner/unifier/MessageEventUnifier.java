package astra.reasoner.unifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.messaging.MessageEvent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class MessageEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		MessageEvent s = (MessageEvent) source;
		MessageEvent t = (MessageEvent) target;

		List<Term> slist = new ArrayList<Term>();
		List<Term> tlist = new ArrayList<Term>();
		slist.add(s.performative());
		tlist.add(t.performative());
		slist.add(s.sender());
		tlist.add(t.sender());
		if (s.params() != null) {
			slist.add(s.params());
			tlist.add(t.params());
		}
		
		Map<Integer, Term> bindings = Unifier.unify(slist.toArray(new Term[slist.size()]), tlist.toArray(new Term[tlist.size()]), new HashMap<Integer, Term>(), agent);
//		System.out.println("s: "+ s);
//		System.out.println("t: "+ t);
//		System.out.println("result: "+ bindings);
		if (bindings != null) {
			return Unifier.unify(s.content(), t.content(), bindings, agent);
		}
		return null;
	}
}
