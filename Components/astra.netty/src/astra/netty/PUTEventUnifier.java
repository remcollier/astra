package astra.netty;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class PUTEventUnifier implements EventUnifier<PUTEvent> {
	@Override
	public Map<Integer,Term> unify(PUTEvent source, PUTEvent target, Agent agent) {
    	return Unifier.unify(
    		new Term[] {source.context, source.request, source.arguments, source.fields}, 
    		new Term[] {target.context, target.request, target.arguments, source.fields}, 
    		new HashMap<Integer,Term>(),
    		agent);
    }
 }