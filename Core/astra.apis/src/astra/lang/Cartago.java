package astra.lang;

import astra.cartago.CartagoASTRAEvent;
import astra.cartago.CartagoEventUnifier;
import astra.core.Module;
import astra.event.Event;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.term.Funct;
import astra.term.Term;
import cartago.CartagoException;
import cartago.CartagoService;

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
	static {
		Unifier.eventFactory.put(CartagoASTRAEvent.class, new CartagoEventUnifier());
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
		agent.linkToCartago();
		return true;
	}
	
	@EVENT( types = {"string", "funct" }, signature="@cpe" )
	public Event added(Term id, Term args) {
		return new CartagoASTRAEvent(CartagoASTRAEvent.ADDED, id, new Predicate(((Funct) args).functor(), ((Funct) args).terms()));
	}

	@EVENT( types = {"funct" }, signature="@cpe" )
	public Event updated(Term args) {
		return new CartagoASTRAEvent(CartagoASTRAEvent.UPDATED, null, new Predicate(((Funct) args).functor(), ((Funct) args).terms()));
	}
}
