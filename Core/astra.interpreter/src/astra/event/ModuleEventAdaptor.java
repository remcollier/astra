package astra.event;

import astra.core.Agent;
import astra.formula.Predicate;

public interface ModuleEventAdaptor {
	public Event generate(Agent agent, Predicate atom);
}
