package astra.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
/**
 * This class implements an in JVM tuple space.
 * 
 * <p>
 * Tuple spaces are shared spaces are an alternative (to message passing) mechanism for 
 * implementing coordination mechanisms in multi-agent systems. This class implements
 * an in JVM tuple space.
 * </p>
 * 
 * <p>
 * To use this class, you will need to import this class and add it as a module:
 * </p>
 * <p>
 * <code>
 * package ts;<br/>
 * <br/>
 * import astra.util.TupleSpace;<br/>
 * <br/>
 * agent Template {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;module TupleSpace ts;<br/>
 * }<br/>
 * </code>
 * </p>
 * <p>
 * 
 * @author Rem Collier
 *
 */
public class TupleSpace extends Module {
	private class Tuple {
		private List<String> participants = new LinkedList<String>();
		private Map<String, Object> tuples = new HashMap<String, Object>();
		
		public void join(String name) {
			participants.add(name);
		}
		
		public void leave(String name) {
			participants.remove(name);
		}
		
		public boolean isParticipant(String name) {
			return participants.contains(name);
		}
		
		public boolean putValue(String name, String key, Object value) {
			if (!isParticipant(name)) throw new UnsupportedOperationException("You are not in this tuple space: " + name);
			tuples.put(key, value);
			return true;
		}

		public Object getValue(String name, String key) {
			if (!isParticipant(name)) throw new UnsupportedOperationException("You are not in this tuple space: " + name);
			return tuples.get(key);
		}
		
		public String toString() {
			return participants + " / " + tuples;
		}

		public boolean hasParticipants() {
			return !participants.isEmpty();
		}
	}
	
	static Map<String, Tuple> spaces = new HashMap<String, Tuple>();
	static Map<String, List<String>> agents = new HashMap<String, List<String>>();
	
	/**
	 * Action that lets the agent join a tuple space
	 * 
	 * <p>
	 * This action should be used to connect an agent to a tuple space. If a tuple space
	 * with the given id exists, then the agent joins that space, alternatively, the space
	 * is first created and then the agent joins it.
	 * </p>
	 * 
	 * @param id the id of the tuple space that the agent is to join.
	 * @return
	 */
	@ACTION
	public synchronized boolean join(String id) {
		List<String> list = agents.get(agent.name());
		if (list == null) {
			agents.put(agent.name(), list = new LinkedList<String>());
		}
		if (list.contains(id)) {
			throw new UnsupportedOperationException("Agent: " + agent.name() + " has already joined space: " + id);
		}
		
		Tuple tuple = spaces.get(id);
		if (tuple == null) {
			spaces.put(id, tuple = new Tuple());
		}
		
		tuple.join(agent.name());
		list.add(id);
		return true;
	}
	
	/**
	 * Action for an agent to leave a tuple space
	 * <p>
	 * This action should be used when an agent wants to leave a tuple space. If the tuple space is
	 * empty after the agent has left, then the space is deleted.
	 * </p> 
	 * <p>
	 * This action fails if:
	 * <ol>
	 * <li>The space does not exist.
	 * <li>The agent is not in the space.
	 * </ol>
	 * </p>
	 * 
	 * @param id the id of the space.
	 * @return
	 */
	@ACTION
	public synchronized boolean leave(String id) {
		List<String> list = agents.get(agent.name());
		if (list == null) {
			throw new UnsupportedOperationException("Agent: " + agent.name() + " is not in any spaces");
		}
		
		if (!list.contains(id)) {
			throw new UnsupportedOperationException("Agent: " + agent.name() + " is not in space: " + id);
		}
		
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		tuple.leave(agent.name());
		list.remove(agent.name());
		
		if (!tuple.hasParticipants()) spaces.remove(id);
		return true;
	}
	
	/**
	 * Action to post an object to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the object will be stored under
	 * @param value the object
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, Object value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), id, value);
	}
	
	/**
	 * Action to post a string to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the string will be stored under
	 * @param value the string
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, String value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post an int to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the int will be stored under
	 * @param value the int
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, int value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post an long to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the long will be stored under
	 * @param value the long
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, long value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post an float bject to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the float will be stored under
	 * @param value the float
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, float value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post an double to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the double will be stored under
	 * @param value the double
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, double value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post a char to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the char will be stored under
	 * @param value the char
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, char value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}

	/**
	 * Action to post a boolean to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the boolean will be stored under
	 * @param value the boolean
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, boolean value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Action to post an list to the specified space.
	 * 
	 * <p>
	 * This action will fail if the space does not exist.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the list will be stored under
	 * @param value the list
	 * @return
	 */
	@ACTION
	public boolean post(String id, String key, ListTerm value) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return false;
		
		return tuple.putValue(agent.name(), key, value);
	}
	
	/**
	 * Term to retrieve a string from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public String getString(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (String) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve an int from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Integer getInt(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Integer) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve a long from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Long getLong(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Long) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve a float from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Float getFloat(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Float) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve a double from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Double getDouble(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Double) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve a list from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public ListTerm getList(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (ListTerm) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Term to retrieve an object from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Object getObject(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return tuple.getValue(agent.name(), key);
	}

	/**
	 * Term to retrieve a char from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public char getChar(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Character) tuple.getValue(agent.name(), key);
	}
	/**
	 * Term to retrieve a boolean from a space stored under a given key
	 * <p>
	 * This term fails if there is not tuple space with the given id.
	 * </p>
	 * 
	 * @param id the space id
	 * @param key the key that the value is stored under
	 * @return
	 */
	@TERM
	public Boolean getBoolean(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) throw new UnsupportedOperationException("There is no tuple space: " + id);
		return (Boolean) tuple.getValue(agent.name(), key);
	}
	
	/**
	 * Formula to check whether a space contains a key
	 *
	 * @param id the space id
	 * @param key the key
	 * @return
	 */
	@FORMULA
	public Formula hasTuple(String id, String key) {
		Tuple tuple = spaces.get(id);
		if (tuple == null) return Predicate.FALSE;
		return tuple.getValue(agent.name(), key) != null ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Formula to check whether a space exists
	 *
	 * @param id the space id
	 * @param key the key
	 * @return
	 */
	@FORMULA
	public Formula hasSpace(String id) {
		return spaces.get(id) != null ? Predicate.TRUE:Predicate.FALSE;
	}

	/**
	 * Return a list of the spaces that the agent has joined.
	 * 
	 * @return
	 */
	@TERM
	public ListTerm getSpaceList() {
		List<String> list = agents.get(agent.name());
		if (list == null) {
			return new ListTerm(new Term[] {});
		}
		
		Term[] terms = new Term[list.size()];
		for (int i=0; i<terms.length; i++) {
			terms[i] = Primitive.newPrimitive(list.get(i));
		}
		return new ListTerm(terms);
	}
}
