package astra.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import astra.event.Event;
import astra.event.ModuleEvent;
import astra.formula.Formula;
import astra.formula.Inference;
import astra.formula.Predicate;
import astra.reasoner.Queryable;
import astra.reasoner.Unifier;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.VariableVisitor;
import astra.term.Term;
import astra.tr.Function;

public abstract class ASTRAClass implements Queryable {
	Map<String, List<Rule>> rules = new HashMap<String, List<Rule>>();
	Map<Integer, Function> functions = new HashMap<Integer, Function>();
	Map<Integer, Plan> plans = new HashMap<Integer, Plan>();
	Map<Integer, List<Formula>> inferences = new HashMap<Integer, List<Formula>>();
	private Set<String> filter = new HashSet<String>();

	private List<ASTRAClass> linearization;
	private Class<ASTRAClass>[] parents;
	private int distFromRoot = -1;

	public void setParents(Class<ASTRAClass>[] parents) {
		this.parents = parents;
	}

	public synchronized Agent newInstance(String name) throws AgentCreationException, ASTRAClassNotFoundException {
		if (Agent.hasAgent(name)) {
			throw new AgentCreationException("An agent with name: \"" + name + "\" already exists.");
		}
		
		Agent agent = new Agent(name);
		agent.setMainClass(this);
		
		return agent;
	}
	
	public abstract void initialize(Agent agent);
	
	public abstract Fragment createFragment(Agent agent) throws ASTRAClassNotFoundException;
	
	public List<ASTRAClass> getLinearization() throws ASTRAClassNotFoundException {
		if (linearization==null) {
			linearization = new LinkedList<ASTRAClass>();
			
			Queue<ASTRAClass> queue = new PriorityQueue<ASTRAClass>(1, new Comparator<ASTRAClass>() {
				@Override
				public int compare(ASTRAClass o1, ASTRAClass o2) {
					return o1.getDistance()-o2.getDistance();
				}
			});
			
			Queue<ASTRAClass> queue2 = new LinkedList<ASTRAClass>();
			queue2.add(this);
			while (!queue2.isEmpty()) {
				ASTRAClass claz = queue2.poll();
				if (claz.parents != null) {
					for (int i=claz.parents.length-1; i>-1; i--) {
						ASTRAClass c = ASTRAClassLoader.getDefaultClassLoader().loadClass(claz.parents[i]);
						if (!queue.contains(c) && !queue2.contains(c)) {
							queue2.add(c);
						}
					}
					queue.add(claz);
				}
			}
			
			while (!queue.isEmpty()) {
				ASTRAClass claz = queue.poll();
				if (!linearization.contains(claz)) 
					linearization.add(0, claz);
			}
		}
		return linearization;
	}
	
//	private ASTRAClass getParentClass(String name) {
//		try {
//			if (name.contains(".") || this.getClass().getPackage() == null)
//				return ASTRAClass.forName(null, name);
//			else
//				return ASTRAClass.forName(this.getClass().getPackage().getName(), name);
//		} catch (ASTRAClassNotFoundException e) {
//			System.err.println("Linearisation Error in class: " + getClass().getCanonicalName());
//			e.printStackTrace();
//			System.exit(0);
//		}
//		
//		return null;
//	}
	
	public int getDistance() {
		int maxDist, d;
		if (distFromRoot==-1) {
			maxDist=0;
			if (parents != null) {
				for (Class<ASTRAClass> parent : this.parents) {
					try {
						ASTRAClass cls = ASTRAClassLoader.getDefaultClassLoader().loadClass(parent);
						d = cls.getDistance();
						if (d > maxDist) maxDist = d;
					} catch (ASTRAClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			distFromRoot = maxDist+1;
		}
		return distFromRoot;
	}
	
	public String toString() {
		return "ASTRAClass:"+getClass().getCanonicalName();
	}
	
	public String getCanonicalName() {
		return getClass().getCanonicalName();
	}
	
	public void addRule(Rule rule) {
		List<Rule> list = rules.get(rule.event.signature());
		if (list == null) {
			filter.add(rule.event.signature());
			list = new LinkedList<Rule>();
			rules.put(rule.event.signature(), list);
		}
		
		list.add(rule);
	}
	
	public void addInference(Inference inference) {
		List<Formula> list = inferences.get(inference.head().id());
		if (list == null) {
			list = new LinkedList<Formula>();
			inferences.put(inference.head().id(), list);
		}
		
		list.add(inference);
	}
	
	public void addFunction(Function function) {
		if (functions.containsKey(function.identifier.id())) {
			System.out.println("Attempt to add duplicate function :" + function.identifier);
			return;
		}
		functions.put(function.identifier.id(), function);
	}
	
	public void addPlan(Plan plan) {
		if (plans.containsKey(plan.id().id())) {
			System.out.println("Attempt to add duplicate plan: " + plan.id());
			return;
		}
		plans.put(plan.id().id(), plan);
	}
	
	public Set<String> filter() {
		return filter;
	}

	public boolean handleEvent(Event event, Agent agent) {
		List<Rule> list = rules.get(event.signature());
//		System.out.println("list: " + list);
		if (list == null) return false;
		
		for (Rule rule : list) {
//			VariableVisitor visitor = new VariableVisitor();
			Event _event = rule.event;
			if (_event instanceof ModuleEvent) {
				_event = ((ModuleEvent) _event).adaptor().generate(agent,((ModuleEvent) _event).event());
			}
			
			if (_event != null) {
				Map<Integer, Term> bindings = Unifier.unify(_event, event, agent);
//				System.out.println("Event Bindings: " + bindings);
				if (bindings != null) {
					List<Map<Integer, Term>> results = agent.query(rule.context, bindings);
					if (results != null) {
						if (!results.isEmpty()) {
							bindings.putAll(results.get(0));
						}
	
						if (event.getSource() != null) {
							Intention intention = (Intention) event.getSource();
							intention.addSubGoal(rule.statement.getStatementHandler(), bindings);
							intention.resume();
						} else {
							agent.addIntention(new Intention(agent, event, rule, bindings));
						}
						return true;
					}
				}				
			}
		}
		
		return false;
	}
	
	public static ASTRAClass forName(String url) throws ASTRAClassNotFoundException {
		return ASTRAClassLoader.getDefaultClassLoader().loadClass(url);
	}
	
	public static ASTRAClass forName(String _package, String url) throws ASTRAClassNotFoundException {
		if (_package == null) return forName(url);
		return ASTRAClassLoader.getDefaultClassLoader().loadClass(_package+"."+url);
	}

	public Function getFunction(Predicate predicate) {
		return functions.get(predicate.id());
	}

	public Plan getPlan(Predicate id) {
		return plans.get(id.id());
	}

	public boolean hasFunctions() {
		return !functions.isEmpty();
	}

	@Override
	public List<Formula> getMatchingFormulae(Formula predicate) {
		if (predicate instanceof Predicate) {
			List<Formula> list = inferences.get(((Predicate) predicate).id());
			if (list != null) return list;
		}
		return new LinkedList<Formula>();
	}

	public boolean isSubclass(ASTRAClass cl) {
		try {
			for (ASTRAClass cls : cl.getLinearization()) {
				if (cls.getCanonicalName().equals(this.getCanonicalName())) return true;
			}
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Map<String, List<Rule>> rules() {
		return this.rules;
	}
}
