package astra.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import astra.core.Intention;
import astra.core.Module;
import astra.eis.EISAgent;
import astra.eis.EISEvent;
import astra.eis.EISFormula;
import astra.eis.EISService;
import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.EISFormulaStackEntryFactory;
import astra.reasoner.ResolutionBasedReasoner;
import astra.reasoner.Unifier;
import astra.reasoner.unifier.EISEventUnifier;
import astra.reasoner.util.AbstractEvaluateVisitor;
import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.RenameVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;
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
public class EIS extends Module {
	static {
		ResolutionBasedReasoner.register(EISFormula.class, new EISFormulaStackEntryFactory());
		Unifier.eventFactory.put(EISEvent.class, new EISEventUnifier());
		AbstractEvaluateVisitor.addFormulaHandler(new AbstractEvaluateVisitor.Handler<EISFormula>() {
			@Override public Class<EISFormula> getType() { return EISFormula.class; }
			@Override public Object handle(LogicVisitor visitor, EISFormula formula, boolean passByalue) {
				if (formula.id() != null) {
					return new EISFormula((Term) formula.id().accept(visitor), (Term) formula.entity().accept(visitor), (Predicate) formula.predicate().accept(visitor));
				} else if (formula.entity() != null) {
					return new EISFormula((Term) formula.entity().accept(visitor), (Predicate) formula.predicate().accept(visitor));
				}
				return new EISFormula((Predicate) formula.predicate().accept(visitor));
			}
		});
		RenameVisitor.addFormulaHandler(new RenameVisitor.Handler<EISFormula>() {
			@Override public Class<EISFormula> getType() { return EISFormula.class; }
			@Override public Object handle(LogicVisitor visitor, EISFormula eisFormula, String modifier, Map<Integer, Term> bindings) {
				if (eisFormula.id() != null) {
					return new EISFormula((Term) eisFormula.id().accept(visitor), (Term) eisFormula.entity().accept(visitor), (Predicate) eisFormula.predicate().accept(visitor));
				} else if (eisFormula.entity() != null) {
					return new EISFormula((Term) eisFormula.entity().accept(visitor), (Predicate) eisFormula.predicate().accept(visitor));
				}
				return new EISFormula((Predicate) eisFormula.predicate().accept(visitor));
			}
		});
		VariableVisitor.addFormulaHandler(new VariableVisitor.Handler<EISFormula>() {
			@Override public Class<EISFormula> getType() { return EISFormula.class; }
			@Override public Object handle(LogicVisitor visitor, EISFormula formula, Set<Variable> variables) {
				if (formula.id() != null) formula.id().accept(visitor);
				formula.predicate().accept(visitor);
				return null;
			}
		});
	}

