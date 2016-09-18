package astra.lang;

import java.io.Serializable;

/**
 * This is not an API - it is support class that us used by the 
 * {@link astra.lang.System} API.
 * 
 * @author Rem Collier
 *
 */
public class AgentState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8494110376318693019L;
	
	public String name;
	public String type;
	public byte[] beliefs;
	
	/**
	 * A container to hold the state of an agent (this is not used in
	 * execution, but can be used to store, retrieve, or transfer the
	 * agents state).
	 * 
	 * @param name
	 * @param type
	 * @param beliefs
	 */
	public AgentState(String name, String type, byte[] beliefs) {
		this.name = name;
		this.type = type;
		this.beliefs = beliefs;
	}
}
