package astra.lang;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import astra.cartago.CartagoAPI;
import astra.cartago.CartagoProperty;
import astra.cartago.CartagoPropertyEvent;
import astra.cartago.CartagoPropertyEventUnifier;
import astra.cartago.CartagoSignalEvent;
import astra.cartago.CartagoSignalEventUnifier;
import astra.core.Intention;
import astra.core.Module;
import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.CartagoPropertyStackEntryFactory;
import astra.reasoner.ResolutionBasedReasoner;
import astra.reasoner.Unifier;
import astra.reasoner.util.AbstractEvaluateVisitor;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.LogicVisitor;
import astra.reasoner.util.RenameVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.term.Funct;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.CartagoService;
import cartago.Op;

/**
 * This API provides additional support for the CArtAgO integration.
 * 
 * <p>
 * CArtAgO is an environment infrastructure. Support for it is provided
 * as a core part of ASTRA in terms of custom events, statements and
 * formulae. This API provides additional support for deplyoing and
 * configuring CArtAgO environments.
 * </p>
 * 
 * @author Rem Collier
 *
 */
public class Cartago extends Module {
	private CartagoAPI cartagoAPI;

	static {
		Unifier.eventFactory.put(CartagoPropertyEvent.class, new CartagoPropertyEventUnifier());
		Unifier.eventFactory.put(CartagoSignalEvent.class, new CartagoSignalEventUnifier());
		ResolutionBasedReasoner.register(CartagoProperty.class, new CartagoPropertyStackEntryFactory());
		AbstractEvaluateVisitor.addFormulaHandler(new AbstractEvaluateVisitor.Handler<CartagoProperty>() {
			@Override public Class<CartagoProperty> getType() { return CartagoProperty.class; }
			@Override public Object handle(LogicVisitor visitor, CartagoProperty property, boolean passByalue) {
				return new CartagoProperty((Predicate) property.content().accept(visitor));
			}
		});
		RenameVisitor.addFormulaHandler(new RenameVisitor.Handler<CartagoProperty>() {
			@Override public Class<CartagoProperty> getType() { return CartagoProperty.class; }
			@Override public Object handle(LogicVisitor visitor, CartagoProperty property, String modifier, Map<Integer, Term> bindings) {
				return new CartagoProperty((Predicate) property.content().accept(visitor));
			}
		});
		VariableVisitor.addFormulaHandler(new VariableVisitor.Handler<CartagoProperty>() {
			@Override public Class<CartagoProperty> getType() { return CartagoProperty.class; }
			@Override public Object handle(LogicVisitor visitor, CartagoProperty property, Set<Variable> variables) {
				property.content().accept(visitor);
				return null;
			}
		});
	}
	
	/**
	 * Action that starts a local CArtAgO node.
	 * 
	 * @return
	 */
	@ACTION
	public boolean startService() {
		try {
			CartagoService.startNode();
		} catch (CartagoException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Action that registers the agent with the local CArtAgO node.
	 * 
	 * @return
	 */
	@ACTION
	public boolean link() {
		cartagoAPI = CartagoAPI.create(agent);
		return true;
	}
	
	@EVENT( symbols={"+", "-"}, types = {"string", "funct" }, signature="$cpe" )
	public Event event(String symbol, Term id, Term args) {
		return new CartagoPropertyEvent(
				Primitive.newPrimitive(symbol), 
				id, 
				new Predicate(((Funct) args).functor(), ((Funct) args).terms())
		);
	}

	@EVENT( symbols={}, types = {"string", "funct" }, signature="$cse" )
	public Event signal(Term id, Term args) {
		return new CartagoSignalEvent(
				id, 
				new Predicate(((Funct) args).functor(), ((Funct) args).terms())
		);
	}

	/**
	 * Use of SUPPRESS_NOTIFICATIONS annotation to stop the interpreter from
	 * notifying itself on completion of the action. For CArtAgO, notification is
	 * done asynchronously when the operation actually completes...
	 * 
	 *  This is handled within the {@link astra.cartago.CartagoAPI} class.
	 *  
	 * @param context
	 * @param action
	 * @return
	 * @throws CartagoException 
	 */
	@SUPPRESS_NOTIFICATIONS
	public boolean auto_action(Intention context, Predicate action) {
		Predicate activity = action;
		Op op = null;
		
		// Last argument is the operation
		if (action.predicate().equals("operation")) {
			Funct funct = (Funct) action.termAt(action.size()-1);
			activity = (Predicate) new Predicate(funct.functor(), funct.terms());
		}

		ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context);
		activity = (Predicate) activity.accept(visitor);
		LinkedList<Object> list = cartagoAPI.getArguments(activity);
		op = list.isEmpty() ? new Op(activity.predicate()):new Op(activity.predicate(), list.toArray());
		
		
		try {
			context.suspend();
			if (action.predicate().equals("operation") && action.size() == 2) {
				Term term = (Term) action.termAt(0).accept(visitor);
				if (!Primitive.class.isInstance(term)) {
					throw new RuntimeException("Failed CArtAgO Operation: " + action);
				}
				// We have an artifact id...
				Object o = ((Primitive<?>) term).value();
				if (o instanceof ArtifactId) {
					cartagoAPI.registerOperation(
							cartagoAPI.getSession().doAction((ArtifactId) o, op, null, -1), 
							context, activity
					);
				} else if (o instanceof String) {
					cartagoAPI.registerOperation(
							cartagoAPI.getSession().doAction(o.toString(), op, null, -1), 
							context, activity
					);
				} else {
					throw new RuntimeException("Could not handle artifact id type: " + o.getClass().getName());
				}
			} else {
				cartagoAPI.registerOperation(
						cartagoAPI.getSession().doAction(op, null, -1), 
						context, activity
				);
			}
			return true;
		} catch (CartagoException e) {
			throw new RuntimeException(e);
		}
	}
	
	@FORMULA
	public Formula auto_formula(Predicate formula) {
		return new CartagoProperty(formula);
	}
	
	@FORMULA
	public Formula property(ArtifactId id, Funct formula) {
		return new CartagoProperty(id, new Predicate(formula.functor(), formula.terms()));
	}
}