	private EISService service;
	private String defaultEntity;
	
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
		// If the module has been linked to an environment, don't allow another 
		// environment to be started...
		if (service != null) return false;
		service = EISService.newService(id, jar);
		return true;
	}

	/**
	 * Initialise the EIS Environment with no parameters
	 * 
	 * @param id
	 * @param keys
	 * @param values
	 * @return
	 */
	@ACTION
	public boolean init() {
		return init(new ListTerm());
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
	public boolean init(ListTerm parameters) {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		Map<String, Parameter> map = new HashMap<String,Parameter>();
		for (Term term : parameters) {
			Funct f = (Funct) term;
			Object obj = ((Primitive<?>) f.termAt(0)).value();
			if (obj instanceof Number) {
				map.put(f.functor(), new Numeral((Number) obj));
			} else {
				map.put(f.functor(), new Identifier(obj.toString()));
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
		return reset(new ListTerm(), new ListTerm());
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
	@SuppressWarnings("unchecked")
	@ACTION
	public boolean reset(ListTerm keys, ListTerm values) {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
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
	public boolean join() {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		agent.addSource(new EISAgent(agent, service));
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
		if (!service.id().equals(id)) {
			if (service != null) throw new RuntimeException("EIS Service connected.");
			
			service = EISService.getService(id);
			if (service == null) return false;
		}
		
		agent.addSource(new EISAgent(agent, service));
		return true;
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
	 * Action that links an agent to an entity in the environment with 
	 * identifier, id, to the entity with the given name. The action 
	 * fails if the agent is not registered with the environment.
	 * 
	 * @param id the environment id
	 * @param entity the entity name
	 * @return
	 */
	@ACTION
	public boolean link(String entity) {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		EISAgent agt = service.get(agent.name());
		
		if (agt == null) throw new RuntimeException("No EIS Agent available.");;
		defaultEntity = entity;
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
		if (service == null) throw new RuntimeException("No EIS Service available.");
		service.eisStart();
		return true;
	}

	/**
	 * Term that returns a list of free entity names in the default environment.
	 * 
	 * @return
	 */
	@TERM
	public ListTerm freeEntities() {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
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
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
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
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		return service.queryEntityType(name);
	}
	
	/**
	 * Term that returns the state of the default environment.
	 * 
	 * @return
	 */
	@TERM
	public String getState() {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		return service.getEnvironmentState();
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
	public boolean perform(String entity, String action, ListTerm terms) {
		if (service == null) throw new RuntimeException("No EIS Service available.");
		
		EISAgent agt = service.get(agent.name());
		try {
			agt.invoke(entity == null ? agt.defaultEntity():entity, new Predicate(action, terms.toArray(new Term[terms.size()])));
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
	public boolean perform(String action, ListTerm terms) {
		return perform(null, action, terms);
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
	public boolean perform(Funct action) {
		return perform(action.functor(), new ListTerm(action.terms()));
	}
	
	@ACTION
	public boolean perform(String entity, Funct action) {
		return perform(entity, action.functor(), new ListTerm(action.terms()));
	}
	
	@FORMULA
	public Formula check(String entity, String predicate, ListTerm terms) {
		return new EISFormula(Primitive.newPrimitive(entity), new Predicate(predicate, terms.toArray(new Term[terms.size()])));
	}

	@FORMULA
	public Formula check(String predicate, ListTerm terms) {
		return check(service.get(agent.name()).defaultEntity(), predicate, terms);
	}

	@FORMULA
	public Formula check(String entity, Funct function) {
		return new EISFormula(Primitive.newPrimitive(entity), new Predicate(function.functor(), function.terms()));
	}

	@FORMULA
	public Formula check(Funct function) {
		return check(service.get(agent.name()).defaultEntity(), function);
	}

	@EVENT( symbols = {"+","-"}, types = {"string", "funct"}, signature="$eis" )
	public Event event(String symbol, Term entity, Term term) {
		return new EISEvent(
				symbol.charAt(0),
				entity,
				term
		);
	}
	
	@EVENT( symbols = {"+","-"}, types = {"funct"}, signature="$eis" )
	public Event event(String symbol, Term term) {
		return new EISEvent(
				symbol.charAt(0),
				new Variable(Type.STRING, "_"),
				term
		);
	}
	
	@EVENT( symbols={}, types = {"funct"}, signature="$eis" )
	public Event environment(Term term) {
		return new EISEvent(
				EISEvent.ENVIRONMENT,
				term
		);
	}
	
	@SENSOR
	public void sense() {
		if (service == null) return;
		if (service.get(agent.name()) == null) return;
		service.get(agent.name()).sense();
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
		if (service == null) {
			java.lang.System.out.println("No connected EIS Service");
		} else {
			java.lang.System.out.println("Environment: " + service.id());
			service.get(agent.name()).dumpBeliefs();
		}
		java.lang.System.out.println("----------------------------------------------------------------------------------------------");
		return true;
	}
	
	public boolean auto_action(Intention context, Predicate action) {
		return perform(new Funct(action.predicate(), action.terms()));
	}
	
	public Formula auto_formula(Predicate formula) {
		return check(service.get(agent.name()).defaultEntity(), new Funct(formula.predicate(), formula.terms()));
	}
}
