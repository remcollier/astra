package astra.core;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.formula.Formula;
import astra.formula.Predicate;
import astra.type.Type;


public class HierarchicalBeliefStore {
	class StoreNode {
		int index;
		List<Formula> predicates = new LinkedList<Formula>();
		Map<Type, StoreNode> layers = new HashMap<Type, StoreNode>();
		
		public StoreNode(int index) {
			this.index = index;
		}
		
		public boolean add(Predicate predicate) {
			if (predicate.size() == index) {
				if (!predicates.contains(predicate)) {
					predicates.add(predicate);
					return true;
				} else {
					return false;
				}
			} else {
				StoreNode layer = layers.get(predicate.getTerm(index).type());
				if (layer == null) {
					layer = new StoreNode(index+1);
					layers.put(predicate.getTerm(index).type(), layer);
				}
				return layer.add(predicate);
			}
		}
		
		public boolean remove(Predicate predicate) {
			if (predicate.size() == index) {
				return predicates.remove(predicate);
			} else {
				StoreNode layer = layers.get(predicate.getTerm(index).type());
				if (layer == null) {
					return false;
				}
				return layer.remove(predicate);
			}
		}
		
		public List<Formula> match(Predicate predicate) {
			if (predicate.size() == index) {
				return predicates;
			} else {
				StoreNode layer = layers.get(predicate.getTerm(index).type());
				if (layer == null) {
					return new LinkedList<Formula>();
				}
				return layer.match(predicate);
			}
		}
		
		public void getBeliefs(List<Formula> list) {
			list.addAll(predicates);
			for (StoreNode node : layers.values()) {
				node.getBeliefs(list);
			}
		}
	}
	
	private StoreNode root = new StoreNode(0);
	private int size = 0;
	
	public boolean addBelief(Predicate belief) {
		boolean result = root.add(belief);
		if (result) size++;
		return result;
	}

	public boolean removeBelief(Predicate belief) {
		boolean result = root.remove(belief);
		if (result) size--;
		return result;
	}

	public List<Formula> beliefs() {
		List<Formula> list = new LinkedList<Formula>();
		root.getBeliefs(list);
		return list;
	}

	public List<Formula> getMatchingBeliefs(Predicate formula) {
		return root.match(formula);
	}

	public int size() {
		return size;
	}
}

