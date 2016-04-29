package astra.formula;

import astra.util.BindingsEvaluateVisitor;
import astra.util.LogicVisitor;

public class ModuleFormula implements Formula {
	String module;
	Predicate predicate;
	ModuleFormulaAdaptor adaptor;
	
	public ModuleFormula(String module, Predicate predicate, ModuleFormulaAdaptor adaptor) {
		this.module = module;
		this.predicate = predicate;
		this.adaptor = adaptor;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Formula formula) {
		return false;
	}

	public String module() {
		return module;
	}
	
	public Predicate predicate() {
		return predicate;
	}
	
	public ModuleFormulaAdaptor adaptor() {
		return adaptor;
	}
	
	public String toString() {
		return module + "." + predicate;
	}
}
