package astra.tr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.cartago.CartagoAPI;
import astra.core.AbstractTask;
import astra.core.Agent;
import astra.core.Module;
import astra.eis.EISAgent;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.term.Primitive;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;

public class TRContext {
	List<Predicate> trace = new LinkedList<Predicate>();
	Agent agent;
	int index;
	
	public TRContext(Agent agent, Predicate call) {
		this.agent = agent;
		trace.add(call);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Term term) {
		if (term instanceof Primitive<?>) {
			return (T) ((Primitive<?>) term).value();
		}
		return null;
	}

	public Module getModule(String classname, String key) {
		return agent.getModule(classname, key);
	}
	
	public boolean execute() {
		int index = 0;
		while (index < trace.size()) {
//			System.out.println("handling: " + trace.get(index));
			Function function = agent.getFunction(trace.get(index));
//			System.out.println("function: " + function);
			Map<Integer, Term> bindings = Unifier.unify(function.identifier, trace.get(index));
			if (bindings == null) {
				System.err.println("ERROR EXECUTING TR FUNCTION: " + trace.get(index));
				for (int i=trace.size()-1; i >= 0; i--) {
					System.err.println(trace.get(i));
				}
				return false;
			}

			// Loop through the function rules until one fires and then
			// exit the loop. If no rules fire, this is okay - we do a
			// skip operation
			for (TRRule rule : function.rules) {
				Map<Integer, Term> b = new HashMap<Integer, Term>();
				b.putAll(bindings);
				Formula cond = (Formula) rule.condition.accept(new BindingsEvaluateVisitor(b, agent));
				List<Map<Integer,Term>> result = agent.query(cond, b);
				if (result != null) {
					bindings.putAll(result.get(0));
					rule.execute(this, bindings);
					break;
				}
			}

			
			index++;
		}
		return true;
	}

	public void callFunction(Predicate call) {
		trace.add(call);
	}

	public void addBelief(Predicate belief) {
		agent.beliefs().addBelief(belief);
		
	}

	public void removeBelief(Predicate belief) {
		agent.beliefs().dropBelief(belief);
		
	}

	public EISAgent getEISAgent(String id) {
		if (id == null) {
			id = agent.defaultEnvironment();
		}
		if (agent.eisAgents().isEmpty()) {
			System.err.println("No connected environment");
			return null;
		}
		
		return agent.eisAgents().get(id);
	}

	public String name() {
		return agent.name();
	}

	public void schedule(AbstractTask task) {
		agent.schedule(task);
	}

	public void stopFunction(Predicate function) {
		agent.stopFunction(function);
	}

	public CartagoAPI getCartagoAPI() {
		return agent.getCartagoAPI();
	}
}
