package astra.reasoner.util;

import java.util.HashMap;
import java.util.Map;

import astra.formula.AND;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Goal;
import astra.formula.Inference;
import astra.formula.IsNull;
import astra.formula.ModuleFormula;
import astra.formula.NOT;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.term.Brackets;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;

public class RenameVisitor implements LogicVisitor {
	public static interface Handler<T> {
		public Class<T> getType();
		public Object handle(LogicVisitor visitor, T object, String modifier, Map<Integer, Term> bindings);
	}

	private static Map<Class<? extends Formula>, Handler<? extends Formula>> formulaHandlers = new HashMap<Class<? extends Formula>, Handler<? extends Formula>>();
	private static Map<Class<? extends Term>, Handler<? extends Term>> termHandlers = new HashMap<Class<? extends Term>, Handler<? extends Term>>();

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

	String modifier;
	private Map<Integer, Term> bindings = new HashMap<Integer, Term>();
	
	public Map<Integer, Term> bindings() {
		return bindings;
	}
	
	public RenameVisitor(String modifier) {
		this.modifier = modifier;
	}
	
	public Object visit(Formula formula) {
		Handler<Formula> handler = getFormulaHandler(formula.getClass());
		if (handler == null) {
			return null;
		}
		return handler.handle(this, formula, modifier, bindings);
	}
	
	public Object visit(Term term) {
		Handler<Term> handler = getTermHandler(term.getClass());
		if (handler == null) {
			return null;
		}
		return handler.handle(this, term, modifier, bindings);
	}
	
