package astra.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.core.ASTRAClass;
import astra.core.ASTRAClassNotFoundException;
import astra.core.Agent;
import astra.core.AgentCreationException;
import astra.core.Module;
import astra.core.ModuleException;
import astra.core.Scheduler;
import astra.execution.SchedulerStrategy;
import astra.formula.Formula;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.messaging.Utilities;
import astra.messaging.Utilities.PredicateState;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

/**
 * The System API contains a number of actions, terms and formulae that help you to 
 * manage the operation of the overall multi-agent system.
 */
public class System extends Module {
	private static class AgentEntry {
		String owner;
		Agent agent;
		
		public AgentEntry(String owner, Agent agent) {
			this.owner = owner;
			this.agent = agent;
		}
	}
	
	// Static map of agents on the platform.
	private static Map<String, AgentEntry> agents = new HashMap<String, AgentEntry>();
	
	/**
	 * Internal method that indicates that the actions in this module not not need 
	 * to be scheduled, but can be executed directly.
	 */
//	@Override
//	public boolean inline() {
//		return true;
//	}
	
	@ACTION
	public boolean setDebugging(boolean state) {
		agent.setDebugging(state);
		return true;
	}
	
	/**
	 * This internal method associates the agent with the module - this is overridden 
	 * to storea reference to the agent in the agents map
	 */
	@Override
	public void setAgent(Agent agent) {
		super.setAgent(agent);
		agents.put(agent.name(), new AgentEntry(null, agent));
	}
	
	/**
	 * Action that fails.
	 * 
	 * @return false
	 */
	@ACTION
	public boolean fail() {
		return false;
	}
	
	/**
	 * Action that allows the agent to terminate itself.
	 * 
	 * @return
	 */
	@ACTION
	public boolean terminate() {
		agent.terminate();
		agents.remove(agent.name());
		return true;
	}
	
	/**
	 * Action that allows the agent to create another agent.
	 * 
	 * @param name the name of the agent to be created
	 * @param clazz the class of the agent (an ASTRA class)
	 * 
	 * @return
	 * @throws AgentCreationException 
	 */
	@ACTION
	public boolean createAgent(String name, String clazz) {
		if (agents.containsKey(name)) {
			throw new ModuleException("[System Module] Error: An agent with this name already exists on this platform: " + name);
		}
		
		try {
			ASTRAClass cl = ASTRAClass.forName(clazz);
			
			final Agent agt = cl.newInstance(name);
			agents.put(name, new AgentEntry(agent.name(), agt));
			Scheduler.schedule(agt);
		} catch (AgentCreationException e) {
			throw new ModuleException(e);
		} catch (ASTRAClassNotFoundException e) {
			throw new ModuleException(e);
		}
		return true;
	}

	/**
	 * This method can be used to set the main goal of an agent. It has been added
	 * to support the basic debugger...
	 * @param name
	 * @param args
	 * @return
	 */
	@ACTION
	public boolean setMainGoal(String name, ListTerm args) {
		agents.get(name).agent.initialize(new Goal(new Predicate("main", new Term[] {args})));
		return true;
	}
	
	/**
	 * Action to terminate another agent (if it exists and the invoking agent 
	 * is the owner of that agent).
	 * 
	 * @param name the name of the agent to be terminated
	 * @return
	 */
	@ACTION
	public boolean terminateAgent(String name) {
		AgentEntry entry = agents.get(name);
		if (entry == null) {
			java.lang.System.err.println("[System Module] No such agent: " + name);
			return false;
		}
		
		// Only the owner (the creator) or the agent itself can call terminate
		if (agent.name().equals(entry.owner) || name.equals(agent.name())) {
			entry.agent.terminate();
			agents.remove(name);
			java.lang.System.out.println("[System Module] Agent '" + name + "' terminated");
		} else {
			java.lang.System.err.println("[System Module] Access Denied: Agent: " + agent.name() + " cannot terminate agent: " + name);
			return false;
		}
		return true;
	}

	@ACTION
	public boolean suspendAgent(String name) {
		AgentEntry entry = agents.get(name);
		entry.agent.state(Agent.INACTIVE);
		return true;
	}

	@ACTION
	public boolean resumeAgent(String name) {
		AgentEntry entry = agents.get(name);
		entry.agent.state(Agent.ACTIVE);
		Scheduler.schedule(entry.agent);
		return true;
	}
	
	/**
	 * Term that returns a list of all the agents on the platform.
	 * 
	 * @return an ASTRA list containing the names of all agents on the platform 
	 */
	@TERM 
	public ListTerm getAgents() {
		ListTerm term = new ListTerm();
		for (String name : agents.keySet()) {
			term.add(Primitive.newPrimitive(name));
		}
		return term;
	}
	
	/**
	 * Term that returns the type of the agent.
	 * 
	 * @return
	 */
	@TERM
	public String getType() {
		return agent.getASTRAClass().getCanonicalName();
	}
	

	/**
	 * Term that returns the type of the agent with the given name.
	 * 
	 * @param name
	 * @return
	 */
	@TERM
	public String getType(String name) {
		AgentEntry entry = agents.get(name);
		if (entry == null) {
			java.lang.System.out.println("[System Module] There is no agent with name: " + name);
			return "none";
		}
		return entry.agent.getASTRAClass().getCanonicalName();
	}
	
	/**
	 * Term that returns a list of all the agents on the platform
	 * 
	 * @return an ASTRA list containing the names of all agents on the platform 
	 */
	@TERM 
	public ListTerm getAgentsOfType(String type) {
		ListTerm term = new ListTerm();

		ASTRAClass cl;
		try {
			cl = ASTRAClass.forName(type);
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
			java.lang.System.err.println("[System Module] Error: Type: " + type + " is not an ASTRA Class");
			return term;
		}
		
		for (String name : agents.keySet()) {
			AgentEntry entry = agents.get(name);
			if (entry.agent.getASTRAClass().isSubclass(cl)) term.add(Primitive.newPrimitive(name));
		}
		return term;
	}

