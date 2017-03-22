package astra.eis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import astra.core.Agent;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.Queryable;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import eis.exceptions.ActException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import eis.iilang.TruthValue;

public class EISAgent implements Queryable {
	private static final List<Formula> EMPTY_PERCEPT_LIST = new LinkedList<Formula>();
	
	Agent agent;
	EISService service;
	String defaultEntity;
	Map<String, EISBeliefBase> beliefSets = new HashMap<String, EISBeliefBase>();
	
	private Object lastAction;
	
	public String toString() {
		return service.id() + "->" + agent.name();
	}
	
	public class EISBeliefBase {
		Primitive<String> pid;
		
		List<Percept> incoming = new LinkedList<Percept>();
		Map<String, List<Percept>> current = new HashMap<String, List<Percept>>();
		Map<String, List<Percept>> next = new HashMap<String, List<Percept>>();
		Map<String, List<Formula>> cache = new HashMap<String, List<Formula>>();
		
		public EISBeliefBase(String id) {
			pid = Primitive.newPrimitive(id);
		}
		
		public synchronized List<Formula> getMatchingFormulae(Predicate predicate) {
			List<Formula> list = cache.get(predicate.predicate());
			if (list == null) {
				list = new LinkedList<Formula>();
				List<Percept> percepts = current.get(predicate.predicate());
				if (percepts != null) {
					for (Percept percept : percepts) {
						list.add(convertToPredicate(percept));
					}
				}
				cache.put(predicate.predicate(), list);
			}
			return list;
		}

		public synchronized void update(Collection<Percept> percepts) {
			// Hack because EIS has been customised for Goal...
			if (percepts.isEmpty()) return;
			
			incoming.addAll(percepts);
			
			// Step 1: Process incoming percepts
			for (Percept percept : incoming) {
				// The following can happen if an action returns no percepts...
				if (percept == null) continue;
				
				// Step 1a: Add the percept to the new percepts...
				List<Percept> list = next.get(percept.getName());
				if (list == null) {
					next.put(percept.getName(), list = new LinkedList<Percept>());
				}
				list.add(percept);
//				System.out.println("[" + agent.name()+"] adding: " + percept.toProlog());
				
				// Step 1b: Check if it was in the old percepts
				list = current.get(percept.getName());
				if (list == null) {
					if (agent != null) agent.addEvent(new EISEvent(EISEvent.ADDED, pid, convertToFunct(percept)));
				} else {
					if (!list.remove(percept)) {
						if (agent != null) agent.addEvent(new EISEvent(EISEvent.ADDED, pid, convertToFunct(percept)));
					}
				}
			}
			
			// Step 2: identify what it left and generate removed EIS percepts events
			for (Entry<String,List<Percept>> entry : current.entrySet()) {
				while (!entry.getValue().isEmpty()) {
					agent.addEvent(new EISEvent(EISEvent.REMOVED, pid, convertToFunct(entry.getValue().remove(0))));
				}
			}
			
			// Step 3: Now set the completed perceptions to be the current ones...
			current = next;
			next = new HashMap<String, List<Percept>>();
			incoming.clear();
			cache.clear();
		}

		public synchronized void incoming(Collection<Percept> values) {
			incoming.addAll(values);
		}

		public synchronized void dumpBeliefs() {
			for(Entry<String, List<Percept>> entry : current.entrySet()) {
				for (Percept percept : entry.getValue()) {
					System.err.println(percept.toProlog());
				}
			}
		}

		public List<String> beliefStrings() {
			LinkedList<String> list = new LinkedList<String>();
			for(Entry<String, List<Percept>> entry : current.entrySet()) {
				for(Percept percept : entry.getValue()) {
					list.add(percept.toProlog());
				}
			}
			return list;
		}
	}

	public EISAgent(Agent agent, EISService service) {
		this.agent = agent;
		this.service = service;
		service.registerAgent(this);
	}
	
	public String name() {
		return agent.name();
	}
	
	/**
	 * Get the next set of perceptions from EIS and update the entity belief bases...
	 */
	public void sense() {
		try {
			Map<String, Collection<Percept>> percepts = service.collectBeliefs(this);
			
			for (Entry<String, Collection<Percept>> entry : percepts.entrySet()) {
				EISBeliefBase base = beliefSets.get(entry.getKey());
				if (base == null) {
					beliefSets.put(entry.getKey(), base = new EISBeliefBase(entry.getKey()));
				}
				base.update(entry.getValue());
			}
		} catch (Throwable th) {
			System.out.println("Error Sensing...");
			th.printStackTrace();
		}
	}
	
