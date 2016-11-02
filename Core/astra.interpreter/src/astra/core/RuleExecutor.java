package astra.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import astra.event.Event;
import astra.statement.Block;
import astra.statement.StatementHandler;
import astra.term.Term;
import astra.term.Variable;

public class RuleExecutor implements Executor {
	private Event event;
	private Rule rule;
	private Map<Integer, Term> bindings;
	private Stack<StatementExecutor> executors = new Stack<StatementExecutor>();
	private Map<Integer, Term> unbound;
	
	public RuleExecutor(Event event, Rule rule, Map<Integer, Term> bindings) {
		this.event = event;
		this.rule = rule;
		this.bindings = bindings;
		addStatement(rule.statement.getStatementHandler());
		captureUnboundVariables();
	}

	private void captureUnboundVariables() {
		unbound = new HashMap<Integer, Term>();
		
		for (Entry<Integer, Term> entry : bindings.entrySet()) {
			if (entry.getValue() instanceof Variable) {
				Variable var = ((Variable) entry.getValue());
				unbound.put(var.id(), new Variable(var.type(), entry.getKey()));
			}
		}
	}
	
	public Map<Integer, Term> getUnboundBindings() {
		for (int id : unbound.keySet()) {
			unbound.put(id, bindings.get(((Variable) unbound.get(id)).id()));
		}
		return unbound;
	}

	public void updateRuleBindings(Map<Integer, Term> bindings) {
		int i=executors.size()-1;
		while (i >= 0) {
			StatementExecutor executor = executors.get(i--);
			if (executor.bindings() != null) updateBindings(executor.bindings(), bindings);
		}
		
		updateBindings(this.bindings, bindings);
	}

	private void updateBindings(Map<Integer, Term> bindings, Map<Integer, Term> bindings2) {
		for (Entry<Integer, Term> entry:bindings.entrySet()) {
			if (entry.getValue() == null) {
				bindings.put(entry.getKey(), bindings2.get(entry.getKey()));
			}
		}
	}

	public boolean execute(Intention intention) {
		if (!executors.peek().execute(intention)) {
			executors.pop();
		}

//		System.out.println(variableTrace());
//		System.out.println("-----------------------------------------------");
		return !executors.isEmpty();
	}

	public void addStatement(StatementHandler handler) {
		if (handler.statement() instanceof Block) {
			executors.push(new StatementExecutor(handler, new HashMap<Integer, Term>()));
		} else {
			executors.push(new StatementExecutor(handler));
		}
	}

	public void addStatement(StatementHandler handler, Map<Integer, Term> bindings) {
		executors.push(new StatementExecutor(handler, bindings));
	}

	public Event event() {
		return event;
	}


	public void addVariable(Variable variable) {
		addVariable(variable, null);
	}

	public void addVariable(Variable variable, Term term) {
		Map<Integer, Term> b = getTopBindings();
		if (b==null) b=bindings;
		b.put(variable.id(), term);
	}
	
	public void removeVariable(Variable variable) {
		Map<Integer, Term> b = getTopBindings();
		if (b==null) b=bindings;
		b.remove(variable.id());
	}

	private Map<Integer, Term> getTopBindings() {
		int i = executors.size()-1;
		while (i >= 0) {
			if (executors.get(i).bindings() != null) {
//				System.out.println("\ttop bindings: (" + i + ") = "+ executors.get(i).bindings());
				return executors.get(i).bindings();
			}
			i--;
		}
		return null;
	}
	
	public boolean updateVariable(Variable term, Term logic) {
		int i=executors.size()-1;
		
		while (i >= 0) {
			if (executors.get(i).bindings() != null)
				if (executors.get(i).updateVariable(term, logic)) return true;
			i--;
		}
		if (bindings.containsKey(term.id())) {
			bindings.put(term.id(), logic);
			return true;
		}
		return false;
	}


	public Term getValue(Variable term) {
		int i=executors.size()-1;
		while (i >= 0) {
			StatementExecutor executor = executors.get(i--);
			
			if((executor.bindings() != null) && executor.bindings().containsKey(term.id())) {
				return executor.bindings().get(term.id());
			}
		}
		
		return bindings.get(term.id());
	}


	public Map<Integer,Term> bindings() {
		return bindings;
	}


	public String toString() {
		return rule.toString();
	}

	public String variableTrace() {
		String out = "";
		int i=executors.size()-1;
		while (i >= 0) {
			StatementExecutor executor = executors.get(i--);
			if (executor.bindings() != null) out += "("+(i+1)+"). " + executor.bindings()+"\n";
		}
		
		out+= "(BASE). " + bindings+"\n";
		return out;
	}

	public StatementHandler getNextStatment() {
		return executors.peek().getStatement();
	}

	public boolean rollback(Intention intention) {
		while (!executors.isEmpty()) {
			StatementExecutor executor = executors.peek();
			if (executor.rollback(intention)) return true;
			executors.pop();
		}
		return false;
	}

	public void buildFailureTrace(Stack<StatementHandler> failureTrace) {
		for (StatementExecutor executor : executors) {
			executor.buildFailureTrace(failureTrace);
		}
	}
}
