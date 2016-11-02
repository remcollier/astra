package astra.lang;

import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;

/**
 * Basic Debugging Support Tools.
 * 
 * <p>
 * This class is highly experimental and is mainly used for debugging the interpreter
 * rather than debugging actual ASTRA code.  We have no idea whether these methods
 * will actually be any use.
 * </p>
 * 
 * @author Rem Collier
 *
 */
public class Debug extends Module {
	/**
	 * Internal method indicating that methods in this API do not need to be threaded.
	 */
	public boolean inline() {
		return true;
	}

	/**
	 * Action that dumps the agents beliefs to the console.
	 * 
	 * @return
	 */
	@ACTION
	public boolean dumpBeliefs() {
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		java.lang.System.out.println("BELIEF DUMP FOR: " + agent.name());
		for (Formula belief : agent.beliefs().beliefs()) {
			java.lang.System.out.println("\t" + belief);
		}
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}

	/**
	 * Action that dumps the agents beliefs that match the given 
	 * predicate to the console.
	 *  
	 * @param predicate
	 * @return
	 */
	@ACTION
	public boolean dumpBeliefsWithPredicate(String predicate) {
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		java.lang.System.out.println("BELIEF DUMP FOR: " + agent.name());
		for (Formula belief : agent.beliefs().beliefs()) {
			if (((Predicate) belief).predicate().equals(predicate)) java.lang.System.out.println("\t" + belief);
		}
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}

	/**
	 * Action that dumps a stack trace for the current intention to 
	 * the console.
	 * 
	 * @return
	 */
	@ACTION
	public boolean printStackTrace() {
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		java.lang.System.out.println("STACK TRACE FOR: " + agent.name());
//		java.lang.System.out.println(agent.intention());
		agent.intention().dumpStack();
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}

	/**
	 * Action that dumps a stack trace for the current intention to 
	 * the console.
	 * 
	 * @return
	 */
	@ACTION
	public boolean printEventQueue() {
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		java.lang.System.out.println("EVENT QUEUE FOR: " + agent.name());
		java.lang.System.out.println(agent.events());
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}
}
