package astra.statement;

import astra.core.Intention;
import astra.formula.Predicate;

public interface ModuleCallAdaptor {
	public boolean invoke(Intention context, Predicate atom);
}
