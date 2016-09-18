package astra.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import astra.acre.AcreAPI;
import astra.cartago.CartagoAPI;
import astra.eis.EISAgent;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.event.ScopedGoalEvent;
import astra.formula.Formula;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.statement.Block;
import astra.statement.PlanCall;
import astra.statement.StatementHandler;
import astra.statement.Subgoal;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;

public class Intention {
	class Entry<T> {
		public Entry(int index, T value, boolean finish) {
			this.index = index;
			this.value = value;
			this.finish = finish;
		}
		
		T value;
		int index;
		boolean finish;
		
		public String toString() {
			return index + ": " + value + " {" + finish + "}";
		}
	}
	
	@SuppressWarnings("rawtypes")
	Map<Variable, ActionParam> actionParams = new HashMap<Variable, ActionParam>();
	Stack<StatementHandler> failureTrace;
	String failureReason;

	Stack<StatementHandler> statements = new Stack<StatementHandler>();
	Stack<Entry<Map<Integer, Term>>> bindingStack = new Stack<Entry<Map<Integer, Term>>>();
	Stack<Entry<Goal>> goalStack = new Stack<Entry<Goal>>();
	Map<Goal, Map<Variable, Integer>> subgoalVariables = new HashMap<Goal, Map<Variable, Integer>>();
	private Throwable exception;
	public Agent agent;
	Event event;
	boolean suspended = false;
	boolean failed = false;
	int age = 0;
	
	public Intention(Agent agent, Event event, Rule rule, Map<Integer, Term> bindings) {
		this.agent = agent;
		this.event = event;
		statements.push(rule.statement.getStatementHandler());
		bindingStack.push(new Entry<Map<Integer, Term>>(0, bindings, true));
	}
	
	public synchronized boolean execute() {
		age++;
		StatementHandler handler = null;
		try {
			handler = statements.peek();
		} catch (Throwable th) {
			th.printStackTrace();
			System.out.println("event: " + event + " / age: " + age);
		}

		
//		System.out.println("Executing: " + handler);
		if (!handler.execute(this)) {
			statements.pop();

			if (!goalStack.isEmpty() && goalStack.peek().index == statements.size()) {
				Entry<Goal> entry = goalStack.pop();
				
				VariableVisitor visitor = new VariableVisitor();
				entry.value.accept(visitor);
				
//				dumpVariableTables();
				Map<Variable, Term> update = new HashMap<Variable, Term>();
				for (Variable variable : visitor.variables()) {
//					System.out.println("variable: " + variable + " / id=" + variable.id());
//					System.out.println("subgoal: " + subgoalVariables.get(entry.value).get(variable));
//					System.out.println("value: " + getVariableValue(subgoalVariables.get(entry.value).get(variable)));
					update.put(variable, getVariableValue(subgoalVariables.get(entry.value).get(variable)));
				}
				
				if (!bindingStack.isEmpty() && bindingStack.peek().index == statements.size()) {
					bindingStack.pop();
				}

				for(Map.Entry<Variable, Term> e : update.entrySet()) {
					updateVariable(e.getKey(), e.getValue());
				}
//				dumpVariableTables();
			} else {
				if (!bindingStack.isEmpty() && bindingStack.peek().index == statements.size()) {
					bindingStack.pop();
				}
			}
			
		}
//		System.out.println("Executed: " + handler);
//		dumpVariableTables();
		
		if (statements.isEmpty() && event instanceof GoalEvent) {
			GoalEvent g1 = (GoalEvent) event;
			if (g1.type() == GoalEvent.ADDITION) agent.addEvent(new GoalEvent(GoalEvent.REMOVAL, g1.goal()));
		}
		return !statements.isEmpty();
	}

