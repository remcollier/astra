package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ClassDeclarationElement;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ImportElement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.definition.FormulaDefinition;
import astra.ast.element.FunctionElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.TypesElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.formula.AcreFormula;
import astra.ast.formula.AndFormula;
import astra.ast.formula.BindFormula;
import astra.ast.formula.BracketFormula;
import astra.ast.formula.CartagoFormula;
import astra.ast.formula.ComparisonFormula;
import astra.ast.formula.EISFormula;
import astra.ast.formula.FormulaVariable;
import astra.ast.formula.GoalFormula;
import astra.ast.formula.ModuleFormula;
import astra.ast.formula.NOTFormula;
import astra.ast.formula.OrFormula;
import astra.ast.formula.PredicateFormula;
import astra.ast.formula.ScopedGoalFormula;
import astra.ast.statement.AcreAdvanceStatement;
import astra.ast.statement.AcreCancelStatement;
import astra.ast.statement.AcreConfirmCancelStatement;
import astra.ast.statement.AcreDenyCancelStatement;
import astra.ast.statement.AcreStartStatement;
import astra.ast.statement.AssignmentStatement;
import astra.ast.statement.BlockStatement;
import astra.ast.statement.CartagoStatement;
import astra.ast.statement.DeclarationStatement;
import astra.ast.statement.EISStatement;
import astra.ast.statement.ForAllStatement;
import astra.ast.statement.ForEachStatement;
import astra.ast.statement.IfStatement;
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
import astra.ast.term.Brackets;
import astra.ast.term.Function;
import astra.ast.term.InlineVariableDeclaration;
import astra.ast.term.ListSplitterTerm;
import astra.ast.term.ListTerm;
import astra.ast.term.Literal;
import astra.ast.term.ModuleTerm;
import astra.ast.term.Operator;
import astra.ast.term.QueryTerm;
import astra.ast.term.Variable;
import astra.ast.tr.CartagoAction;
import astra.ast.tr.EISAction;
import astra.ast.tr.TRAction;
import astra.ast.type.BasicType;
import astra.ast.type.ObjectType;

/**
 * This class iterates through the parse tree adding types where necessary.
 * 
 * @author Rem Collier
 *
 */
public class GoalCheckVisitor extends AbstractVisitor {

	@Override
	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		for (InitialElement initial : element.getInitials()) {
			initial.accept(this, data);
		}
		
		for (RuleElement rule : element.getRules()) {
			rule.accept(this, data);
		}
		
		for (PlanElement plan : element.getPlans()) {
			plan.accept(this, data);
		}
		
		return null;
	}

	@Override
	public Object visit(PlanElement element, Object data) throws ParseException {
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(RuleElement element, Object data) throws ParseException {
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(GoalFormula formula, Object data) throws ParseException {
		((ComponentStore) data).checkForEvent(formula);
		return null;
	}

	@Override
	public Object visit(BracketFormula formula, Object data)
			throws ParseException {
		formula.formula().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ScopedGoalFormula formula, Object data) throws ParseException {
		formula.goal().accept(this, data);
		return null;
	}

	@Override
	public Object visit(SynchronizedBlockStatement statement, Object data) throws ParseException {
		for (IStatement s: statement.statements()) {
			s.accept(this, data);
		}
		return null;
	}
	
	@Override
	public Object visit(BlockStatement statement, Object data) throws ParseException {
		for (IStatement s: statement.statements()) {
			s.accept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(WhileStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ForEachStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(ForAllStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(WhenStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(TryRecoverStatement statement, Object data)
			throws ParseException {
		statement.tryStatement().accept(this, data);
		statement.recoverStatement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(IfStatement statement, Object data)
			throws ParseException {
		statement.ifStatement().accept(this, data);
		if (statement.elseStatement() != null) statement.elseStatement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(SpawnGoalStatement statement, Object data)
			throws ParseException {
		statement.goal().accept(this, data);
		return null;
	}

	@Override
	public Object visit(SubGoalStatement statement, Object data)
			throws ParseException {
		statement.goal().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ScopedStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(InitialElement element, Object data) throws ParseException {
		element.formula().accept(this, data);
		return null;
	}
}
