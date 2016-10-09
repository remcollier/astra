package astra.reasoner.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import astra.formula.AND;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
import astra.formula.Formula;
import astra.formula.FormulaVariable;
import astra.formula.Goal;
import astra.formula.ModuleFormula;
import astra.formula.NOT;
import astra.formula.OR;
import astra.formula.Predicate;
import astra.formula.ScopedGoal;
import astra.term.Brackets;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.Operator;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;

public class VariableVisitor implements LogicVisitor {
	public static interface Handler<T> {
		public Class<T> getType();
		public Object handle(LogicVisitor visitor, T object, Set<Variable> variables);
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
	
	Set<Variable> variables = new HashSet<Variable>();

	public Set<Variable> variables() {
		return variables;
	}
	
	public Object visit(Formula formula) {
		Handler<Formula> handler = getFormulaHandler(formula.getClass());
		if (handler == null) return null;
		return handler.handle(this, formula, variables);
	}
	
	public Object visit(Term term) {
		Handler<Term> handler = getTermHandler(term.getClass());
		if (handler == null) return null;
		return handler.handle(this, term, variables);
	}
	
	static {
		addTermHandler(new Handler<Variable>() {
			@Override public Class<Variable> getType() { return Variable.class; }
			@Override public Object handle(LogicVisitor visitor, Variable variable, Set<Variable> variables) {
				variables.add(variable);
				return null;
			}
		});
		addTermHandler(new Handler<Funct>() {
			@Override public Class<Funct> getType() { return Funct.class; }
			@Override public Object handle(LogicVisitor visitor, Funct function, Set<Variable> variables) {
				for (int i=0; i < function.size(); i++) {
					function.getTerm(i).accept(visitor);
				}
				return null;
			}
		});
		addTermHandler(new Handler<Variable>() {
			@Override public Class<Variable> getType() { return Variable.class; }
			@Override public Object handle(LogicVisitor visitor, Variable variable, Set<Variable> variables) {
				variables.add(variable);
				return null;
			}
		});
		addTermHandler(new Handler<FormulaTerm>() {
			@Override public Class<FormulaTerm> getType() { return FormulaTerm.class; }
			@Override public Object handle(LogicVisitor visitor, FormulaTerm formula, Set<Variable> variables) {
				if (formula.value() != null) formula.value().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<Operator>() {
			@Override public Class<Operator> getType() { return Operator.class; }
			@Override public Object handle(LogicVisitor visitor, Operator operator, Set<Variable> variables) {
				operator.left().accept(visitor);
				operator.right().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<ModuleTerm>() {
			@Override public Class<ModuleTerm> getType() { return ModuleTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleTerm term, Set<Variable> variables) {
				term.method().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<ListTerm>() {
			@Override public Class<ListTerm> getType() { return ListTerm.class; }
			@Override public Object handle(LogicVisitor visitor, ListTerm list, Set<Variable> variables) {
				try {
					for (Term term : list.terms()) {
						term.accept(visitor);
					}
				} catch (Throwable th) {
					System.out.println("list: " + list);
				}
				return null;
			}
		});
		addTermHandler(new Handler<QueryTerm>() {
			@Override public Class<QueryTerm> getType() { return QueryTerm.class; }
			@Override public Object handle(LogicVisitor visitor, QueryTerm term, Set<Variable> variables) {
				term.formula().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<Brackets>() {
			@Override public Class<Brackets> getType() { return Brackets.class; }
			@Override public Object handle(LogicVisitor visitor, Brackets brackets, Set<Variable> variables) {
				brackets.term().accept(visitor);
				return null;
			}
		});
		addTermHandler(new Handler<ListSplitter>() {
			@Override public Class<ListSplitter> getType() { return ListSplitter.class; }
			@Override public Object handle(LogicVisitor visitor, ListSplitter term, Set<Variable> variables) {
				term.head().accept(visitor);
				term.tail().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<Predicate>() {
			@Override public Class<Predicate> getType() { return Predicate.class; }
			@Override public Object handle(LogicVisitor visitor, Predicate predicate, Set<Variable> variables) {
				for (Term term : predicate.terms()) {
					term.accept(visitor);
				}
				return null;
			}
		});
		addFormulaHandler(new Handler<Comparison>() {
			@Override public Class<Comparison> getType() { return Comparison.class; }
			@Override public Object handle(LogicVisitor visitor, Comparison comparison, Set<Variable> variables) {
				comparison.left().accept(visitor);
				comparison.right().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<AND>() {
			@Override public Class<AND> getType() { return AND.class; }
			@Override public Object handle(LogicVisitor visitor, AND and, Set<Variable> variables) {
				and.left().accept(visitor);
				and.right().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<OR>() {
			@Override public Class<OR> getType() { return OR.class; }
			@Override public Object handle(LogicVisitor visitor, OR or, Set<Variable> variables) {
				or.left().accept(visitor);
				or.right().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<NOT>() {
			@Override public Class<NOT> getType() { return NOT.class; }
			@Override public Object handle(LogicVisitor visitor, NOT not, Set<Variable> variables) {
				not.formula().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<Goal>() {
			@Override public Class<Goal> getType() { return Goal.class; }
			@Override public Object handle(LogicVisitor visitor, Goal goal, Set<Variable> variables) {
				goal.formula().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<ScopedGoal>() {
			@Override public Class<ScopedGoal> getType() { return ScopedGoal.class; }
			@Override public Object handle(LogicVisitor visitor, ScopedGoal goal, Set<Variable> variables) {
				goal.formula().accept(visitor);
				return null;
			}
		});
		addFormulaHandler(new Handler<ModuleFormula>() {
			@Override public Class<ModuleFormula> getType() { return ModuleFormula.class; }
			@Override public Object handle(LogicVisitor visitor, ModuleFormula formula, Set<Variable> variables) {
				return formula.predicate().accept(visitor);
			}
		});
		addFormulaHandler(new Handler<FormulaVariable>() {
			@Override public Class<FormulaVariable> getType() { return FormulaVariable.class; }
			@Override public Object handle(LogicVisitor visitor, FormulaVariable variable, Set<Variable> variables) {
				return variable.variable().accept(visitor);
			}
		});
		addFormulaHandler(new Handler<BracketFormula>() {
			@Override public Class<BracketFormula> getType() { return BracketFormula.class; }
			@Override public Object handle(LogicVisitor visitor, BracketFormula formula, Set<Variable> variables) {
				return formula.formula().accept(visitor);
			}
		});
		addFormulaHandler(new Handler<Bind>() {
			@Override public Class<Bind> getType() { return Bind.class; }
			@Override public Object handle(LogicVisitor visitor, Bind bind, Set<Variable> variables) {
				bind.variable().accept(visitor);
				bind.term().accept(visitor);
				return null;
			}
		});
	}

//	@Override
//	public Object visit(AcreFormula formula) {
//		formula.cid().accept(this);
//		formula.index().accept(this);
//		formula.type().accept(this);
//		formula.performative().accept(this);
//		formula.content().accept(this);
//		return null;
//	}

}
