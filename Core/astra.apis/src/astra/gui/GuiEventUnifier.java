package astra.gui;


import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.event.Event;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class GuiEventUnifier implements EventUnifier {

	@Override
	public Map<Integer, Term> unify(Event source, Event target, Agent agent) {
		GuiEvent s = (GuiEvent) source;
		GuiEvent t = (GuiEvent) target;
	
		return Unifier.unify(
				new Term[] {s.type, s.args}, 
				new Term[] {t.type, t.args}, 
				new HashMap<Integer, Term>(),
				agent);
	}


}
