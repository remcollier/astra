package astra.core;

import java.util.Map;
import java.util.Stack;

import astra.debugger.Breakpoints;
import astra.statement.StatementHandler;
import astra.term.Term;
import astra.term.Variable;

public class StatementExecutor implements Executor {
	private Map<Integer, Term> bindings;
	private Stack<StatementHandler> handlers = new Stack<StatementHandler>();
	private boolean executed = false;
	
	public StatementExecutor(StatementHandler handler, Map<Integer, Term> bindings) {
		this.bindings = bindings;
		handlers.push(handler);
	}

	public StatementExecutor(StatementHandler handler) {
		this(handler, null);
	}

	public boolean execute(Intention intention) {
//		System.out.println("executing: " + handlers.peek());
//		if (!executed) {
			Breakpoints.getInstance().check(intention.agent, handlers.peek().statement());
//			executed = true;
//		}
		
		if (!handlers.peek().execute(intention)) {
			handlers.pop();
		}
		
		return !handlers.isEmpty();
	}

	public void addStatement(StatementHandler handler) {
		handlers.push(handler);
	}

	public Map<Integer, Term> bindings() {
		return bindings;
	}

	public boolean updateVariable(Variable term, Term logic) {
		if (bindings.containsKey(term.id())) {
//			System.out.println("updating: "+handlers.peek() +"/"+term+"="+logic);
			bindings.put(term.id(), logic);
			return true;
		}
		return false;
	}

	public StatementHandler getStatement() {
		return handlers.peek();
	}

	public boolean rollback(Intention intention) {
		while (!handlers.isEmpty()) {
			StatementHandler handler = handlers.peek();
			if (handler.onFail(intention)) return true;
			handlers.pop();
		}
		return false;
	}

	public void buildFailureTrace(Stack<StatementHandler> failureTrace) {
		for (StatementHandler handler : handlers) {
			failureTrace.push(handler);
		}
	}
}
