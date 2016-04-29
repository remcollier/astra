package astra.util;

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
import astra.term.Variable;

public interface LogicVisitor {
	public Object visit(Variable variable);
	public Object visit(Primitive<?> primitive);
	public Object visit(Operator operator);
	
	public Object visit(Predicate predicate);
	public Object visit(AND and);
	public Object visit(NOT not);
	public Object visit(Goal goal);
	public Object visit(Comparison comparison);
	public Object visit(ModuleTerm term);
	public Object visit(ModuleFormula formula);
	public Object visit(Performative performative);
	public Object visit(EISFormula eisFormula);
	public Object visit(ListTerm list);
	public Object visit(CartagoProperty property);
	public Object visit(FormulaVariable variable);
	public Object visit(FormulaTerm term);
	public Object visit(OR or);
	public Object visit(Inference inference);
	public Object visit(IsNull isNull);
	public Object visit(ScopedGoal goal);
	public Object visit(AcreFormula formula);
	public Object visit(QueryTerm term);
	public Object visit(BracketFormula formula);
	public Object visit(Brackets term);
	public Object visit(Funct function);
	public Object visit(ListSplitter term);
	public Object visit(Bind bind);

//	public Object visit(Performative performative);

}
