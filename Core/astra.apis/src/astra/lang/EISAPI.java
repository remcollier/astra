package astra.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import astra.core.Agent;
import astra.core.Module;
import astra.eis.EISAgent;
import astra.eis.EISEvent;
import astra.eis.EISFormula;
import astra.eis.EISService;
import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.ResolutionBasedReasoner;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import eis.exceptions.ActException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;

/**
 * This API implements support for deploying and managing EIS environments.
 * 
 * <p>
 * While ASTRA provides language level support for EIS events, formulae, and 
 * actions; this is only part of the picture. Support is also required for
 * deploying, configuring and managing EIS environments. This API provides 
 * that additional support.
 * </p>
 * <p>
 * To write and agent that can use an EIS environment, the following minimum 
 * number of steps is necessary:
 * <ol>
 * <li>Launch the environment</li>
 * <li>Join the environment</li>
 * <li>Link to an entity in the environment</li>
 * <li>Start the environment</li>
 * </ol>
 * </p>
 * <p>
 * This can be implemented using the following code:
 * </p>
 * <p>
 * <code>
 * module EISAPI eis;
 * 
 * rule +!main(list args) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;eis.launch("tower", "towerenv.jar");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;eis.join("tower");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;eis.link("gripper");<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;eis.startEnv();<br/>
 * }
 * </code>
 * </p>
 *   
 * @author Rem Collier
 *
 */
public class EISAPI extends Module {
	private ResolutionBasedReasoner reasoner;
	
	public void setAgent(Agent agent) {
		super.setAgent(agent);
		
		reasoner = new ResolutionBasedReasoner(agent);
	}
	
	/**
	 * Action that launches a new EIS environment from the specified url 
	 * with the given id and registers the agent with that environment.
	 * 
	 * @param id the id to be associated with that environment
	 * @param jar the url of the jar file containing the environment 
	 * @return
	 */
	@ACTION
	public boolean launch(String id, String jar) {
		EISService.newService(id, jar);
		return true;
	}

	/**
	 * Initialise the default EIS Environment with no parameters
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean init() {
		return init(agent.defaultEnvironment(), new ListTerm(), new ListTerm());
	}

	/**
	 * Initialise the EIS Environment identified by id with no parameters
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean init(String id) {
		return init(id, new ListTerm(), new ListTerm());
	}

	/**
	 * Initialise the default EIS Environment with the parameters given in 
	 * keys and values. Specifically, the first key is matched to the first
	 * value, the second key to the second value, and so on...
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean init(ListTerm keys, ListTerm values) {
		return init(agent.defaultEnvironment(), keys, values);
	}
	
	/**
	 * Initialise the EIS Environment identified by id with the parameters given
	 * in keys and values. Specifically, the first key is matched to the first
	 * value, the second key to the second value, and so on...
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ACTION
	public boolean init(String id, ListTerm keys, ListTerm values) {
		EISService service = EISService.getService(id);
		Map<String, Parameter> map = new HashMap<String,Parameter>();
		for (int i=0;i<keys.size(); i++) {
			Object obj = ((Primitive<?>) values.get(i)).value();
			if (obj instanceof Number) {
				map.put(((Primitive<String>) keys.get(i)).value(), new Numeral((Number) obj));
			} else {
				map.put(((Primitive<String>) keys.get(i)).value(), new Identifier(obj.toString()));
			}
		}
		service.init(map);
		return true;
	}
	

	/**
	 * Reset the default EIS Environment with no parameters
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean reset() {
		return reset(agent.defaultEnvironment(), new ListTerm(), new ListTerm());
	}

	/**
	 * Reset the EIS Environment identified by id with no parameters
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean reset(String id) {
		return reset(id, new ListTerm(), new ListTerm());
	}

	/**
	 * Reset the default EIS Environment with the parameters given in 
	 * keys and values. Specifically, the first key is matched to the first
	 * value, the second key to the second value, and so on...
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean reset(ListTerm keys, ListTerm values) {
		return reset(agent.defaultEnvironment(), keys, values);
	}
	
	/**
	 * Reset the EIS Environment identified by id with the parameters given
	 * in keys and values. Specifically, the first key is matched to the first
	 * value, the second key to the second value, and so on...
	 * 
	 * @pa
	 * ram id
	 * @param keys
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ACTION
	public boolean reset(String id, ListTerm keys, ListTerm values) {
		EISService service = EISService.getService(id);
		Map<String, Parameter> map = new HashMap<String,Parameter>();
		for (int i=0;i<keys.size(); i++) {
			Object obj = ((Primitive<?>) values.get(i)).value();
			if (obj instanceof Number) {
				map.put(((Primitive<String>) keys.get(i)).value(), new Numeral((Number) obj));
			} else {
				map.put(((Primitive<String>) keys.get(i)).value(), new Identifier(obj.toString()));
			}
		}
		service.init(map);
		return true;
	}
/**
	 * Action that registers an agent with an existing EIS environment 
	 * (if it exists) and sets it as the default environment. The action
	 * fails if the environment does not exist.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@ACTION
	public boolean join(String id) {
		EISService service = EISService.getService(id);
		if (service == null) return false;
		return agent.linkToEISService(id, service);
	}
	
	/**
	 * Action that links an agent to an entity in the default environment 
	 * with the same name as the agent. The action fails if the agent is 
	 * not registered with the default environment.
	 * 
	 * @return
	 */
	@ACTION
	public boolean link() {
		return link(agent.name());
	}

