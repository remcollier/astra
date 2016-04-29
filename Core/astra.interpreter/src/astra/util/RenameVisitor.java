package astra.util;

import java.util.HashMap;
import java.util.Map;

import astra.cartago.CartagoProperty;
import astra.eis.EISFormula;
import astra.formula.AND;
import astra.formula.AcreFormula;
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
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListSplitter;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.Operator;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.QueryTerm;
import astra.term.Term;
import astra.term.Variable;

public class RenameVisitor implements LogicVisitor {
	String modifier;
	private Map<Integer, Term> bindings = new HashMap<Integer, Term>();
	
	public Map<Integer, Term> bindings() {
		return bindings;
	}
	
	public RenameVisitor(String modifier) {
		this.modifier = modifier;
	}
	
	@Override
	public Object visit(Variable variable) {
		Variable v = new Variable(variable.type(), modifier + variable.identifier());
		bindings.put(variable.id(), v);
		return v;
	}

	@Override
	public Object visit(Primitive<?> primitive) {
		return primitive;
	}

	@Override
	public Object visit(Operator operator) {
		return Operator.newOperator(operator.op(), (Term) operator.left().accept(this), (Term) operator.right().accept(this));
	}

	@Override
	public Object visit(Predicate predicate) {
		Term[] terms = new Term[predicate.size()];
		for (int i=0; i < predicate.size(); i++) {
			terms[i] = (Term) predicate.termAt(i).accept(this);
		}
		return new Predicate(predicate.predicate(), terms);
	}
	
	@Override
	public Object visit(Funct function) {
		Term[] terms = new Term[function.size()];
		
		for (int i=0; i < function.size(); i++) {
			terms[i] = (Term) function.getTerm(i).accept(this);
		}
		return new Predicate(function.functor(), terms);
	}	

	@Override
	public Object visit(AND and) {
		return new AND((Formula) and.left().accept(this), (Formula) and.right().accept(this));
	}

	@Override
	public Object visit(NOT not) {
		return new NOT((Formula) not.formula().accept(this));
	}

	@Override
	public Object visit(Goal goal) {
		return new Goal((Predicate) goal.formula().accept(this));
	}

	@Override
	public Object visit(ScopedGoal goal) {
		return new ScopedGoal(goal.scope(), (Goal) goal.formula().accept(this));
	}

	@Override
	public Object visit(Comparison comparison) {
		return new Comparison(comparison.operator(), (Term) comparison.left().accept(this), (Term) comparison.right().accept(this));
	}

	@Override
	public Object visit(ModuleTerm term) {
		return new ModuleTerm(term.module(), term.type(), (Predicate) term.method().accept(this), term.adaptor());
	}

	@Override
	public Object visit(ModuleFormula formula) {
		return new ModuleFormula(formula.module(), (Predicate) formula.predicate().accept(this), formula.adaptor());
	}

	@Override
	public Object visit(Performative performative) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(EISFormula eisFormula) {
		if (eisFormula.id() != null) {
			return new EISFormula((Term) eisFormula.id().accept(this), (Term) eisFormula.entity().accept(this), (Predicate) eisFormula.predicate().accept(this));
		} else if (eisFormula.entity() != null) {
			return new EISFormula((Term) eisFormula.entity().accept(this), (Predicate) eisFormula.predicate().accept(this));
		}
		return new EISFormula((Predicate) eisFormula.predicate().accept(this));
	}

	@Override
	public Object visit(ListTerm list) {
		Term[] terms = new Term[list.terms().length];
		for (int i=0;i<list.terms().length; i++) {
			terms[i] = (Term) list.get(i).accept(this);
		}
		return new ListTerm(terms);
	}

	@Override
	public Object visit(CartagoProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FormulaVariable variable) {
		return new FormulaVariable((Variable) variable.variable().accept(this));
	}

	@Override
	public Object visit(FormulaTerm term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(OR or) {
		return new OR((Formula) or.left().accept(this), (Formula) or.right().accept(this));
	}

	@Override
	public Object visit(Inference inference) {
		return new Inference((Predicate) inference.head().accept(this), (Formula) inference.body().accept(this));
	}

	@Override
	public Object visit(IsNull isNull) {
		return new IsNull((Term) isNull.formula().accept(this));
	}

	@Override
	public Object visit(AcreFormula formula) {
		return new AcreFormula((Term) formula.index().accept(this), (Term) formula.type().accept(this), (Term) formula.performative().accept(this),
				(Term) formula.cid().accept(this), (Formula) formula.content().accept(this));
	}

	@Override
	public Object visit(QueryTerm term) {
		return new QueryTerm((Formula) term.formula().accept(this));
	}

	@Override
	public Object visit(BracketFormula formula) {
		return new BracketFormula((BracketFormula) formula.formula().accept(this));
	}

	@Override
	public Object visit(Brackets term) {
		return new Brackets((Term) term.term().accept(this));
	}

	@Override
	public Object visit(ListSplitter term) {
		return new ListSplitter((Variable) term.head().accept(this), (Variable) term.tail().accept(this));
	}

	@Override
	public Object visit(Bind bind) {
		return new Bind((Variable) bind.variable().accept(this), (Term) bind.term().accept(this));
	}
}
