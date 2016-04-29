package astra.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.event.BeliefEvent;
import astra.event.Event;
import astra.event.ScopedBeliefEvent;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.Queryable;

public class EventBeliefManager implements Queryable {
	BeliefStore store = new BeliefStore();

	Agent agent;
	List<Predicate> addedBeliefs = Collections.synchronizedList(new LinkedList<Predicate>());
	List<Predicate> droppedBeliefs = Collections.synchronizedList(new LinkedList<Predicate>());
	
	public EventBeliefManager(Agent agent) {
		this.agent = agent;
	}
	
	public void addBelief(Predicate belief) {
		addedBeliefs.add(belief);
	}
	
	public void dropBelief(Predicate belief) {
		droppedBeliefs.add(belief);
	}
	
	public void update() {
		while (!addedBeliefs.isEmpty()) {
			Predicate belief = addedBeliefs.remove(0);
			if (store.addBelief(belief)) {
				String scope = scopes.remove(belief);
				if (scope != null) {
					agent.addEvent(new ScopedBeliefEvent(scope, new BeliefEvent(Event.ADDITION, belief)));
				} else {
					agent.addEvent(new BeliefEvent(Event.ADDITION, belief));
				}
			}
		}
		
		while (!droppedBeliefs.isEmpty()) {
			Predicate belief = droppedBeliefs.remove(0);
			if (store.removeBelief(belief)) {
				String scope = scopes.remove(belief);
				if (scope != null) {
					agent.addEvent(new ScopedBeliefEvent(scope, new BeliefEvent(Event.REMOVAL, belief)));
				} else {
					agent.addEvent(new BeliefEvent(BeliefEvent.REMOVAL, belief));
				}
			}
		}
		
//		dumpBeliefs();
	}

	@Override
	public List<Formula> getMatchingFormulae(Formula formula) {
		if (formula instanceof Predicate) {
			return store.getMatchingBeliefs((Predicate) formula);
		}
		return new LinkedList<Formula>();
	}

	public int size() {
		return store.size();
	}

	public List<Formula> beliefs() {
		return store.beliefs();
	}

	public void dumpBeliefs() {
		for (Formula belief : store.beliefs()) {
			System.err.println(belief);
		}
	}

	public boolean hasUpdates() {
		return !addedBeliefs.isEmpty() || !droppedBeliefs.isEmpty();
	}

	private Map<Predicate, String> scopes = new HashMap<Predicate, String>();
	
	public void addScopedBelief(String scope, Predicate belief) {
		scopes.put(belief, scope);
		addBelief(belief);
	}

	public void dropScopedBelief(String scope, Predicate belief) {
		scopes.put(belief, scope);
		dropBelief(belief);
	}

	public BeliefStore store() {
		return this.store;
	}
}
