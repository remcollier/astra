package astra.netty;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class GETEventUnifier implements EventUnifier<GETEvent> {
	@Override
	public Map<Integer,Term> unify(GETEvent source, GETEvent target, Agent agent) {
    	System.out.println("here");
    	return Unifier.unify(
    		new Term[] {source.context, source.request, source.arguments}, 
    		new Term[] {target.context, target.request, target.arguments}, 
    		new HashMap<Integer,Term>(),
    		agent);
    }
 }