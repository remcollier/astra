package astra.reasoner.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringMapper {
	private ArrayList<String> strings = new ArrayList<String>();
	private Map<String, Integer> stringMap = new HashMap<String, Integer>();

	public synchronized int toId(String predicate) {
		Integer id = stringMap.get(predicate);
		if (id == null) {
			id = strings.size();
			strings.add(predicate);
			stringMap.put(predicate, id);
		}
		return id;
	}
	
	public String fromId(int id) {
		return strings.get(id);
	}

}
