package astra.reasoner;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import astra.core.Agent;
import astra.eis.EISFormula;
import astra.formula.AND;
import astra.formula.AcreFormula;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.ModuleFormula;
import astra.formula.NOT;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.term.Term;
import astra.util.Utilities;
import astra.util.VariableVisitor;

public class ResolutionBasedReasoner implements Reasoner {
	public static final int MAX_DEPTH 										= 200;
	
	public static Map<String, ReasonerStackEntryFactory> factories = 
			new HashMap<String, ReasonerStackEntryFactory>();
	
	public static void register(Class<? extends Formula> class1, ReasonerStackEntryFactory factory) {
		factories.put(class1.getName(), factory);
	}

	static {
		// Install the default set of stack entries
		ResolutionBasedReasoner.register(AcreFormula.class, new AcreFormulaStackEntryFactory());
		ResolutionBasedReasoner.register(AND.class, new ANDStackEntryFactory());
		ResolutionBasedReasoner.register(Bind.class, new BindStackEntryFactory());
		ResolutionBasedReasoner.register(Comparison.class, new ComparisonStackEntryFactory());
		ResolutionBasedReasoner.register(EISFormula.class, new EISFormulaStackEntryFactory());
		ResolutionBasedReasoner.register(ModuleFormula.class, new ModuleFormulaStackEntryFactory());
		ResolutionBasedReasoner.register(NOT.class, new NOTStackEntryFactory());
		ResolutionBasedReasoner.register(OR.class, new ORStackEntryFactory());
		ResolutionBasedReasoner.register(Predicate.class, new PredicateStackEntryFactory());
	}
	
	List<Queryable> sources = Collections.synchronizedList(new LinkedList<Queryable>());
	
	Stack<ReasonerStackEntry> stack;
	Stack<Formula> formulae;
	List<Map<Integer, Term>> solutions;
	boolean singleResult;
	Agent agent;
	
	public ResolutionBasedReasoner(Agent agent) {
		this.agent = agent;
	}
	
	public ResolutionBasedReasoner copy() {
		ResolutionBasedReasoner reasoner = new ResolutionBasedReasoner(agent);
		reasoner.sources = sources;
		return reasoner;
	}
	
	void propagateBindings(Map<Integer, Term> bindings) {
//		System.out.println("stack.size()=" + stack.size());
		if (stack.size() == 1) {
//			System.out.println("this is the end...");
			solutions.add(bindings);
		} else {
//			System.out.println("Pushing up stack...");
			stack.get(stack.size()-2).addBindings(bindings);
		}
	}

	ReasonerStackEntry newStackEntry(Formula formula, Map<Integer, Term> bindings) {
		ReasonerStackEntryFactory factory = factories.get(formula.getClass().getName());
		if (factory == null) {
			if (BracketFormula.class.isInstance(formula)) {
				return newStackEntry(((BracketFormula) formula).formula(), bindings);
			} else {
				System.err.println("Reasoner did not handle entry: " + formula);
				System.err.println("Type: " + formula.getClass().getName());
				return null;
			}
		}
		return factory.create(this, formula, bindings);
	}
	
	public void addSource(Queryable source) {
		sources.add(source);
	}
	
	public List<Map<Integer, Term>> queryAll(Formula formula) {
		this.singleResult = false;
		List<Map<Integer, Term>> list = doQuery(formula, new HashMap<Integer, Term>());
//		System.out.println("list: " + list);
		return list;
	}
	
	public List<Map<Integer, Term>> query(Formula formula) {
//		stats = new Stats();
		this.singleResult = true;
		List<Map<Integer, Term>> result = doQuery(formula, new HashMap<Integer, Term>());
//		System.out.println("stats: " + stats.entries + " / " + stats.steps + " / " + stats.predicateChecks + " / " + (System.nanoTime()-stats.start));
		return result;
	}
	
	private List<Map<Integer, Term>> doQuery(Formula formula, Map<Integer, Term> initial) {
		stack = new Stack<ReasonerStackEntry>();
		formulae = new Stack<Formula>();
		solutions = new LinkedList<Map<Integer, Term>>();

//		System.out.println("\n\n========================== solving ==========================");
		stack.push(newStackEntry(formula, initial));
		while (!stack.isEmpty() && stack.size() < MAX_DEPTH) {
//			System.out.println("\n\n================================\n" + stack.size() + ": " + stack.peek());
//			System.out.println("\tsolutions: " + solutions);
//			stats.steps++;
			if (!stack.peek().solve()) {
//				System.out.println("Failure");
				if (!propogateFailure()) return null;
//			} else {
//				System.out.println("success");
			}
		}
		
//		System.out.println("\n\n========================= solutions =========================");
//		System.out.println("solutions: " + solutions);
		VariableVisitor visitor = new VariableVisitor();
		formula.accept(visitor);
//		System.out.println("visitor variables: " + visitor.variables());
		List<Map<Integer, Term>> filtered = new LinkedList<Map<Integer, Term>>();
		for (Map<Integer, Term> bindings : solutions) {
			Map<Integer, Term> b = Utilities.mgu(bindings);
//			System.out.println("b: " + b);
			filtered.add(Utilities.filter(b, visitor.variables()));
		}
//		System.out.println("filtered: " + filtered);
		return filtered;
	}
	
	private boolean propogateFailure() {
		while (!stack.isEmpty() && stack.peek().failure()) stack.pop();

//		System.out.println("stack size: " + stack.size());
		return !stack.isEmpty();
	}

	@Override
	public List<Map<Integer, Term>> query(Formula formula, Map<Integer, Term> bindings) {
		this.singleResult = false;
		return doQuery(formula, bindings);
	}
}
