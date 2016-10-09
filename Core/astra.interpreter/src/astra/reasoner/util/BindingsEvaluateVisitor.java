package astra.reasoner.util;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Inference;
import astra.formula.IsNull;
import astra.formula.Predicate;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.NullTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;

public class BindingsEvaluateVisitor extends AbstractEvaluateVisitor {
	private static Map<Class<? extends Formula>, Handler<? extends Formula>> formulaHandlers = new HashMap<Class<? extends Formula>, Handler<? extends Formula>>();
	private static Map<Class<? extends Term>, Handler<? extends Term>> termHandlers = new HashMap<Class<? extends Term>, Handler<? extends Term>>();

	static {
		addTermHandler(new Handler<Variable>() {
			@Override public Class<Variable> getType() { return Variable.class; }
			@Override public Object handle(LogicVisitor visitor, Variable variable, Map<Integer, Term> bindings, Agent agent) {
				Term term =  bindings.get(variable.id());
//				System.out.println(variable + " (" + variable.id() + ") " + term);
				if (term == null) return variable;
				return term;
			}
		});
		addTermHandler(new Handler<ModuleTerm>() {
			@Override public Class<ModuleTerm> getType() { return ModuleTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleTerm term, Map<Integer, Term> bindings, Agent agent) {
				return Primitive.newPrimitive(term.evaluate((BindingsEvaluateVisitor)visitor));
			}
		});
		addTermHandler(new Handler<QueryTerm>() {
			@Override public Class<QueryTerm> getType() { return QueryTerm.class; }
			@Override public Object handle(LogicVisitor visitor, QueryTerm term, Map<Integer, Term> bindings, Agent agent) {
				return Primitive.newPrimitive(agent.query(term.formula(), bindings) == null ? false:true); 
			}
		});
		addFormulaHandler(new Handler<IsNull>() {
			@Override public Class<IsNull> getType() { return IsNull.class; }
			@Override public Object handle(LogicVisitor visitor, IsNull isNull, Map<Integer, Term> bindings, Agent agent) {
				Term t = isNull.formula();
				if (t instanceof Variable) {
					Term term = bindings.get(((Variable) t).id());
					return (term == null) ? Predicate.TRUE : Predicate.FALSE;
				} else if (t instanceof FormulaVariable) {
					Term term = bindings.get(((FormulaVariable) t).variable().id());
					return (term == null) ? Predicate.TRUE : Predicate.FALSE;
				}
				return Predicate.FALSE;
			}
		});
		addFormulaHandler(new Handler<Inference>() {
			@Override public Class<Inference> getType() { return Inference.class; }
			@Override public Object handle(LogicVisitor visitor, Inference inference, Map<Integer, Term> bindings, Agent agent) {
				return new Inference((Predicate) inference.head().accept(visitor), (Formula) inference.body().accept(visitor));
			}
		});
	}

	public static interface Handler<T> {
		public Class<T> getType();
		public Object handle(LogicVisitor visitor, T object, Map<Integer, Term> bindings, Agent agent);
	}

	public static <T extends Formula> void addFormulaHandler(Handler<T> handler) {
		formulaHandlers.put(handler.getType(), handler);
	}
	
	public static <T extends Term> void addTermHandler(Handler<T> handler) {
		termHandlers.put(handler.getType(), handler);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Formula> Handler<Formula> getFormulaHandler(Class<T> cls) {
		return (Handler<Formula>) formulaHandlers.get(cls);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Term> Handler<Term> getTermHandler(Class<T> cls) {
		return (Handler<Term>) termHandlers.get(cls);
	}
	
	Map<Integer, Term> bindings;
	Agent agent;
	
	public BindingsEvaluateVisitor(Map<Integer, Term> bindings, Agent agent) {
		super(false);
		this.bindings = bindings;
		this.agent = agent;
	}
	
	public Agent agent() {
		return agent;
	}
	
	public Object visit(Formula formula) {
		Handler<Formula> handler = getFormulaHandler(formula.getClass());
		if (handler == null) {
			return super.visit(formula);
		}
		return handler.handle(this, formula, bindings, agent);
	}
	
	public Object visit(Term term) {
		Handler<Term> handler = getTermHandler(term.getClass());
		if (handler == null) {
			return super.visit(term);
		}
		return handler.handle(this, term, bindings, agent);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T evaluate(Term term) {
		if (term instanceof Primitive) {
			T val =  ((Primitive<T>) term).value();
			return val;
		}
		
		if (term instanceof Variable) {
//			System.out.println("term: " + term);
			Term t = (Term) term.accept(this);
//			System.out.println("t: " + term);
			
			// check if term is unbound...
			if (t instanceof Variable) {
				return null;
			}
			
			return evaluate(t);
		}
		
		if (term instanceof Operator) {
			return ((Primitive<T>) term.accept(this)).value();
		}

		if (term instanceof ModuleTerm) {
			return ((Primitive<T>) term.accept(this)).value();
		}
		
		if (term instanceof ListTerm || term instanceof FormulaTerm || term instanceof Funct) {
			return (T) term.accept(this);
		}
		
		if (term instanceof ModuleTerm) {
			return ((Primitive<T>) term.accept(this)).value();
		}
		
		if (term instanceof NullTerm) {
//			throw new UnsupportedOperationException("handling null term");
			return null;
		}
		
		System.out.println("[ModuleFormulaAdaptor] FAILED TO EVALUATE: " + term.getClass().getName());

		return null;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public Object visit(CartagoProperty property) {
//		Predicate obs_prop = null;
//		if (property.target() != null) {
//			obs_prop = CartagoAPI.get(agent).store().getObservableProperty(property.target(), property.content().predicate());
//		} else {
//			obs_prop = CartagoAPI.get(agent).store().getObservableProperty(property.content().predicate());
//		}
//		
//		if (obs_prop == null) return property;
//		
//		Map<Integer, Term> b = Unifier.unify(obs_prop, property.content(), agent);
//		if (b != null) bindings.putAll(b);
//		
//		return (b == null) ? Predicate.FALSE : Predicate.TRUE;
//	}
//
//	@Override
//	public Object visit(AcreFormula formula) {
//		return new AcreFormula(
//				(Term) formula.cid().accept(this),
//				(Term) formula.index().accept(this),
//				(Term) formula.type().accept(this), 
//				(Term) formula.performative().accept(this), 
//				formula.content() instanceof FormulaVariable ? formula.content():(Formula) formula.content().accept(this)
//		);
//	}
}
