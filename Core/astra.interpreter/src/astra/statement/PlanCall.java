package astra.statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import astra.core.Intention;
import astra.core.Plan;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.term.Term;
import astra.term.Variable;

public class PlanCall extends AbstractStatement {
	Predicate id;
	
	public PlanCall(Predicate id) {
		this.id = id;
	}
	
	public PlanCall(String clazz, int[] data, Predicate id) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.id = id;
	}
	
	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			Predicate call;
			Map<Integer, Term> bindings;
			Plan plan;
			int index = 0;
			Set<Integer> unbound = new HashSet<Integer>();
			Map<Integer, Variable> variableMap = new HashMap<Integer, Variable>();
			
			@Override
			public boolean execute(Intention intention) {
				switch (index) {
				case 0:
					plan = intention.getPlan(call=(Predicate) id.accept(new ContextEvaluateVisitor(intention)));
					if (plan == null) {
						intention.failed("No such plan: " + call, null);
						return false;
					}
					
					bindings = Unifier.unify(plan.id(), call, intention.agent);
					if (bindings == null) {
						intention.failed("Plan call: " + call + " does not match expected types: " + plan.id(), null);
						return false;
					}
					
					Map<Integer, Term> b = new HashMap<Integer, Term>();
					for (Entry<Integer, Term> entry : bindings.entrySet()) {
						if (entry.getValue() instanceof Variable) {
							unbound.add(entry.getKey());
							b.put(entry.getKey(), null);
							variableMap.put(entry.getKey(), (Variable) entry.getValue());
							if (!intention.hasVariable((Variable) entry.getValue())) {
								intention.addVariable((Variable) entry.getValue());
							}
						} else {
							b.put(entry.getKey(), entry.getValue());
						}
					}

					intention.addBindings(b);
					intention.addStatement(plan.statement.getStatementHandler());
					index = 1;
					return true;
				case 1:
					Map<Variable, Term> updates = new HashMap<Variable, Term>();
					
					for (Integer i : unbound) {
						updates.put(variableMap.get(i), intention.getVariableValue(i));
					}
					intention.removeBindings();
					for (Entry<Variable, Term> entry : updates.entrySet()) {
						intention.updateVariable(entry.getKey(), entry.getValue());
					}
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}
			
			@Override
			public Statement statement() {
				return PlanCall.this;
			}
			
			public String toString() {
				if (call == null) return id.toString();
				return call.toString();
			}
		};
	}
	

}
