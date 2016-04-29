package astra.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import astra.term.Term;
import astra.term.Variable;

public class Utilities {
	public static Map<Integer, Term> merge(Map<Integer, Term> mapA, Map<Integer, Term> mapB) {
		Map<Integer, Term> map = new HashMap<Integer, Term>();
		map.putAll(mapA);
		map.putAll(mapB);
		return map;
	}
	
	public static Map<Integer, Term> filter(Map<Integer, Term> source, Set<Variable> variables) {
		Map<Integer, Term> map = new HashMap<Integer, Term>();
		for (Variable variable : variables) {
			Term term = source.get(variable.id());
			if (term != null) {
				while (term != null && term instanceof Variable) {
					term = source.get(((Variable) term).id());
				}
				if (term != null) map.put(variable.id(), term);
			}
		}
		return map;
	}
	
	public static Map<Integer, Term> mgu(Map<Integer, Term> source) {
		boolean changed = true;
		
		Map<Integer, Term> oldMap = source;
		Map<Integer, Term> map = null;
		while (changed) {
			changed = false;
			map = new HashMap<Integer, Term>();
			
			for (Entry<Integer, Term> entry : oldMap.entrySet()) {
				if (entry.getValue() instanceof Variable) {
					Term val = source.get(((Variable) entry.getValue()).id());
					map.put(entry.getKey(), val == null ? entry.getValue():val);
					if (val != null && !val.equals(entry.getValue())) {
						changed = true; 
					}
				} else {
					map.put(entry.getKey(), entry.getValue());
				}
				oldMap = map;
			}
		}
		return map;
	}
	
}
