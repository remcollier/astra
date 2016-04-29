package astra.formula;

import astra.util.BindingsEvaluateVisitor;

public abstract class ModuleFormulaAdaptor {
	public abstract Formula invoke(BindingsEvaluateVisitor visitor, Predicate atom);
	
}