	private Funct convertToFunct( Percept percept ) {
        if ( percept == null ) return null;
        List<Parameter> parameters = percept.getParameters();
        Term[] terms = new Term[ parameters.size() ];
        for ( int i = 0; i < parameters.size(); i++ ) {
        	terms[ i ] = convertParameter(parameters.get( i ));
        }

        return new Funct( percept.getName(), terms );
    }

	private Predicate convertToPredicate( Percept percept ) {
        if ( percept == null ) return null;
        List<Parameter> parameters = percept.getParameters();
        Term[] terms = new Term[ parameters.size() ];
        for ( int i = 0; i < parameters.size(); i++ ) {
        	terms[ i ] = convertParameter(parameters.get( i ));
        }

        return new Predicate( percept.getName(), terms );
    }

	/**
	 * Recursively construct a parameter (recursion comes where the parameter
	 * is a list)
	 * @param parameter
	 * @return
	 */
	private Term convertParameter(Parameter parameter) {
        if ( Numeral.class.isInstance( parameter ) ) {
            return Primitive.newPrimitive( ( (Numeral) parameter ).getValue() );
        }
        else if (ParameterList.class.isInstance( parameter )) {
        	ListTerm list = new ListTerm();
        	Iterator<Parameter> it = ((ParameterList) parameter).iterator();
        	while (it.hasNext()) {
        		list.add(convertParameter(it.next()));
        	}
        	return list;
        }
        else if (Function.class.isInstance( parameter )) {
        	Function f = (Function) parameter;
            Term[] terms = new Term[ f.getParameters().size() ];
            for ( int i = 0; i < f.getParameters().size(); i++ ) {
            	terms[ i ] = convertParameter(f.getParameters().get( i ));
            }

            return new Funct( f.getName(), terms );
        }
        else if (TruthValue.class.isInstance( parameter )) {
            return Primitive.newPrimitive( ( (TruthValue) parameter ).getValue() );
        }
        else {
            return Primitive.newPrimitive( ( (Identifier) parameter ).getValue() );
        }
	}
	
	public void invoke(String entity, Predicate predicate) throws ActException, NoEnvironmentException {
		String actionId = null;
		LinkedList<Parameter> list = new LinkedList<Parameter>();

		actionId = predicate.predicate();
		for (Term t : predicate.terms()) {
			Object term = ((Primitive<?>) t).value();
			if (Number.class.isInstance(term)) {
				list.add(new Numeral((Number) term));
			} else {
				list.add(new Identifier(term.toString()));
			}
		}
		
		if (!predicate.equals(lastAction)) {
			lastAction = predicate;
		}

		// Invoke the action using EIS
		Map<String, Percept> percepts = service.performAction(agent.name(), entity, new eis.iilang.Action(actionId, list));

		beliefSets.get(entity).incoming(percepts.values());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<Formula> getMatchingFormulae(Formula formula) {
		if (formula instanceof EISFormula) {
			EISFormula f = (EISFormula) formula;
			
			// Step 1: Check if this EIS Agent is for the specified service...
			if (f.id() != null && !((Primitive<String>) f.id()).value().equals(service.id())) return EMPTY_PERCEPT_LIST;
			
			String entity = defaultEntity;
			if (f.entity() != null) {
				entity = ((Primitive<String>) f.entity()).value();
			}
			
			if (beliefSets.get(entity) == null) return EMPTY_PERCEPT_LIST;
			return beliefSets.get(entity).getMatchingFormulae(f.predicate());
		}
		return EMPTY_PERCEPT_LIST;
	}

	public synchronized void dumpBeliefs() {
		for(Entry<String, EISBeliefBase> entry : beliefSets.entrySet()) {
			System.err.println("=============================================== Start of Belief Set for Entity: " + entry.getKey());
			entry.getValue().dumpBeliefs();
			System.err.println("=============================================== End of Belief Set for Entity: " + entry.getKey());
		}
	}
	
	public void addEvent(EISEvent event) {
		agent.addEvent(event);
	}

	public boolean associcateEntity(String entity) {
		boolean result = service.associateEntity(agent.name(), entity);
		if (result) defaultEntity = entity;
		return result;
	}
	
	public String defaultEntity() {
		return defaultEntity;
	}
	
	public boolean defaultEntity(String entity) {
		if (service.hasAssociatedEntity(agent.name(), entity)) {
			defaultEntity = entity;
			return true;
		}
		return false;
	}

	public Map<String, EISBeliefBase> beliefs() {
		return beliefSets;
	}
}
