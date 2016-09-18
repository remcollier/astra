package astra.ast.core;

import astra.ast.definition.FormulaDefinition;
import astra.ast.element.FunctionElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PackageElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
import astra.ast.element.TypesElement;
import astra.ast.event.AdvancedAcreEvent;
import astra.ast.event.BasicAcreEvent;
import astra.ast.event.MessageEvent;
import astra.ast.event.ModuleEvent;
import astra.ast.event.UpdateEvent;
import astra.ast.formula.AcreFormula;
import astra.ast.formula.AndFormula;
import astra.ast.formula.BindFormula;
import astra.ast.formula.BracketFormula;
import astra.ast.formula.ComparisonFormula;
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
import astra.ast.statement.DeclarationStatement;
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
import astra.ast.tr.BlockAction;
import astra.ast.tr.CartagoAction;
import astra.ast.tr.FunctionCallAction;
import astra.ast.tr.TRAction;
import astra.ast.tr.TRModuleCallAction;
import astra.ast.tr.TRRuleElement;
import astra.ast.tr.UpdateAction;
import astra.ast.type.BasicType;
import astra.ast.type.ObjectType;

public interface IElementVisitor {
	// Elements
	public Object visit(ASTRAClassElement element, Object data) throws ParseException;
	public Object visit(PackageElement element, Object data) throws ParseException;
	public Object visit(ImportElement element, Object data);
	public Object visit(ClassDeclarationElement element, Object data) throws ParseException;
	public Object visit(InitialElement element, Object data) throws ParseException;
	public Object visit(ModuleElement element, Object data) throws ParseException;
	public Object visit(PlanElement element, Object data) throws ParseException;
	public Object visit(RuleElement element, Object data) throws ParseException;
	public Object visit(InferenceElement element, Object data) throws ParseException;
	public Object visit(FunctionElement element, Object data) throws ParseException;
	public Object visit(TypesElement element, Object data) throws ParseException;
	
	// TR Components
	public Object visit(TRRuleElement element, Object data) throws ParseException;
	public Object visit(TRModuleCallAction action, Object data) throws ParseException;
	public Object visit(CartagoAction action, Object data) throws ParseException;
	public Object visit(TRAction action, Object data) throws ParseException;
	public Object visit(BlockAction action, Object data) throws ParseException;
	public Object visit(FunctionCallAction action, Object data) throws ParseException;
	public Object visit(UpdateAction updateAction, Object data) throws ParseException;
	
	// Events
	public Object visit(UpdateEvent event, Object data) throws ParseException;
	public Object visit(MessageEvent event, Object data) throws ParseException;
	public Object visit(BasicAcreEvent event, Object data) throws ParseException;
	public Object visit(AdvancedAcreEvent event, Object data) throws ParseException;
	public Object visit(ModuleEvent moduleEvent, Object data) throws ParseException;
	
	// Formulae
	public Object visit(GoalFormula formula, Object data) throws ParseException;
	public Object visit(PredicateFormula formula, Object data) throws ParseException;
	public Object visit(NOTFormula formula, Object data) throws ParseException;
	public Object visit(ComparisonFormula formula, Object data) throws ParseException;
	public Object visit(AndFormula formula, Object data) throws ParseException;
	public Object visit(OrFormula formula, Object data) throws ParseException;
	public Object visit(FormulaVariable formula, Object data) throws ParseException;
	public Object visit(ModuleFormula formula, Object data) throws ParseException;
	public Object visit(ScopedGoalFormula formula, Object data) throws ParseException;
	public Object visit(AcreFormula formula, Object data) throws ParseException;
	public Object visit(BracketFormula formula, Object data) throws ParseException;

	// Statements
	public Object visit(DeclarationStatement statement, Object data) throws ParseException;
	public Object visit(AssignmentStatement statement, Object data) throws ParseException;
	public Object visit(BlockStatement statement, Object data) throws ParseException;
	public Object visit(ModuleCallStatement statement, Object data) throws ParseException;
	public Object visit(PlanCallStatement statement, Object data) throws ParseException;
	public Object visit(SendStatement statement, Object data) throws ParseException;
	public Object visit(IfStatement statement, Object data) throws ParseException;
	public Object visit(UpdateStatement statement, Object data) throws ParseException;
	public Object visit(SpawnGoalStatement statement, Object data) throws ParseException;
	public Object visit(SubGoalStatement statement, Object data) throws ParseException;
	public Object visit(QueryStatement statement, Object data) throws ParseException;
	public Object visit(WhileStatement statement, Object data) throws ParseException;
	public Object visit(ForEachStatement statement, Object data) throws ParseException;
	public Object visit(WhenStatement statement, Object data) throws ParseException;
	public Object visit(WaitStatement statement, Object data) throws ParseException;
	public Object visit(TryRecoverStatement statement, Object data) throws ParseException;
	public Object visit(TRStatement statement, Object data) throws ParseException;
	public Object visit(SynchronizedBlockStatement statement, Object data) throws ParseException;
	public Object visit(ScopedStatement statement, Object data) throws ParseException;
	public Object visit(AcreStartStatement statement, Object data) throws ParseException;
	public Object visit(AcreAdvanceStatement statement, Object data) throws ParseException;
	public Object visit(AcreCancelStatement statement, Object data) throws ParseException;
	public Object visit(AcreConfirmCancelStatement statement, Object data) throws ParseException;
	public Object visit(AcreDenyCancelStatement statement, Object data) throws ParseException;
	public Object visit(ForAllStatement statement, Object data) throws ParseException;
	public Object visit(PlusPlusStatement plusPlusStatement, Object data) throws ParseException;
	public Object visit(MinusMinusStatement minusMinusStatement, Object data) throws ParseException;

	// Terms
	public Object visit(InlineVariableDeclaration term, Object data) throws ParseException;
	public Object visit(Literal term, Object data) throws ParseException;
	public Object visit(Operator term, Object data) throws ParseException;
	public Object visit(Variable term, Object data) throws ParseException;
	public Object visit(ModuleTerm term, Object data) throws ParseException;
	public Object visit(ListTerm listTerm, Object data) throws ParseException;
	public Object visit(QueryTerm queryTerm, Object data) throws ParseException;
	public Object visit(Brackets brackets, Object data) throws ParseException;
	public Object visit(Function function, Object data) throws ParseException;
	
	// Types
	public Object visit(BasicType basicType, Object data) throws ParseException;
	public Object visit(ObjectType objectType, Object data) throws ParseException;
	
	// Ontology Stuff
	public Object visit(FormulaDefinition formulaDefinition, Object data) throws ParseException;
	public Object visit(ListSplitterTerm term, Object data) throws ParseException;
	public Object visit(BindFormula formula, Object data) throws ParseException;
}