	/**
	 * Action that links an agent to a specified entity in the default 
	 * environment with the same name as the agent. The action fails if 
	 * the agent is not registered with the default environment.
	 * 
	 * @param entity the entity to be linked to
	 * @return
	 */
	@ACTION
	public boolean link(String entity) {
		return link(agent.defaultEnvironment(), entity);
	}
	
	/**
	 * Action that links an agent to an entity in the environment with 
	 * identifier, id, to the entity with the given name. The action 
	 * fails if the agent is not registered with the environment.
	 * 
	 * @param id the environment id
	 * @param entity the entity name
	 * @return
	 */
	@ACTION
	public boolean link(String id, String entity) {
		EISAgent agt = agent.eisAgents().get(id);
		if (agt == null) return false;
		return agt.associcateEntity(entity);
	}

	/**
	 * Action that starts the default environment if it is in a 
	 * paused state. The action fails if the agent is not registered
	 * with the environment.
	 * 
	 * @return
	 */
	@ACTION
	public boolean startEnv() {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) return false;
		service.eisStart();
		return true;
	}

	/**
	 * Action that starts the environment with identifier, id, 
	 * if it is in a paused state. The action fails if the agent 
	 * is not registered with the environment.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@ACTION
	public boolean startEnv(String id) {
		EISService.getService(id).eisStart();
		return true;
	}
	
	/**
	 * Action that sets the environment with the given id as 
	 * the default environment.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@ACTION
	public boolean setDefaultEnvionment(String id) {
		agent.defaultEnvironment(id);
		return true;
	}

	/**
	 * Term that returns the id of the current default environment
	 * 
	 * @return
	 */
	@TERM
	public String defaultEnvironment() {
		return agent.defaultEnvironment();
	}
	
	/**
	 * Term that returns a list of free entity names in the default environment.
	 * 
	 * @return
	 */
	@TERM
	public ListTerm freeEntities() {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) throw new RuntimeException("No EIS Service available.");
		ListTerm list = new ListTerm();
		for (String entity : service.getFreeEntities()) {
			list.add(Primitive.newPrimitive(entity));
		}
		return list;
	}
	
	/**
	 * Term that returns a list of free entity names in the specified environment.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@TERM
	public ListTerm freeEntities(String id) {
		EISService service = EISService.getService(id);
		if (service == null) throw new RuntimeException("No EIS Service: " + id);
		ListTerm list = new ListTerm();
		for (String entity : service.getFreeEntities()) {
			list.add(Primitive.newPrimitive(entity));
		}
		return list;
	}
	
	/**
	 * Term that returns a list of all entity names in the default environment.
	 * 
	 * @return
	 */
	@TERM
	public ListTerm allEntities() {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) throw new RuntimeException("No EIS Service available.");
		ListTerm list = new ListTerm();
		for (String entity : service.getAllEntities()) {
			list.add(Primitive.newPrimitive(entity));
		}
		return list;
	}
	
	/**
	 * Term that returns a list of all entity names in the specified environment.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@TERM
	public ListTerm allEntities(String id) {
		EISService service = EISService.getService(id);
		if (service == null) throw new RuntimeException("No EIS Service: " + id);
		ListTerm list = new ListTerm();
		for (String entity : service.getAllEntities()) {
			list.add(Primitive.newPrimitive(entity));
		}
		return list;
	}
	
	/**
	 * Term that returns the type of the specified entity in the default environment.
	 * 
	 * @param name the entity name
	 * @return
	 */
	@TERM
	public String getEntityType(String name) {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) throw new RuntimeException("No EIS Service available.");
		return service.queryEntityType(name);
	}
	
	/**
	 * Term that returns the type of the specified entity in the specified environment.
	 * 
	 * @param id the environment id
	 * @param name the entity name
	 * @return
	 */
	@TERM
	public String getEntityType(String id, String name) {
		EISService service = EISService.getService(id);
		if (service == null) throw new RuntimeException("No EIS Service: " + id);
		return service.queryEntityType(name);
	}
	
	/**
	 * Term that returns the state of the default environment.
	 * 
	 * @return
	 */
	@TERM
	public String getState() {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) throw new RuntimeException("No EIS Service available.");
		return service.getEnvironmentState();
	}
	
	/**
	 * Term that returns the state of the specified environment.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@TERM
	public String getState(String id) {
		return EISService.getService(id).getEnvironmentState();
	}
	
	/**
	 * Formula that returns true if the specified environment exists, false otherwise.
	 * 
	 * @param id the environment id
	 * @return
	 */
	@FORMULA
	public Formula environmentExists(String id) {
		return EISService.getService(id) != null ? Predicate.TRUE:Predicate.FALSE;
	}
	
	@ACTION
	public boolean perform(String id, String entity, String action, ListTerm terms) {
		EISService service = EISService.getService(id);
		if (service == null) throw new RuntimeException("No EIS Service available.");
		EISAgent agt = service.get(agent.name());
		try {
			agt.invoke(entity, new Predicate(action, terms.toArray(new Term[terms.size()])));
		} catch (ActException e) {
			e.printStackTrace();
			return false;
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@ACTION
	public boolean perform(String entity, String action, ListTerm terms) {
		return this.perform(agent.defaultEnvironment(), entity, action, terms);
	}
	
	@ACTION
	public boolean perform(String action, ListTerm terms) {
		EISService service = EISService.getService(agent.defaultEnvironment());
		if (service == null) throw new RuntimeException("No EIS Service available.");
		EISAgent agt = service.get(agent.name());
		try {
			agt.invoke(agt.defaultEntity(), new Predicate(action, terms.toArray(new Term[terms.size()])));
		} catch (ActException e) {
			e.printStackTrace();
			return false;
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@ACTION
	public boolean perform(String action) {
		return perform(action, new ListTerm());
	}

	@ACTION
	public boolean perform(String entity, String action) {
		return perform(entity, action, new ListTerm());
	}

	@ACTION
	public boolean perform(String id, String entity, String action) {
		return perform(id, entity, action, new ListTerm());
	}

	@ACTION
	public boolean perform(Funct action) {
		return perform(action.functor(), new ListTerm(action.terms()));
	}
	
	@FORMULA
	public Formula check(String id, String entity, String predicate, ListTerm terms) {
		return new EISFormula(Primitive.newPrimitive(id), Primitive.newPrimitive(entity), new Predicate(predicate, terms.toArray(new Term[terms.size()])));
	}

	@FORMULA
	public Formula check(String id,String predicate, ListTerm terms) {
		return check(id, agent.defaultEISAgent().defaultEntity(), predicate, terms);
	}

	@FORMULA
	public Formula check(String predicate, ListTerm terms) {
		return check(agent.defaultEnvironment(), agent.defaultEISAgent().defaultEntity(), predicate, terms);
	}

	@FORMULA
	public Formula check(String id, String entity, Funct function) {
		return new EISFormula(Primitive.newPrimitive(id), Primitive.newPrimitive(entity), new Predicate(function.functor(), function.terms()));
	}

	@FORMULA
	public Formula check(String id, Funct function) {
		return check(id, agent.defaultEISAgent().defaultEntity(), function);
	}

	@FORMULA
	public Formula check(Funct function) {
		return check(agent.defaultEnvironment(), agent.defaultEISAgent().defaultEntity(), function);
	}

	@EVENT( types = {"string", "string", "funct"}, signature="+@eis:" )
	public Event add(Term id, Term entity, Term term) {
		return new EISEvent(
				EISEvent.ADDITION,
				id,
				entity,
				new Predicate(
					((Funct) term).functor(), 
					((Funct) term).terms()
				)
		);
	}

	@EVENT( types = {"string", "string", "funct"}, signature="+@eis:" )
	public Event remove(Term id, Term entity, Term term) {
		return new EISEvent(
				EISEvent.REMOVED,
				id,
				entity,
				new Predicate(
					((Funct) term).functor(), 
					((Funct) term).terms()
				)
		);
	}
	
	@EVENT( types = {"string", "funct"}, signature="+@eis:" )
	public Event environment(Term id, Term term) {
		return new EISEvent(
				EISEvent.ENVIRONMENT,
				id,
				new Predicate(
					((Funct) term).functor(), 
					((Funct) term).terms()
				)
		);
	}
	
	@SENSOR
	public void sense() {
		for(EISAgent agt : agent.eisAgents().values()) {
			agt.sense();
		}
	}

	/**
	 * Action that dumps EIS beliefs to the console.
	 * 
	 * @return
	 */
	@ACTION
	public boolean dumpState() {
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		java.lang.System.out.println("EIS BELIEF DUMP FOR: " + agent.name());
		if (agent.eisAgents().isEmpty()) {
			java.lang.System.out.println("No connected EIS Service");
		} else {
			for (Entry<String, EISAgent> entry : agent.eisAgents().entrySet()) {
				java.lang.System.out.println("Environment: " + entry.getKey());
				entry.getValue().dumpBeliefs();
			}
		}
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}
}