	static{
		addTermHandler(new Handler<Variable>() {
			@Override public Class<Variable> getType() { return Variable.class; }
			@Override public Object handle(LogicVisitor visitor, Variable variable, String modifier, Map<Integer, Term> bindings) {
				Variable v = new Variable(variable.type(), modifier + variable.identifier());
				bindings.put(variable.id(), v);
				return v;
			}
		});
		addTermHandler(new Handler<Primitive>() {
			@Override public Class<Primitive> getType() { return Primitive.class; }
			@Override public Object handle(LogicVisitor visitor, Primitive primitive, String modifier, Map<Integer, Term> bindings) {
				return primitive;
			}
		});
		addTermHandler(new Handler<Operator>() {
			@Override public Class<Operator> getType() { return Operator.class; }
			@Override public Object handle(LogicVisitor visitor, Operator operator, String modifier, Map<Integer, Term> bindings) {
				return Operator.newOperator(operator.op(), (Term) operator.left().accept(visitor), (Term) operator.right().accept(visitor));
			}
		});
		addTermHandler(new Handler<Funct>() {
			@Override public Class<Funct> getType() { return Funct.class; }
			@Override public Object handle(LogicVisitor visitor, Funct function, String modifier, Map<Integer, Term> bindings) {
				Term[] terms = new Term[function.size()];
				
				for (int i=0; i < function.size(); i++) {
					terms[i] = (Term) function.getTerm(i).accept(visitor);
				}
				return new Predicate(function.functor(), terms);
			}
		});
		addTermHandler(new Handler<ModuleTerm>() {
			@Override public Class<ModuleTerm> getType() { return ModuleTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleTerm term, String modifier, Map<Integer, Term> bindings) {
				return new ModuleTerm(term.module(), term.type(), (Predicate) term.method().accept(visitor), term.adaptor());
			}
		});
		addTermHandler(new Handler<ListTerm>() {
			@Override public Class<ListTerm> getType() { return ListTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ListTerm list, String modifier, Map<Integer, Term> bindings) {
				Term[] terms = new Term[list.terms().length];
				for (int i=0;i<list.terms().length; i++) {
					terms[i] = (Term) list.get(i).accept(visitor);
				}
				return new ListTerm(terms);
			}
		});
		addTermHandler(new Handler<QueryTerm>() {
			@Override public Class<QueryTerm> getType() { return QueryTerm.class; }
			@Override public Object handle(LogicVisitor visitor, QueryTerm term, String modifier, Map<Integer, Term> bindings) {
				return new QueryTerm((Formula) term.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Predicate>() {
			@Override public Class<Predicate> getType() { return Predicate.class; }
			@Override public Object handle(LogicVisitor visitor, Predicate predicate, String modifier, Map<Integer, Term> bindings) {
				Term[] terms = new Term[predicate.size()];
				for (int i=0; i < predicate.size(); i++) {
					terms[i] = (Term) predicate.termAt(i).accept(visitor);
				}
				return new Predicate(predicate.predicate(), terms);
			}
		});
		addFormulaHandler(new Handler<AND>() {
			@Override public Class<AND> getType() { return AND.class; }
			@Override public Object handle(LogicVisitor visitor, AND and, String modifier, Map<Integer, Term> bindings) {
				return new AND((Formula) and.left().accept(visitor), (Formula) and.right().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<OR>() {
			@Override public Class<OR> getType() { return OR.class; }
			@Override public Object handle(LogicVisitor visitor, OR or, String modifier, Map<Integer, Term> bindings) {
				return new OR((Formula) or.left().accept(visitor), (Formula) or.right().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<NOT>() {
			@Override public Class<NOT> getType() { return NOT.class; }
			@Override public Object handle(LogicVisitor visitor, NOT not, String modifier, Map<Integer, Term> bindings) {
				return new NOT((Formula) not.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Goal>() {
			@Override public Class<Goal> getType() { return Goal.class; }
			@Override public Object handle(LogicVisitor visitor, Goal goal, String modifier, Map<Integer, Term> bindings) {
				return new Goal((Predicate) goal.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<ScopedGoal>() {
			@Override public Class<ScopedGoal> getType() { return ScopedGoal.class; }
			@Override public Object handle(LogicVisitor visitor, ScopedGoal goal, String modifier, Map<Integer, Term> bindings) {
				return new ScopedGoal(goal.scope(), (Goal) goal.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Comparison>() {
			@Override public Class<Comparison> getType() { return Comparison.class; }
			@Override public Object handle(LogicVisitor visitor, Comparison comparison, String modifier, Map<Integer, Term> bindings) {
				return new Comparison(comparison.operator(), (Term) comparison.left().accept(visitor), (Term) comparison.right().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<ModuleFormula>() {
			@Override public Class<ModuleFormula> getType() { return ModuleFormula.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleFormula formula, String modifier, Map<Integer, Term> bindings) {
				return new ModuleFormula(formula.module(), (Predicate) formula.predicate().accept(visitor), formula.adaptor());
			}
		});
		addFormulaHandler(new Handler<FormulaVariable>() {
			@Override public Class<FormulaVariable> getType() { return FormulaVariable.class; }
			@Override public Object handle(LogicVisitor visitor, FormulaVariable variable, String modifier, Map<Integer, Term> bindings) {
				return new FormulaVariable((Variable) variable.variable().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Inference>() {
			@Override public Class<Inference> getType() { return Inference.class; }
			@Override public Object handle(LogicVisitor visitor, Inference inference, String modifier, Map<Integer, Term> bindings) {
				return new Inference((Predicate) inference.head().accept(visitor), (Formula) inference.body().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<IsNull>() {
			@Override public Class<IsNull> getType() { return IsNull.class; }
			@Override public Object handle(LogicVisitor visitor, IsNull isNull, String modifier, Map<Integer, Term> bindings) {
				return new IsNull((Term) isNull.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<BracketFormula>() {
			@Override public Class<BracketFormula> getType() { return BracketFormula.class; }
			@Override public Object handle(LogicVisitor visitor, BracketFormula formula, String modifier, Map<Integer, Term> bindings) {
				return new BracketFormula((BracketFormula) formula.formula().accept(visitor));
			}
		});
		addFormulaHandler(new Handler<Bind>() {
			@Override public Class<Bind> getType() { return Bind.class; }
			@Override public Object handle(LogicVisitor visitor, Bind bind, String modifier, Map<Integer, Term> bindings) {
				return new Bind((Variable) bind.variable().accept(visitor), (Term) bind.term().accept(visitor));
			}
		});
		addTermHandler(new Handler<Brackets>() {
			@Override public Class<Brackets> getType() { return Brackets.class; }
			@Override public Object handle(LogicVisitor visitor, Brackets term, String modifier, Map<Integer, Term> bindings) {
				return new Brackets((Term) term.term().accept(visitor));
			}
		});
		addTermHandler(new Handler<ListSplitter>() {
			@Override public Class<ListSplitter> getType() { return ListSplitter.class; }
			@Override public Object handle(LogicVisitor visitor, ListSplitter term, String modifier, Map<Integer, Term> bindings) {
				return new ListSplitter((Variable) term.head().accept(visitor), (Variable) term.tail().accept(visitor));
			}
		});
	}


//	@Override
//	public Object visit(CartagoProperty property) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object visit(AcreFormula formula) {
//		return new AcreFormula((Term) formula.index().accept(this), (Term) formula.type().accept(this), (Term) formula.performative().accept(this),
//				(Term) formula.cid().accept(this), (Formula) formula.content().accept(this));
//	}

}
