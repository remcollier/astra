package astra.cartago;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.event.Event;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class CartagoEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target) {
		CartagoASTRAEvent s = (CartagoASTRAEvent) source;
		CartagoASTRAEvent t = (CartagoASTRAEvent) target;
	
		List<Term> slist = new ArrayList<Term>();
		List<Term> tlist = new ArrayList<Term>();
		if (s.type() != null && t.type() != null) { slist.add(s.type()); tlist.add(t.type()); }
		if (s.id() != null && t.id() != null) { slist.add(s.id()); tlist.add(t.id()); }
		Map<Integer, Term> bindings = Unifier.unify(slist.toArray(new Term[slist.size()]), tlist.toArray(new Term[tlist.size()]), new HashMap<Integer, Term>());
//		System.out.println("source: " + s + " / target: " + t + " bindings: " + bindings);
		if (bindings != null) {
			bindings = Unifier.unify(s.content(), t.content(), bindings);
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
