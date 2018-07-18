package astra.netty;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class DELETEEventUnifier implements EventUnifier<DELETEEvent> {
	@Override
	public Map<Integer,Term> unify(DELETEEvent source, DELETEEvent target, Agent agent) {
    	System.out.println("here");
    	return Unifier.unify(
    		new Term[] {source.context, source.request, source.arguments}, 
    		new Term[] {target.context, target.request, target.arguments}, 
    		new HashMap<Integer,Term>(),
    		agent);
    }
 }