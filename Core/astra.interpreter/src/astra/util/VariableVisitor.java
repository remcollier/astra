package astra.util;

import java.util.HashSet;
import java.util.Set;

import astra.cartago.CartagoProperty;
import astra.eis.EISFormula;
import astra.formula.AND;
import astra.formula.AcreFormula;
import astra.formula.Bind;
import astra.formula.BracketFormula;
import astra.formula.Comparison;
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

public class VariableVisitor implements LogicVisitor {
	Set<Variable> variables = new HashSet<Variable>();

	public Set<Variable> variables() {
		return variables;
	}
	
	@Override
	public Object visit(Variable variable) {
		variables.add(variable);
		return null;
	}

	@Override
	public Object visit(Primitive<?> term) {
		return null;
	}

	@Override
	public Object visit(Predicate predicate) {
		for (Term term : predicate.terms()) {
			term.accept(this);
		}
		return null;
	}
	
	@Override
	public Object visit(Funct function) {
		for (int i=0; i < function.size(); i++) {
			function.getTerm(i).accept(this);
		}
		return null;
	}	

	@Override
	public Object visit(Operator operator) {
		operator.left().accept(this);
		operator.right().accept(this);
		return null;
	}

	@Override
	public Object visit(Comparison comparison) {
		comparison.left().accept(this);
		comparison.right().accept(this);
		return null;
	}

	@Override
	public Object visit(AND and) {
		and.left().accept(this);
		and.right().accept(this);
		return null;
	}

	@Override
	public Object visit(OR or) {
		or.left().accept(this);
		or.right().accept(this);
		return null;
	}

	@Override
	public Object visit(NOT not) {
		not.formula().accept(this);
		return null;
	}

	@Override
	public Object visit(Goal goal) {
		goal.formula().accept(this);
		return null;
	}

	@Override
	public Object visit(ScopedGoal goal) {
		goal.formula().accept(this);
		return null;
	}

	@Override
	public Object visit(ModuleTerm term) {
		term.method().accept(this);
		return null;
	}

	@Override
	public Object visit(ModuleFormula formula) {
		return formula.predicate().accept(this);
	}

	@Override
	public Object visit(Performative performative) {
		return null;
	}

	@Override
	public Object visit(EISFormula formula) {
		if (formula.id() != null) formula.id().accept(this);
		formula.predicate().accept(this);
		return null;
	}

	@Override
	public Object visit(ListTerm list) {
		try {
			for (Term term : list.terms()) {
				term.accept(this);
			}
		} catch (Throwable th) {
			System.out.println("list: " + list);
		}
		return null;
	}

	@Override
	public Object visit(CartagoProperty property) {
		property.target().accept(this);
		property.content().accept(this);
		return null;
	}

	@Override
	public Object visit(FormulaVariable variable) {
		return variable.variable().accept(this);
	}

	@Override
	public Object visit(FormulaTerm formula) {
		if (formula.value() != null) formula.value().accept(this);
		return null;
	}	

	@Override
	public Object visit(Inference inference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IsNull isNull) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AcreFormula formula) {
		formula.cid().accept(this);
		formula.index().accept(this);
		formula.type().accept(this);
		formula.performative().accept(this);
		formula.content().accept(this);
		return null;
	}

	@Override
	public Object visit(QueryTerm term) {
		term.formula().accept(this);
		return null;
	}

	@Override
	public Object visit(BracketFormula formula) {
		formula.formula().accept(this);
		return null;
	}

	@Override
	public Object visit(Brackets term) {
		term.term().accept(this);
		return null;
	}

	@Override
	public Object visit(ListSplitter term) {
		term.head().accept(this);
		term.tail().accept(this);
		return null;
	}

	@Override
	public Object visit(Bind bind) {
		bind.variable().accept(this);
		bind.term().accept(this);
		return null;
	}
}