	public Module getModule(String classname, String key) {
		return agent.getModule(classname,key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T evaluate(Term term) {
		if (term instanceof Primitive) {
			T val =  ((Primitive<T>) term).value();
			return val;
		}
		
		if (term instanceof Variable) {
			Term val = getValue((Variable) term);
//			System.out.println("variable: " + term + " / value: " + val);
			if (val instanceof NullTerm) {
				return null;
			}
			
			if (val == null) {
				ActionParam param = new ActionParam();
				actionParams.put((Variable) term, param);
				return (T) param;
			}
			return evaluate(val);
		}
		
		if (term instanceof Operator || term instanceof ModuleTerm) {
			Term t = (Term) term.accept(new ContextEvaluateVisitor(this));
			if (t instanceof Primitive) {
				return ((Primitive<T>) t).value();
			} else {
				return (T) t;
			}
		}
		
		if (term instanceof astra.term.FormulaTerm) {
			return (T) term;
		}
		if (term instanceof astra.term.ListTerm) {
			return (T) term.accept(new ContextEvaluateVisitor(this));
		}
		
		if (term instanceof astra.term.Funct) {
			return (T) term.accept(new ContextEvaluateVisitor(this));
		}
		
		System.out.println("term: " + term);
		System.out.println("EVALUATE: " + term.getClass().getName());

		return null;
	}

	public Term getValue(Variable term) {
		int i=bindingStack.size()-1;
		boolean finish = false;
		while (i >= 0 && !finish) {
			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
			if (entry.value.containsKey(term.id())) {
				return entry.value.get(term.id());
			}
			finish = entry.finish;
		}
//		System.out.println(">>>>>>>>>>>>>>>>>>> i=" + i);
		
		return null;
	}

	public void addStatement(StatementHandler handler) {
		if (handler.statement() instanceof Block) {
			bindingStack.push(new Entry<Map<Integer, Term>>(statements.size(), new HashMap<Integer, Term>(), false));
		}

		statements.push(handler);
	}

	public void addSubGoal(StatementHandler handler, Map<Integer, Term> bindings) {
		Map<Variable, Integer> unboundVariables = new HashMap<Variable, Integer>();
		Map<Integer,Term> b = new HashMap<Integer, Term>();
		for (java.util.Map.Entry<Integer, Term> entry : bindings.entrySet()) {
			if (entry.getValue() instanceof Variable) {
				unboundVariables.put((Variable) entry.getValue(), entry.getKey());
				b.put(entry.getKey(), null);
			} else {
				b.put(entry.getKey(), entry.getValue());
			}
		}
		
		subgoalVariables.put(goalStack.peek().value, unboundVariables);
		bindingStack.push(new Entry<Map<Integer, Term>>(statements.size(), b, true));
//		bindingStack.push(new Entry<Map<Integer, Term>>(statements.size(), bindings, true));
		statements.push(handler);
	}
	
	public void addSubGoal(Goal gl) {
		goalStack.push(new Entry<Goal>(statements.size(), gl, false));
		VariableVisitor visitor = new VariableVisitor();
		gl.accept(visitor);
//		System.out.println("pushing[" + statements.size() + "]: " + visitor.variables());
		for (Variable variable : visitor.variables()) {
			// NOTE: ADDED THE UPDATE VARIABLE LINE TO CHECK IF THE VARIABLE ALREADY EXISTS
			if (!this.updateVariable(variable, null)) {
				addVariable(variable);
//			} else {
//				System.out.println(variable + " exists");
			}
		}
		agent.addEvent(new GoalEvent(Event.ADDITION, gl, this));		
	}
	
	public void addScopedSubGoal(String scope, Goal gl) {
		goalStack.push(new Entry<Goal>(statements.size(), gl, false));
		agent.addEvent(new ScopedGoalEvent(Event.ADDITION, new ScopedGoal(scope, gl), this));		
	}

	/**
	 * Adds the current statement to the program stack and stores the provided bindings.  These bindings
	 * are applied to the existing bindings where possible, or are added as new variables where they
	 * don't already exist.
	 * 
	 */
	public void addStatement(StatementHandler handler, Map<Integer, Term> bindings) {
		// Step 1: Create a new binding layer
		bindingStack.push(new Entry<Map<Integer, Term>>(statements.size(), new HashMap<Integer, Term>(), false));
		
		// Step 2: Check to see if any of the bindings provided apply to existing variables
		for (Integer term : bindings.keySet()) {
			Term logic = bindings.get(term);
			
			// Step 2a: Check if the variable already exists in the intention...
			boolean newVariable = true;
			boolean finished = false;
			int i=bindingStack.size()-1;
			while (i >= 0 && !finished) {
				Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
				if (entry.value.containsKey(term)) {
					entry.value.put(term, logic);
					newVariable = false;
					break;
				}
				finished = entry.finish;
			}
			
			// Step 2b: If the variable does not exist in the bindings, add it as a new variable
			if (newVariable) {
				bindingStack.peek().value.put(term, logic);
			}
			
		}
		
		// Step 3: Push the handler on to the program stack.
		statements.push(handler);
	}
	
	public void addVariable(Variable variable) {
		bindingStack.peek().value.put(variable.id(), null);
	}

	public void removeVariable(Variable variable) {
		bindingStack.peek().value.remove(variable.id());
	}
	
	public boolean updateVariable(Variable term, Term logic) {
		int i=bindingStack.size()-1;
		while (i >= 0) {
			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
			if (entry.value.containsKey(term.id())) {
				entry.value.put(term.id(), logic);
				return true;
			}
			if (entry.finish) return false;
		}
		return false;
	}

	public void addUnboundVariables(Set<Variable> variables) {
		for(Variable variable : variables) {
			if (getValue(variable) == null) {
				addVariable(variable);
			}
		}
	}
	
	public void addBindings(Map<Integer, Term> bindings) {
		bindingStack.push(new Entry<Map<Integer, Term>>(statements.size()-1, bindings, true));
	}

	public String toString() {
		String out = "";
		for (int i=statements.size()-1 ; i >= 0; i--) {
			if (statements.get(i).statement() instanceof Subgoal || i == statements.size()-1) {
				out += statements.get(i) + "\n";
			}
		}
		out += event.toString() + "\n";	
		return out;
	}

	public void failed(String reason) {
		failed(reason, null);
	}
	
	public void failed(String reason, Throwable exception) {
		failed = true;
		failureTrace = new Stack<StatementHandler>();
		for(int i=0; i < statements.size(); i++) {
			failureTrace.push(statements.get(i));
		}
		failureReason = reason;
		this.exception = exception;
	}
	
	public boolean isFailed() {
		return failed;
	}

	public void printStackTrace() {
		if (failureTrace == null) return;
		System.err.println(failureReason);
		for (int i=failureTrace.size()-1; i >= 0;  i--) {
			if (failureTrace.get(i).statement() instanceof Subgoal || failureTrace.get(i).statement() instanceof PlanCall||i == failureTrace.size()-1) {
				System.err.print(failureTrace.get(i).statement().getASTRAClass() + "." + failureTrace.get(i));
				if (failureTrace.get(i).statement().isLinkedToSource()) {
					System.err.print(":" + failureTrace.get(i).statement().beginLine());
				}
				System.err.println();
			}
		}
		System.err.println(event.toString());
		if (exception != null) {
			System.err.println("Caused By:");
			exception.printStackTrace();
		}
	}

	public boolean rollback() {
		while (!statements.isEmpty()) {
			StatementHandler handler = statements.peek();
			if (handler.onFail(this)) {
				failed = false;
				resume();
				return true;
			}
			statements.pop();
		}
		return false;
	}

	public void addBelief(Predicate belief) {
		agent.beliefs().addBelief(belief);
	}

	public void removeBelief(Predicate belief) {
		agent.beliefs().dropBelief(belief);
	}

	public void suspend() {
		suspended = true;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void resume() {
		suspended = false;
	}

	public void resetActionParams() {
		actionParams.clear();
	}
	
	@SuppressWarnings("rawtypes")
	public void applyActionParams() {
		for (java.util.Map.Entry<Variable, ActionParam> entry : actionParams.entrySet()) {
			this.updateVariable(entry.getKey(), entry.getValue().toLogic());
		}
	}

	public void addVariable(Variable variable, Term term) {
		bindingStack.peek().value.put(variable.id(), term);
	}

	public void dumpState() {
		for (Entry<Map<Integer, Term>> entry : bindingStack) {
			dumpEntry(entry);
		}
	}

	public void dumpStack() {
		for (int i=statements.size()-1 ; i >= 0; i--) {
			System.out.println(i +". " + statements.get(i));
		}
		System.out.println(event.toString());
	}

	protected void dumpEntry(Entry<Map<Integer, Term>> entry) {
		System.out.print("Level " + entry.index + ": [");
		boolean first = true;
		for (java.util.Map.Entry<Integer, Term> e : entry.value.entrySet()) {
			if (first) first = false; else System.out.print(",");
			System.out.print(Variable.mapper.fromId(e.getKey()) +"(" + e.getKey() + ")" + "=" + e.getValue());
		}
		System.out.println("]=" + entry.finish);
	}

	public synchronized void dumpVariableTables() {
		System.out.println("Variable Tables for: " + this.event);
		for (int i=this.bindingStack.size()-1; i >= 0; i--) {
			System.out.println(bindingStack.get(i).toString());
		}
	}

	public void addGoal(Goal goal) {
		agent.addEvent(new GoalEvent(GoalEvent.ADDITION, goal));
	}

	public void addScopedGoal(String scope, Goal goal) {
		agent.addEvent(new ScopedGoalEvent(GoalEvent.ADDITION, new ScopedGoal(scope, goal)));
	}

	public void notifyDone(String message) {
		agent.notifyDone(new Agent.Notification(this, message));
	}

	public void notifyDone(String message, Throwable exception) {
		agent.notifyDone(new Agent.Notification(this, message, exception));
	}

	public void schedule(Task task) {
		agent.schedule(task);
	}

	public String name() {
		return agent.name();
	}
	
	public Map<Integer, Term> query(Formula formula) {
		List<Map<Integer, Term>> result = agent.query(formula, new HashMap<Integer, Term>());
		if (result == null) return null;
		if (result.isEmpty()) return null;
		return result.get(0);
	}

	public List<Map<Integer, Term>> queryAll(Formula formula) {
		return agent.queryAll(formula);
	}

	public void startFunction(Predicate function) {
		agent.startFunction(function);
	}

	public StatementHandler getNextStatement() {
		return statements.peek();
	}

	public void addEvent(Event event) {
		agent.addEvent(event);
		
	}

	public boolean hasLock(String token, Intention context) {
		return agent.hasLock(token, context);
	}

	public boolean requestLock(String token, Intention context) {
		return agent.requestLock(token, context);
	}

	public void releaseLock(String token, Intention context) {
		agent.releaseLock(token, context);
	}

	public void unrequestLock(String token, Intention context) {
		agent.unrequestLock(token, context);
	}

	public Plan getPlan(Predicate id) {
		return agent.getPlan(null, id);
	}

	public Plan getPlan(String scope, Predicate id) {
		return agent.getPlan(scope, id);
	}

	public void stopFunction(Predicate function) {
		agent.stopFunction(function);
	}

	public AcreAPI getAcreAPI() {
		return agent.getAcreAPI();
	}

	public Term getVariableValue(Integer vid) {
		int i=bindingStack.size()-1;
		boolean finish = false;
		while (i >= 0 && !finish) {
			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
			if (entry.value.containsKey(vid)) {
				return entry.value.get(vid);
			}
			finish = entry.finish;
		}
		
		return null;
	}

	public void addScopedBelief(String scope, Predicate belief) {
		agent.beliefs().addScopedBelief(scope, belief);
	}

	public void removeScopedBelief(String scope, Predicate belief) {
		agent.beliefs().dropScopedBelief(scope, belief);
	}

	public void removeBindings() {
		bindingStack.pop();
	}

	public boolean hasVariable(Variable variable) {
		int i=bindingStack.size()-1;
		boolean finish = false;
		while (i >= 0 && !finish) {
			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
			if (entry.value.containsKey(variable.id())) {
				return true;
			}
			finish = entry.finish;
		}
		
		return false;
	}

}
