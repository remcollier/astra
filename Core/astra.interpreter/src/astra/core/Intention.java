package astra.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import astra.core.Agent.Promise;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.event.ScopedGoalEvent;
import astra.formula.Formula;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.statement.PlanCall;
import astra.statement.StatementHandler;
import astra.statement.Subgoal;
import astra.term.FormulaTerm;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;

public class Intention {
	@SuppressWarnings("rawtypes")
	Map<Variable, ActionParam> actionParams = new HashMap<Variable, ActionParam>();
	Stack<StatementHandler> failureTrace;
	String failureReason;

	private Throwable exception;
	public Agent agent;
	Event event;
	private Rule rule;
	Map<Integer, Term> bindings;
	
	boolean suspended = false;
	boolean failed = false;
	boolean recovering = false;
	boolean dead = false;
	int age = 0;
	
	Stack<RuleExecutor> executors = new Stack<RuleExecutor>();
	
	public Intention(Agent agent, Event event, Rule rule, Map<Integer, Term> bindings) {
		this.agent = agent;
		this.event = event;
		this.bindings = bindings;
		this.rule = rule;
		
		executors.push(new RuleExecutor(event, rule, bindings));
	}
	
	public synchronized boolean execute() {
		if (executors.isEmpty()) return true;
		return executors.peek().execute(this);
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
//			System.out.println(executors.peek().variableTrace());
			Term val = getValue((Variable) term);
//			System.out.println("variable: " + term + " / value: " + val);
			if (val instanceof NullTerm) {
				return null;
			}
			
			if (val == null) {
//				System.out.println("adding action param: " + term);
				ActionParam param = new ActionParam();
				actionParams.put((Variable) term, param);
				return (T) param;
			}
			return evaluate(val);
		}
		
		if (term instanceof Operator || term instanceof ModuleTerm) {
//			System.out.println("Processing: " + term);
			Term t = (Term) term.accept(new ContextEvaluateVisitor(this));
			if (t instanceof Primitive) {
				return ((Primitive<T>) t).value();
			} else {
				return (T) t;
			}
		}
		
		if (term instanceof astra.term.FormulaTerm) {
			return (T) ((FormulaTerm) term).value();
		}
		
		if (term instanceof astra.term.ListTerm) {
			return (T) term.accept(new ContextEvaluateVisitor(this));
		}
		
		if (term instanceof astra.term.Funct) {
			return (T) term.accept(new ContextEvaluateVisitor(this));
		}

		if ((term instanceof astra.term.Head) || (term instanceof astra.term.Tail) || (term instanceof astra.term.AtIndex)) {
			Term _term = (Term) term.accept(new ContextEvaluateVisitor(this));
			return evaluate(_term);
		}
		
		
		System.out.println("term: " + term);
		System.out.println("EVALUATE: " + term.getClass().getName());