	/**
	 * Term that returns a list of all the agents on that are children of the invoking agent
	 * 
	 * @return an ASTRA list containing the names of all agents on the platform 
	 */
	@TERM 
	public ListTerm getChildren() {
		ListTerm term = new ListTerm();

		for (AgentEntry entry : agents.values()) {
			if (agent.name().equals(entry.owner)) term.add(Primitive.newPrimitive(entry.agent.name()));
		}
		return term;
	}

	/**
	 * Term that returns a list of all the agents on that are children of the invoking agent
	 * 
	 * @return an ASTRA list containing the names of all agents on the platform 
	 */
	@TERM 
	public String getOwner() {
		AgentEntry entry = agents.get(agent.name());
		if (entry.owner == null) return "none";
		return entry.owner;
	}

	/**
	 * Formula thet returns true if the given type is available on the agent platform
	 * 
	 * @param type
	 * @return
	 */
	@FORMULA
	public Formula hasType(String type) {
		try {
			ASTRAClass.forName(type);
		} catch (ASTRAClassNotFoundException e) {
			return Predicate.FALSE;
		}
		return Predicate.TRUE;
	}
	
	/**
	 * Formula that returns true if the agent has children (i.e. it has created other 
	 * agents), false otherwise.
	 * 
	 * @return
	 */
	@FORMULA
	public Formula hasChildren() {
		for(AgentEntry entry : agents.values()) {
			if (entry.owner.equals(agent.name())) return Predicate.TRUE;
		}
		return Predicate.FALSE;
	}
	
	/**
	 * Formula that returns true if the agent has an owner, false otherwise (this will 
	 * only return false for the "main" agent.
	 * 
	 * @return
	 */
	@FORMULA
	public Formula hasOwner() {
		return agents.get(agent.name()).owner != null ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Action that causes the platform to stop
	 * 
	 * @return
	 */
	@ACTION
	public boolean exit() {
		java.lang.System.exit(0);
		return true;
	}
	
	/**
	 * Action that causes the agent to sleep for a specified period of time
	 * 
	 * @param time the sleep time in milliseconds
	 * @return
	 */
	@ACTION(inline=false)
	public boolean sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Term that returns the name of the agent.
	 * 
	 * @return
	 */
	@TERM
	public String name() {
		return agent.name();
	}
	
	@ACTION
	public boolean setSchedulePoolSize(int size) {
		Scheduler.setThreadPoolSize(size);
		return true;
	}
	
	/**
	 * Action that allows you to set the scheduling strategy used by the agents...
	 * @param strategy
	 * @return
	 */
	@ACTION
	public boolean setSchedulingStrategy(String strategy) {
		try {
			Scheduler.setStrategy((SchedulerStrategy) Class.forName(strategy).newInstance());
			java.lang.System.out.println("Scheduling Strategy set to: "+strategy);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Term that returns the current time in millisecond (delegates to the 
	 * {@link java.lang.System} class)
	 * 
	 * @return
	 */
	@TERM
	public long currentTimeMillis() {
		return java.lang.System.currentTimeMillis();
	}

	/**
	 * Formula that returns true if the agent exists, false otherwise.
	 * 
	 * @param name the name of the agent being checked
	 * @return
	 */
	@FORMULA
	public Formula agentExists(String name) {
		return agents.containsKey(name) ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**************************************************************************************************************
	 * VERY BASIC STATE CAPTURE AND RECONSTRUCTION CODE
	 **************************************************************************************************************/
	
	/**
	 * Term that captures the current state of the specified agent through 
	 * serialisation of its beliefs and stores that state in an 
	 * {@link astra.lang.AgentState} object.
	 * 
	 * @param name the name of the agent whose state is to be captured.
	 * @return
	 */
	@TERM
	public AgentState deconstruct(String name) {
		Agent agt = Agent.getAgent(name);
		
		// Transform to basic form to ensure there is no issue with the internal string mappers
		List<PredicateState> l2 = new LinkedList<PredicateState>();
		for (Formula formula : agt.beliefs().beliefs()) {
			l2.add(Utilities.toPredicateState(formula));
		}
		
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baout);
			out.writeObject(l2);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new AgentState(name, agt.getASTRAClass().getCanonicalName(), baout.toByteArray());
	}

	/**
	 * Action that recreates an agent based on the given state, which is 
	 * passed in as an instance of the {@link astra.lang.AgentState} class.
	 * 
	 * @param state the state of the agent to be recreated.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ACTION
	public boolean reconstruct(AgentState state) {
		try {
			Agent agt = ASTRAClass.forName(state.type).newInstance(state.name);
			astra.lang.System.agents.put(state.name, new AgentEntry(agent.name(), agt));
			ByteArrayInputStream bain = new ByteArrayInputStream(state.beliefs);
			ObjectInputStream in = new ObjectInputStream(bain);
			
			for (PredicateState pstate : (List<PredicateState>) in.readObject()) {
				agt.beliefs().store().addBelief((Predicate) Utilities.fromPredicateState(pstate));
			}

			Scheduler.schedule(agt);
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (AgentCreationException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Term that extracts the name of the agent from an {@link astra.lang.AgentState}
	 * object.
	 * 
	 * @param state an agent state
	 * @return
	 */
	@TERM
	public String getNameFromState(AgentState state) {
		return state.name;
	}
	/**
	 * Sets the sleep time for the scheduler.
	 * @param time
	 * @return
	 */
	@ACTION
	public boolean setSleepTime(long time) {
		Scheduler.setSleepTime(time);
		return true;
	}
}
