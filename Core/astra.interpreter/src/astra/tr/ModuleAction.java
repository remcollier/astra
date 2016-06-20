package astra.tr;

import java.util.Map;

import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.term.Term;



public class ModuleAction extends AbstractAction {
	String module;
	Predicate method;
	ModuleActionAdaptor adaptor;
	
	public ModuleAction(String module, String clazz, int[] data, Predicate method, ModuleActionAdaptor adaptor) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.module = module;
		this.method = method;
		this.adaptor = adaptor;
	}

	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {
			@Override
			public boolean execute(final TRContext context, Map<Integer, Term> bindings) {
				return adaptor.invoke(context, (Predicate) method.accept(new BindingsEvaluateVisitor(bindings, context.agent)));
			}
		};
	}
	
}