		return null;
	}

	public Term getValue(Variable term) {
		return executors.peek().getValue(term);
	}

	public void addStatement(StatementHandler handler) {
		executors.peek().addStatement(handler);
	}

	public void addSubGoal(Event event, Rule rule, Map<Integer, Term> bindings) {
		executors.push(new RuleExecutor(event, rule, bindings));
	}
	
	public boolean addSubGoal(Goal gl) {
		VariableVisitor visitor = new VariableVisitor();
		gl.accept(visitor);
		for (Variable variable : visitor.variables()) {
			// NOTE: ADDED THE UPDATE VARIABLE LINE TO CHECK IF THE VARIABLE ALREADY EXISTS
			if (!this.updateVariable(variable, null)) {
				addVariable(variable);
			}
		}
		return agent.addEvent(new GoalEvent(Event.ADDITION, gl, this));
	}
	
	public boolean addScopedSubGoal(String scope, Goal gl) {
		return agent.addEvent(new ScopedGoalEvent(Event.ADDITION, new ScopedGoal(scope, gl), this));		
	}

	/**
	 * Adds the current statement to the program stack and stores the provided bindings.  These bindings
	 * are applied to the existing bindings where possible, or are added as new variables where they
	 * don't already exist.
	 * 
	 */
	public void addStatement(StatementHandler handler, Map<Integer, Term> bindings) {
		executors.peek().addStatement(handler, bindings);
	}
	
	public void addVariable(Variable variable) {
		executors.peek().addVariable(variable);
	}

	public void removeVariable(Variable variable) {
		executors.peek().removeVariable(variable);
	}
	
	public boolean updateVariable(Variable term, Term logic) {
		return executors.peek().updateVariable(term, logic);
	}

	public void addUnboundVariables(Set<Variable> variables) {
		for(Variable variable : variables) {
			if (getValue(variable) == null) {
				addVariable(variable);
			}
		}
	}
	
	public void addBindings(Map<Integer, Term> bindings) {
		executors.peek().addBindings(bindings);
	}

	public String toString() {
		String out = "";
		for (int i=executors.size()-1 ; i >= 0; i--) {
			out += executors.get(i).event() +"\n";
		}
		return out;
	}

	public void failed(String reason) {
		failed(reason, null);
	}
	
	public void failed(String reason, Throwable exception) {
//		System.out.println("failure: " +reason);
//		exception.printStackTrace();
		failed = true;
		failureTrace = new Stack<StatementHandler>();
		for (RuleExecutor executor : executors) {
			executor.buildFailureTrace(failureTrace);
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

	public synchronized boolean rollback() {
		// DEAL WITH ROLLBACK...
		while (!executors.isEmpty()) {
			RuleExecutor executor = executors.peek();
			if (executor.rollback(this)) {
				failed = false;
				resume();
				return true;
			}
			
			executors.pop();
			
			if (failed) {
//				System.out.println("failed rule: " + executor.event());
				if (executor.event() instanceof GoalEvent) {
					GoalEvent ge = (GoalEvent) executor.event();
					if (ge.type == GoalEvent.ADDITION) {
//						System.out.println("Handling Failure..."); 
						if (agent.addEvent(ge = new GoalEvent(GoalEvent.REMOVAL, ge.goal(), this))) {
							recovering = true;
//							System.out.println("added failure goal: " + ge);
							failed = false;
							return true;
//						} else {
//							System.out.println("Failed to add recovery goal...");
						}
						
					}
				}
			}
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

	public boolean isRecovering() {
		return recovering;
	}

	public void resume() {
		if (recovering) recovering = false;
		suspended = false;
	}

	public void resetActionParams() {
		actionParams.clear();
	}
	
	@SuppressWarnings("rawtypes")
	public void applyActionParams() {
//		if (!actionParams.isEmpty()) {
//			System.out.println("-----------------------------------------------------------------------");
//			System.out.println(executors.peek().variableTrace());
//		}
		for (java.util.Map.Entry<Variable, ActionParam> entry : actionParams.entrySet()) {
//			System.out.println("Updating: " + entry.getKey() + " / " + entry.getValue().toLogic());
			this.updateVariable(entry.getKey(), entry.getValue().toLogic());
		}
//		if (!actionParams.isEmpty()) {
//			System.out.println(executors.peek().variableTrace());
//			System.out.println("-----------------------------------------------------------------------");
//		}
	}

	public void addVariable(Variable variable, Term term) {
		executors.peek().addVariable(variable, term);
	}

	public void dumpStack() {
		throw new UnsupportedOperationException("Intention.dumpStack does not work");
//		for (int i=statements.size()-1 ; i >= 0; i--) {
//			System.out.println(i +". " + statements.get(i));
//		}
//		System.out.println(event.toString());
	}

	public boolean addGoal(Goal goal) {
		return agent.addEvent(new GoalEvent(GoalEvent.ADDITION, goal));
	}

	public boolean addScopedGoal(String scope, Goal goal) {
		return agent.addEvent(new ScopedGoalEvent(GoalEvent.ADDITION, new ScopedGoal(scope, goal)));
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

	public boolean startFunction(Predicate function) {
		return agent.startFunction(function);
	}

	public boolean stopFunction() {
		return agent.stopFunction();
	}

	public StatementHandler getNextStatement() {
		return executors.peek().getNextStatment();
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

	public Term getVariableValue(Integer vid) {
//		int i=bindingStack.size()-1;
//		boolean finish = false;
//		while (i >= 0 && !finish) {
//			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
//			if (entry.value.containsKey(vid)) {
//				return entry.value.get(vid);
//			}
//			finish = entry.finish;
//		}
//		
		return null;
	}

	public void addScopedBelief(String scope, Predicate belief) {
		agent.beliefs().addScopedBelief(scope, belief);
	}

	public void removeScopedBelief(String scope, Predicate belief) {
		agent.beliefs().dropScopedBelief(scope, belief);
	}

	public void removeBindings() {
//		bindingStack.pop();
	}

	public boolean hasVariable(Variable variable) {
//		int i=bindingStack.size()-1;
//		boolean finish = false;
//		while (i >= 0 && !finish) {
//			Entry<Map<Integer, Term>> entry = bindingStack.get(i--);
//			if (entry.value.containsKey(variable.id())) {
//				return true;
//			}
//			finish = entry.finish;
//		}
//		
		return false;
	}

	public String failureReason() {
		return failureReason;
	}

	public void makePromise(Promise promise) {
//		System.out.println("made promise: " + promise.formula);
		agent.addPromise(promise);
	}

	public void dropPromise(Promise promise) {
//		System.out.println("dropped promise: " + promise.formula);
		agent.dropPromise(promise);
	}

	public boolean handleEvent(Event event, Agent agent) {
		boolean success = false;
		int i=executors.size()-1;
		while (!success && i>=0) {
			success = Helper.handleEvent(event, agent, executors.get(i).rule().rules(), this);
			i--;
		}
		return success;
	}

	public Rule rule() {
		if (executors.isEmpty()) return rule;
		return executors.peek().rule();
	}

	public boolean isDone() {
		return this.executors.isEmpty();
	}

	public boolean isGoalCompleted() {
//		System.out.println("checking completed: " + event + " / " + executors.peek().event() + " / " + executors.peek().isDone());
//		if (!executors.peek().isDone()) executors.peek().printStackTrace();
		if (executors.isEmpty()) return true;
		return executors.peek().isDone();
	}

	public synchronized void dropRule() {
//		System.out.println("Dropping: " + executors.peek().event());
		executors.pop();
//		if (!executors.isEmpty()) System.out.println("["+executors.size()+"] " + executors.peek().event());
	}

	public boolean isActive() {
		return !suspended && !failed;
	}

	public synchronized boolean checkEvent(Event event) {
		String signature = event.signature();
		for (int i=executors.size()-1; i>=0; i--) {
			if (executors.get(i).rule().filter().contains(signature)) {
				return true;
			}
		}
		return false;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public boolean isDead() {
		return this.dead;
	}

	public Map<Integer,Term> getBindings() {
		Map<Integer, Term> b = new HashMap<Integer,Term>();
		for(Entry<Integer, Term> entry : bindings.entrySet()) {
			b.put(entry.getKey(), entry.getValue());
		}
		for (RuleExecutor executor : executors) {
			for(Entry<Integer, Term> entry : executor.bindings().entrySet()) {
				b.put(entry.getKey(), entry.getValue());
			}
		}
		return b;
	}

}
