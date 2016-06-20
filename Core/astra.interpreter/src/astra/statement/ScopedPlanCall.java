package astra.statement;

import java.util.Map;

import astra.core.Intention;
import astra.core.Plan;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Term;

public class ScopedPlanCall extends AbstractStatement {
	String scope;
	Predicate id;
	
	public ScopedPlanCall(Predicate id, String scope) {
		this.scope = scope;
		this.id = id;
	}
	
	public ScopedPlanCall(String clazz, int[] data, String scope, Predicate id) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.scope = scope;
		this.id = id;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			Predicate call;
			
			int index = 0;
			@Override
			public boolean execute(Intention intention) {
				switch (index) {
				case 0:
					Plan plan = intention.getPlan(scope, call=(Predicate) id.accept(new ContextEvaluateVisitor(intention)));
					if (plan == null) {
						intention.failed("No such plan: " + call, null);
						return false;
					}
					Map<Integer, Term> bindings = Unifier.unify(plan.id(), call);
					if (bindings == null) {
						intention.failed("Plan call: " + call + " does not match expected types: " + plan.id(), null);
						return false;
					}
					intention.addStatement(plan.statement.getStatementHandler(), bindings);
					index = 1;
					return true;
				case 1:
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}
			
			@Override
			public Statement statement() {
				return ScopedPlanCall.this;
			}
			
			public String toString() {
				if (call == null) return id.toString();
				return call.toString();
			}
		};
	}
	

}
