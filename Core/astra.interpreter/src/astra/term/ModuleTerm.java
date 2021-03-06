package astra.term;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.LogicVisitor;
import astra.type.Type;



public class ModuleTerm implements Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = 596832607031187737L;
	String module;
	Predicate method;
	ModuleTermAdaptor adaptor;
	Type type;
	
	public ModuleTerm(ModuleTermAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public ModuleTerm(String module, Type type, Predicate method, ModuleTermAdaptor adaptor) {
		this.module = module;
		this.type = type;
		this.method = method;
		this.adaptor = adaptor;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public Predicate method() {
		return method;
	}
	
	@Override
	public boolean matches(Term right) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object evaluate(Intention context) {
//		System.out.println("invoking...");
		return adaptor.invoke(context, (Predicate) method.accept(new ContextEvaluateVisitor(context)));
	}

	public Object evaluate(BindingsEvaluateVisitor visitor) {
		return adaptor.invoke(visitor, (Predicate) method.accept(visitor));
	}

	public boolean equals(Object object) {
		if (object instanceof ModuleTerm) {
			ModuleTerm term = (ModuleTerm) object;
			return term.module.equals(module) && term.method.equals(method);
		}
		return false;
	}

	public String toString() {
		return "Module Term: " + module + "."  + method;
	}

	@Override
	public String signature() {
		return null;
	}
	
	public ModuleTermAdaptor adaptor() {
		return adaptor;
	}

	public String module() {
		return module;
	}
	
	public ModuleTerm clone() {
		return this;
	}
}
