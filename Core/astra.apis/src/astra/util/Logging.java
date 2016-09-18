package astra.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.core.Agent;
import astra.core.Module;
/**
 * This class provides a basic logger for ASTRA.
 * 
 * <p>
 * Logging is a key debugging technique. This class exposes the Java Logging
 * API to ASTRA.  One log is created per agent (the id of the log is the
 * agents name). and the standard suite of log levels is supported:
 * <ul>
 * <li>SEVERE</li>
 * <li>WARNING</li>
 * <li>OFF</li>
 * <li>ALL</li>
 * <li>FINE</li>
 * <li>FINER</li>
 * <li>FINEST</li>
 * <li>INFO</li>
 * </ul>
 * </p>
 * <p>
 * When using a log level, case does not matter.
 * </p>
 * @author Rem Collier
 *
 */
public class Logging extends Module {
	private Logger log;
	
	/**
	 * This is an internal (non-API) method that initialises the logger whose id is set to the agents name.
	 * @param agent the agent instance
	 */
	@Override
	public void setAgent(Agent agent) {
		super.setAgent(agent);
		log = Logger.getLogger(agent.name());
		log.setLevel(Level.ALL);
	}
	
	static Map<String, Level> levels = new HashMap<String, Level>();
	
	static {
		levels.put("SEVERE", Level.SEVERE);
		levels.put("WARNING", Level.WARNING);
		levels.put("OFF", Level.OFF);
		levels.put("ALL", Level.ALL);
		levels.put("FINE", Level.FINE);
		levels.put("FINER", Level.FINER);
		levels.put("FINEST", Level.FINEST);
		levels.put("INFO", Level.INFO);
	}
	
	/**
	 * Action to set the logging level.
	 * <p>
	 * The logging level is one of: SEVERE, WARNING, OFF, ALL, FINE, FINER, FINEST, INFO
	 * </p>
	 *  
	 * @param level the logging level
	 * @return
	 */
	@ACTION
	public boolean setLevel(String level) {
		Level lev = levels.get(level.toUpperCase());
		if (lev == null) {
			System.err.println("[" + agent.name() + "] Unknown log level: " + level);
			return false;
		}
		log.setLevel(lev);
		return true;
	}

	/**
	 * Action to record a log entry.
	 * 
	 * <p>
	 * Basic logging action. Combines the message with a log level
	 * </p>
	 * @param level the logging level
	 * @param event the message
	 * @return
	 */
	@ACTION
	public boolean log(String level, String event) {
		Level lev = levels.get(level.toUpperCase());
		if (lev == null) {
			System.err.println("[" + agent.name() + "] Unknown log level: " + level);
			return false;
		}
		log.log(lev, event);
		return true;
	}

	/**
	 * Action to record a SEVERE log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean severe(String event) {
		log.log(Level.SEVERE, "[" + agent.name() + "] Severe: " + event);
		return true;
	}

	/**
	 * Action to record a WARNING log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean warning(String event) {
		log.log(Level.WARNING, "[" + agent.name() + "] Warning: " + event);
		return true;
	}

	/**
	 * Action to record a INFO log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean info(String event) {
		log.log(Level.INFO, "[" + agent.name() + "] Info: " + event);
		return true;
	}

	/**
	 * Action to record a FINE log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean fine(String event) {
		log.log(Level.FINE, "[" + agent.name() + "] Info: " + event);
		return true;
	}
	
	/**
	 * Action to record a FINER log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean finer(String event) {
		log.log(Level.FINER, "[" + agent.name() + "] Info: " + event);
		return true;
	}

	/**
	 * Action to record a FINEST log event.
	 * 
	 * @param event the event
	 * @return
	 */
	@ACTION
	public boolean finest(String event) {
		log.log(Level.INFO, "[" + agent.name() + "] Info: " + event);
		return true;
	}
}
