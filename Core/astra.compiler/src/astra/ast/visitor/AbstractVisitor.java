package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ClassDeclarationElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ImportElement;
import astra.ast.core.ParseException;
import astra.ast.definition.FormulaDefinition;
import astra.ast.element.FunctionElement;
import astra.ast.element.GRuleElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.element.TypesElement;
import astra.ast.event.MessageEvent;
import astra.ast.event.ModuleEvent;
import astra.ast.event.UpdateEvent;
import astra.ast.formula.AndFormula;
import astra.ast.formula.BindFormula;
import astra.ast.formula.BracketFormula;
import astra.ast.formula.ComparisonFormula;
import astra.ast.formula.FormulaVariable;
import astra.ast.formula.GoalFormula;
import astra.ast.formula.IsDoneFormula;
import astra.ast.formula.ModuleFormula;
import astra.ast.formula.NOTFormula;
import astra.ast.formula.OrFormula;
import astra.ast.formula.PredicateFormula;
import astra.ast.formula.ScopedGoalFormula;
import astra.ast.statement.AssignmentStatement;
import astra.ast.statement.BlockStatement;
import astra.ast.statement.DeclarationStatement;
import astra.ast.statement.ForAllStatement;
import astra.ast.statement.ForEachStatement;
import astra.ast.statement.IfStatement;
import astra.ast.statement.MaintainBlockStatement;
import astra.ast.statement.MinusMinusStatement;
import astra.ast.statement.ModuleCallStatement;
import astra.ast.statement.PlanCallStatement;
import astra.ast.statement.PlusPlusStatement;
import astra.ast.statement.QueryStatement;
import astra.ast.statement.ScopedStatement;
import astra.ast.statement.SendStatement;
import astra.ast.statement.SpawnGoalStatement;
import astra.ast.statement.SubGoalStatement;
import astra.ast.statement.SynchronizedBlockStatement;
import astra.ast.statement.TRStatement;
import astra.ast.statement.TryRecoverStatement;
import astra.ast.statement.UpdateStatement;
import astra.ast.statement.WaitStatement;
import astra.ast.statement.WhenStatement;
import astra.ast.statement.WhileStatement;
import astra.ast.term.AtIndexTerm;
import astra.ast.term.Brackets;
import astra.ast.term.CountTerm;
import astra.ast.term.Function;
import astra.ast.term.HeadTerm;
import astra.ast.term.InlineVariableDeclaration;
import astra.ast.term.ListSplitterTerm;
import astra.ast.term.ListTerm;
import astra.ast.term.Literal;
import astra.ast.term.ModuleTerm;
import astra.ast.term.Operator;
import astra.ast.term.QueryTerm;
import astra.ast.term.TailTerm;
import astra.ast.term.Variable;
import astra.ast.tr.BlockAction;
import astra.ast.tr.CartagoAction;
import astra.ast.tr.FunctionCallAction;
import astra.ast.tr.TRAction;
import astra.ast.tr.TRModuleCallAction;
import astra.ast.tr.TRRuleElement;
import astra.ast.tr.UpdateAction;
import astra.ast.type.BasicType;
import astra.ast.type.ObjectType;

public class AbstractVisitor implements IElementVisitor {

	@Override
	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PackageElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ImportElement element, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ClassDeclarationElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(InitialElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ModuleElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PlanElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RuleElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(InferenceElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FunctionElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TypesElement ontologyElement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TRRuleElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TRModuleCallAction action, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CartagoAction action, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TRAction action, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BlockAction action, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FunctionCallAction action, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UpdateAction updateAction, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UpdateEvent event, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MessageEvent event, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object visit(ModuleEvent moduleEvent, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GoalFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PredicateFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NOTFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ComparisonFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AndFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(OrFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FormulaVariable formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ModuleFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ScopedGoalFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BracketFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclarationStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AssignmentStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BlockStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ModuleCallStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PlanCallStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SendStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IfStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UpdateStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SpawnGoalStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SubGoalStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(QueryStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(WhileStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ForEachStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(WhenStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(WaitStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TryRecoverStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TRStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SynchronizedBlockStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MaintainBlockStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ScopedStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ForAllStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(PlusPlusStatement plusPlusStatement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MinusMinusStatement minusMinusStatement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(InlineVariableDeclaration term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Operator term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Variable term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ModuleTerm term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ListTerm listTerm, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(QueryTerm queryTerm, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Brackets brackets, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Function function, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BasicType basicType, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ObjectType objectType, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FormulaDefinition formulaDefinition, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ListSplitterTerm term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BindFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CountTerm term, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(IsDoneFormula formula, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GRuleElement element, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(HeadTerm headTerm, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TailTerm headTerm, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AtIndexTerm atIndexTerm, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}
}
