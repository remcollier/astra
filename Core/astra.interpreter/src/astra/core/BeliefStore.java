package astra.core;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.formula.Formula;
import astra.formula.Predicate;

/**
 * Container for storing beliefs.
 * 
 * Beliefs are organised into lists of formulae that have the same predicate through the use of
 * a Map.
 *  
 * @author Rem
 *
 */
public class BeliefStore {
	private Map<Integer, List<Formula>> store = new HashMap<Integer, List<Formula>>();
	int size = 0;
	
	public boolean addBelief(Predicate belief) {
		List<Formula> list = store.get(belief.id());
		if (list == null) {
			list = new LinkedList<Formula>();
			store.put(belief.id(), list);
		}
		
		for (Formula element : list) {
			if (element.equals(belief)) return false;
		}
		
		list.add(belief);
		size++;
		return true;
	}
	
	public boolean containsBelief(Predicate belief) {
		List<Formula> list = store.get(belief.id());
		if (list == null) return false;
		
		for (Formula element : list) {
			if (element.equals(belief)) return true;
		}
		
		return false;
	}

	public boolean removeBelief(Predicate belief) {
		List<Formula> list = store.get(belief.id());
		if (list == null) return false;
		
		for (Formula element : list) {
			if (element.equals(belief)) {
				list.remove(element);
				size--;
				return true;
			}
		}
		
		return false;
	}

	public List<Formula> beliefs() {
		List<Formula> list = new LinkedList<Formula>();
		for (List<Formula> l : store.values()) {
			list.addAll(l);
		}
		return list;
	}

	public void clear() {
		store.clear();
	}

	public List<Formula> getMatchingBeliefs(Predicate formula) {
		List<Formula> list = store.get(formula.id());
		if (list != null) return list;
		return new LinkedList<Formula>();
	}

	public int size() {
		return size;
	}
}

