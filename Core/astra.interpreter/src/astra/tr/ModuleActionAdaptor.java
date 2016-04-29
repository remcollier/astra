package astra.tr;

import astra.formula.Predicate;

public interface ModuleActionAdaptor {
	public boolean invoke(TRContext context, Predicate predicate);
}
