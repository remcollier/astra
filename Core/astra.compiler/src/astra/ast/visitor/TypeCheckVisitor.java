package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ClassDeclarationElement;
import astra.ast.core.IAction;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ImportElement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
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

/**
 * This class iterates through the parse tree adding types where necessary.
 * 
 * @author Rem Collier
 *
 */
public class TypeCheckVisitor implements IElementVisitor {
	private boolean skipOntology = false;

	@Override
	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		for (InferenceElement inference : element.getInferences()) {
			inference.accept(this, data);
		}
		
		for (RuleElement rule : element.getRules()) {
			rule.accept(this, data);
		}
		
		for (PlanElement plan : element.getPlans()) {
			plan.accept(this, data);
		}
		
		for (FunctionElement function: element.getFunctions()) {
			function.accept(this, data);
		}
		
		return null;
	}

	@Override
	public Object visit(InferenceElement element, Object data) throws ParseException {
		element.head().accept(this, data);
		element.body().accept(this, data);
		return null;
	}

	@Override
	public Object visit(PlanElement element, Object data) throws ParseException {
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(RuleElement element, Object data) throws ParseException {
		element.event().accept(this, data);
		element.context().accept(this, data);
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(FunctionElement element, Object data) throws ParseException {
		for (TRRuleElement rule : element.rules()) {
			rule.accept(this, data);
		}
		return null;
	}
	
	@Override
	public Object visit(TRRuleElement rule, Object data) throws ParseException {
		rule.formula().accept(this, data);
//		rule.action().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(FunctionCallAction action, Object data) throws ParseException {
		action.call().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(UpdateAction action, Object data) throws ParseException {
		action.call().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(BlockAction action, Object data) throws ParseException {
		for (IAction act : action.actions()) {
			act.accept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(TRModuleCallAction action, Object data) throws ParseException {
		action.method().accept(this, data);
		return null;
	}

	@Override
	public Object visit(UpdateEvent event, Object data) throws ParseException {
		event.content().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ModuleEvent event, Object data) throws ParseException {
//		event.event().accept(this, data);
		return null;
	}

	@Override
	public Object visit(MessageEvent event, Object data) throws ParseException {
		event.speechact().accept(this, data);
		event.sender().accept(this, data);
		event.content().accept(this, data);
		return null;
	}

	@Override
	public Object visit(BasicAcreEvent event, Object data)
			throws ParseException {
		event.type().accept(this, data);
		event.cid().accept(this, data);
		return null;
	}

	@Override
	public Object visit(AdvancedAcreEvent event, Object data)
			throws ParseException {
		event.type().accept(this, data);
		event.cid().accept(this, data);
		event.state().accept(this, data);
		event.length().accept(this, data);
		return null;
	}

	@Override
	public Object visit(GoalFormula formula, Object data) throws ParseException {
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
	public Object visit(AcreFormula formula, Object data) throws ParseException {
		return null;
	}

	@Override
	public Object visit(PredicateFormula formula, Object data) throws ParseException {
		if (!skipOntology && !((ComponentStore) data).signatures.contains(formula.toSignature()))
			throw new ParseException("Undeclared predicate formula used: " + formula, formula.start, formula.end);
		
//
//		for (ITerm term : formula.terms()) {
//			term.accept(this, data);
//		}
		return null;
	}

	@Override
	public Object visit(Function function, Object data) throws ParseException {
		for (ITerm term : function.terms()) {
			term.accept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(NOTFormula formula, Object data) throws ParseException {
		formula.formula().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(ListTerm term, Object data) throws ParseException {
		for (ITerm t : term.terms()) {
			t.accept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(DeclarationStatement statement, Object data)
			throws ParseException {
		if (statement.term() != null) statement.term().accept(this, data);
		return null;
	}

	@Override
	public Object visit(PlusPlusStatement statement, Object data)
			throws ParseException {
		return null;
	}

	@Override
	public Object visit(MinusMinusStatement statement, Object data)
			throws ParseException {
		return null;
	}

	@Override
	public Object visit(AssignmentStatement statement, Object data) throws ParseException {
		statement.term().accept(this, data);
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
	public Object visit(ModuleCallStatement statement, Object data) throws ParseException {
		return null;
	}

	@Override
	public Object visit(ModuleFormula formula, Object data) throws ParseException {
		skipOntology = true;	
		formula.method().accept(this, data);
		skipOntology = false;
		return null;
	}

	@Override
	public Object visit(PlanCallStatement statement, Object data) throws ParseException {
		return null;
	}
	
	@Override
	public Object visit(QueryStatement statement, Object data) throws ParseException {
		statement.formula().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(WhileStatement statement, Object data)
			throws ParseException {
		statement.guard().accept(this, data);
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ForEachStatement statement, Object data)
			throws ParseException {
		statement.guard().accept(this, data);
		statement.statement().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(ForAllStatement statement, Object data)
			throws ParseException {
		statement.variable().accept(this, data);
		statement.list().accept(this, data);
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(WhenStatement statement, Object data)
			throws ParseException {
		statement.guard().accept(this, data);
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(WaitStatement statement, Object data)
			throws ParseException {
		statement.guard().accept(this, data);
		return null;
	}

	@Override
	public Object visit(TRStatement statement, Object data)
			throws ParseException {
//		statement.function().accept(this, data);
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
	public Object visit(AcreStartStatement statement, Object data)
			throws ParseException {
		statement.protocol().accept(this, data);
		statement.receiver().accept(this, data);
		statement.performative().accept(this, data);
		statement.content().accept(this, data);
		statement.cid().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(AcreAdvanceStatement statement, Object data)
			throws ParseException {
		statement.performative().accept(this, data);
		statement.content().accept(this, data);
		statement.cid().accept(this, data);
		return null;
	}

	@Override
	public Object visit(InlineVariableDeclaration term, Object data) throws ParseException {
		return null;
	}

	@Override
	public Object visit(Operator term, Object data) throws ParseException {
//		System.out.println("operator: " + term.left().toString() + " / " + term.right().toString() + " has types: " + term.left().type() + " / "  + term.right().type());
		term.left().accept(this, data);
		term.right().accept(this, data);
		
//		System.out.println("types: " + term.left().type() + " / "  + term.right().type());
		term.updateType();
//		System.out.println("types: " + term.left().type() + " / "  + term.right().type() + " = " + term.type());
		return null;
	}

	@Override
	public Object visit(Brackets brackets, Object data) throws ParseException {
		brackets.contents().accept(this, data);
		return null;
	}

	@Override
	public Object visit(Variable term, Object data) throws ParseException {
		return null;
	}

	@Override
	public Object visit(ModuleTerm term, Object data) throws ParseException {
		return null;
	}

	@Override
	public Object visit(IfStatement statement, Object data)
			throws ParseException {
		statement.guard().accept(this, data);
		statement.ifStatement().accept(this, data);
		if (statement.elseStatement() != null) statement.elseStatement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ComparisonFormula formula, Object data)
			throws ParseException {
		formula.left().accept(this, data);
		formula.right().accept(this, data);
		return null;
	}

	@Override
	public Object visit(FormulaVariable formula, Object data)
			throws ParseException {
		return null;
	}

	@Override
	public Object visit(AndFormula formula, Object data) throws ParseException {
		formula.left().accept(this, data);
		formula.right().accept(this, data);
		return null;
	}

	@Override
	public Object visit(OrFormula formula, Object data) throws ParseException {
		formula.left().accept(this, data);
		formula.right().accept(this, data);
		return null;
	}

	@Override
	public Object visit(UpdateStatement statement, Object data)
			throws ParseException {
		statement.formula().accept(this, data);
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
	public Object visit(SendStatement statement, Object data)
			throws ParseException {
		statement.performative().accept(this, data);
		statement.sender().accept(this, data);
		if (statement.sender().type().type() == Token.STRING) {
			// do nothing
		} else if (statement.sender().type().type() == Token.LIST) {
			for (ITerm term : ((ListTerm)statement.sender()).terms()) {
				if (term.type().type() != Token.STRING) {
					throw new ParseException("Receiver term should be a string or list of strings", term);
				}
			}
		} else {
			throw new ParseException("Receiver term should be a string or list of strings", statement.sender());
		}
		
		statement.content().accept(this, data);
		
		return null;
	}

	@Override
	public Object visit(ScopedStatement statement, Object data)
			throws ParseException {
		statement.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(QueryTerm term, Object data) throws ParseException {
		term.formula().accept(this, data);
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
	public Object visit(TypesElement ontologyElement, Object data) throws ParseException {
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
	public Object visit(AcreCancelStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AcreConfirmCancelStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(AcreDenyCancelStatement statement, Object data) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal term, Object data) throws ParseException {
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
}
