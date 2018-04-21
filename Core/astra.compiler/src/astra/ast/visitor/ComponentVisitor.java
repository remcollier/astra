package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IAction;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ILanguageDefinition;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.element.FunctionElement;
import astra.ast.element.GRuleElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.ModuleElement;
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
import astra.ast.formula.MethodSignature;
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
import astra.ast.term.Brackets;
import astra.ast.term.CountTerm;
import astra.ast.term.Function;
import astra.ast.term.InlineVariableDeclaration;
import astra.ast.term.ListSplitterTerm;
import astra.ast.term.ListTerm;
import astra.ast.term.ModuleTerm;
import astra.ast.term.Operator;
import astra.ast.term.QueryTerm;
import astra.ast.term.Variable;
import astra.ast.tr.BlockAction;
import astra.ast.tr.FunctionCallAction;
import astra.ast.tr.TRModuleCallAction;
import astra.ast.tr.TRRuleElement;
import astra.ast.tr.UpdateAction;

/**
 * This class iterates through the parse tree adding types where necessary.
 * 
 * @author Rem Collier
 *
 */
public class ComponentVisitor extends AbstractVisitor {
	private IJavaHelper helper;
	private ComponentStore store;
	
	public ComponentVisitor(IJavaHelper helper, ComponentStore store) {
		this.helper = helper;
		this.store = store;
	}

	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		helper.setup(element.packageElement(), element.imports());
		
		// Check each module exists and store a reference to the module in the component store
		for (ModuleElement module : element.getModules()) {
			String qualifiedName = helper.resolveModule(module.className());
			if (qualifiedName == null)
				throw new ParseException("Unknown module declaration: " + module.className(), module);
			module.setQualifiedName(qualifiedName);
			store.modules.put(module.name(), module);
		}

		// Record any types that you find
		for (TypesElement type : element.getOntologies()) {
			if (store.types.contains(type.name())) 
				throw new ParseException("Duplicate Ontology: " + type.name(), 
						type.start, type.end);

			for (ILanguageDefinition definition : type.definitions()) {
//				System.out.println("definition: " + definition.toSignature());
				if (store.signatures.contains(definition.toSignature())) 
					throw new ParseException("Conflict in ontology: " + type.name() + " for term: " + definition, 
							type.start, type.end);
				
				store.signatures.add(definition.toSignature());
			}
			
			// Ontology was loaded without conflict so we are okay...
			store.types.add(type.name());
		}
		
		// Record the event part of any events that you find.
		for (RuleElement rule : element.getRules()) {
			try {
//				rule.accept(this, store);
				String signature = rule.event().toSignature();
//				System.out.println("signature: " + signature);
				if (!store.events.contains(signature)) store.events.add(signature);
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				throw new ParseException("Illegal variable use in: " + rule.event(), rule.event());
			}
		}
		
		for (GRuleElement rule : element.getGRules()) {
			try {
//				rule.accept(this, store);
				String signature = rule.event().toSignature();
//				System.out.println("grule signature: " + signature);
				if (!store.events.contains(signature)) store.events.add(signature);
			} catch (NullPointerException npe) {
				npe.printStackTrace();
				throw new ParseException("Illegal variable use in: " + rule.event(), rule.event());
			}
		}

		// Record all partial plans that you find
		for (PlanElement plan : element.getPlans()) {
			String signature = plan.signature().toSignature();
			if (!store.plans.contains(signature)) store.plans.add(signature);
		}

		// Now do the type checking...
		for (InferenceElement inference : element.getInferences()) {
			inference.accept(this, new VariableTypeStack());
		}
		
		for (RuleElement rule : element.getRules()) {
			rule.accept(this, new VariableTypeStack());
		}
		
		for (GRuleElement rule : element.getGRules()) {
			rule.accept(this, new VariableTypeStack());
		}

		for (PlanElement plan : element.getPlans()) {
			plan.accept(this, new VariableTypeStack());
		}
		
		for (FunctionElement function: element.getFunctions()) {
			function.accept(this, new VariableTypeStack());
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
		element.signature().accept(this, data);
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(RuleElement element, Object data) throws ParseException {
//		System.out.println("rule: " + element);
		element.event().accept(this, data);
		element.context().accept(this, data);
		element.statement().accept(this, data);
		return null;
	}

	@Override
	public Object visit(GRuleElement element, Object data) throws ParseException {
//		System.out.println("grule: " + element);
		element.event().accept(this, data);
		element.context().accept(this, data);
		element.dropCondition().accept(this, data);
		element.statement().accept(this, data);
		
		// NOT SURE WHAT THIS WILL DO - WILL IT WORK?
		VariableTypeStack stk = (VariableTypeStack) data;
		for(RuleElement rule : element.rules()) {
			stk.addScope();
			rule.accept(this, data);
			stk.removeScope();
		}
		return null;
	}

	@Override
	public Object visit(FunctionElement element, Object data) throws ParseException {
		element.signature().accept(this, data);
//		System.out.println("element: " + element.signature());
//		System.out.println("stk: " + data);
		for (TRRuleElement rule : element.rules()) {
			VariableTypeStack stk = (VariableTypeStack) data;
			stk.addScope();
			rule.accept(this, stk);
			stk.removeScope();
		}
		return null;
	}
	
	@Override
	public Object visit(TRRuleElement rule, Object data) throws ParseException {
//		System.out.println("\trule: " + rule);
		rule.formula().accept(this, data);
		rule.action().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(FunctionCallAction action, Object data) throws ParseException {
		((VariableTypeStack) data).addScope();
		action.call().accept(this, data);
		((VariableTypeStack) data).removeScope();
		return null;
	}
	
	@Override
	public Object visit(UpdateAction action, Object data) throws ParseException {
		((VariableTypeStack) data).addScope();
		action.call().accept(this, data);
		((VariableTypeStack) data).removeScope();
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
		((VariableTypeStack) data).addScope();
		action.method().accept(this, data);
		((VariableTypeStack) data).removeScope();
		return null;
	}

	@Override
	public Object visit(UpdateEvent event, Object data) throws ParseException {
		event.content().accept(this, data);
		return null;
	}

	@Override
	public Object visit(ModuleEvent event, Object data) throws ParseException {
		event.event().accept(this, data);
		return null;
	}

	@Override
	public Object visit(MessageEvent event, Object data) throws ParseException {
		event.speechact().accept(this, data);
		event.sender().accept(this, data);
		event.content().accept(this, data);
		if (event.params() != null) event.params().accept(this, data);
		return null;
	}

	@Override
	public Object visit(GoalFormula formula, Object data) throws ParseException {
		formula.predicate().accept(this, data);
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
	public Object visit(PredicateFormula formula, Object data) throws ParseException {
//		System.out.println("------------------------------------------------------");
//		System.out.println("formula: " + formula);
		for (ITerm term : formula.terms()) {
			term.accept(this, data);
		}
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
		((VariableTypeStack) data).addScope();
		formula.formula().accept(this, data);
		((VariableTypeStack) data).removeScope();		
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
		if (((VariableTypeStack) data).exists(statement.variable())) {
			throw new ParseException(
					"Duplicate variable declaration: " + statement.variable(), 
					statement);
		}
		if (statement.term() != null) {
			statement.term().accept(this, data);

			if (!statement.type().equals(statement.term().type())) {
				throw new ParseException(
						"Type mismatch: expected an expression of type '" + Token.toTypeString(statement.type().type()) + 
						"' but got an expression of type '" + Token.toTypeString(statement.term().type().type()) + "'", 
						statement);
			}
		}	
		((VariableTypeStack) data).addVariable(statement.variable(), statement.type());
		return null;
	}

	@Override
	public Object visit(PlusPlusStatement statement, Object data)
			throws ParseException {
		IType type = ((VariableTypeStack) data).getType(statement.variable());
		if (type == null) {
			throw new ParseException(
					"Undeclared variable: " + statement.variable(), 
					statement);
		}

		if (type.type() != Token.INTEGER) {
			throw new ParseException(
					"Type mismatch: variable must be integer type but got: '" + Token.toTypeString(statement.type().type()) + "'", 
					statement);
		}
		statement.setType(type);
		return null;
	}

	@Override
	public Object visit(MinusMinusStatement statement, Object data)
			throws ParseException {
		IType type = ((VariableTypeStack) data).getType(statement.variable());
		if (type == null) {
			throw new ParseException(
					"Undeclared variable: " + statement.variable(), 
					statement);
		}

		if (type.type() != Token.INTEGER) {
			throw new ParseException(
					"Type mismatch: variable must be integer type but got: '" + Token.toTypeString(statement.type().type()) + "'", 
					statement);
		}
		statement.setType(type);
		return null;
	}

	@Override
	public Object visit(AssignmentStatement statement, Object data) throws ParseException {
		IType type = ((VariableTypeStack) data).getType(statement.variable());
		if (type == null) {
			throw new ParseException("Undeclared variable: " + statement.variable(), statement);
		}
		statement.setType(type);
		statement.term().accept(this, data);
		return null;
	}

	@Override
	public Object visit(SynchronizedBlockStatement statement, Object data) throws ParseException {
		((VariableTypeStack) data).addScope();
		for (IStatement s: statement.statements()) {
			s.accept(this, data);
		}
		((VariableTypeStack) data).removeScope();
		return null;
	}
	
	@Override
	public Object visit(MaintainBlockStatement statement, Object data) throws ParseException {
		statement.formula().accept(this, data);
		((VariableTypeStack) data).addScope();
		for (IStatement s: statement.statements()) {
			s.accept(this, data);
		}
		((VariableTypeStack) data).removeScope();
		return null;
	}
	
	@Override
	public Object visit(BlockStatement statement, Object data) throws ParseException {
		((VariableTypeStack) data).addScope();
		for (IStatement s: statement.statements()) {
			s.accept(this, data);
		}
		((VariableTypeStack) data).removeScope();
		return null;
	}

	@Override
	public Object visit(ModuleCallStatement statement, Object data) throws ParseException {
		statement.method().accept(this, data);
		
		ModuleElement element = store.modules.get(statement.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + statement.module(), statement);
		}

		MethodSignature signature = new MethodSignature(statement.method(), IJavaHelper.ACTION);
		if (!helper.validate(element.className(), signature)) {
			if (!helper.hasAutoAction(element.className())) {
				throw new ParseException("Could not find matching module call method: " + statement.toString(), statement);
			}
		}
		
		return null;
	}

	@Override
	public Object visit(ModuleFormula formula, Object data) throws ParseException {
		formula.method().accept(this, data);
		
		ModuleElement element = store.modules.get(formula.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + formula.module(), formula);
		}

		MethodSignature signature = new MethodSignature((PredicateFormula) formula.method(), IJavaHelper.FORMULA);
		if (!helper.validate(element.className(), signature))
			if (!helper.hasAutoFormula(element.className())) {
				throw new ParseException("Could not match formula to a method: " + formula.method() + " for module: "+formula.module(), formula);
			} else {
				System.out.println("AutoFormula: " + element.className()+"."+signature);
				// Should do a check on the types...
			}

		return null;
	}

	@Override
	public Object visit(PlanCallStatement statement, Object data) throws ParseException {
		statement.call().accept(this, data);
		
		if (!store.plans.contains(statement.call().toSignature())) {
			throw new ParseException("[TypeVisitor3] Could not find plan to match plan call: " + statement.toString(), statement);
		}
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
		((VariableTypeStack) data).addScope();
		statement.guard().accept(this, data);
		statement.statement().accept(this, data);
		((VariableTypeStack) data).removeScope();
		return null;
	}
	
	@Override
	public Object visit(ForAllStatement statement, Object data)
			throws ParseException {
		((VariableTypeStack) data).addScope();
		statement.variable().accept(this, data);
		statement.list().accept(this, data);
		statement.statement().accept(this, data);
		((VariableTypeStack) data).removeScope();
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
		if (statement.function() != null) {
			statement.function().accept(this, data);
		}
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
	public Object visit(InlineVariableDeclaration term, Object data) throws ParseException {
		if (((VariableTypeStack) data).exists(term.identifier())) {
			throw new ParseException("Duplicate variable declaration: " + term.identifier(), term);
		}
//		System.out.println("id: "+term.identifier());
//		System.out.println("type: "+term.type());
		((VariableTypeStack) data).addVariable(term.identifier(), term.type());
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
		brackets.updateType();
		return null;
	}

	@Override
	public Object visit(Variable term, Object data) throws ParseException {
		IType type = ((VariableTypeStack) data).getType(term.identifier());
		if (type == null) {
			throw new ParseException("Undeclared variable: " + term.identifier(), term);
		}
		term.setType(type);
		return null;
	}

	@Override
	public Object visit(ModuleTerm term, Object data) throws ParseException {
		term.method().accept(this, data);
		
		ModuleElement element = store.modules.get(term.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + term.module(), term);
		}
		
		MethodSignature signature = new MethodSignature(term.method(), IJavaHelper.TERM);
//		System.out.println("signature: " + signature);
		if (!helper.validate(element.className(), signature))
			throw new ParseException("Could not match term to a method: " + term.toString() + " for module: "+term.module(), term);

		IType type = helper.getType(element.className(), signature);
		if (type == null) {
			throw new ParseException("Could not find matching module method: " + term.toString(), term);
		}
		term.setType(type);
		return null;
	}

	@Override
	public Object visit(IfStatement statement, Object data)
			throws ParseException {
		((VariableTypeStack) data).addScope();
		statement.guard().accept(this, data);
		statement.ifStatement().accept(this, data);
		if (statement.elseStatement() != null) statement.elseStatement().accept(this, data);
		((VariableTypeStack) data).removeScope();
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
		if (((VariableTypeStack) data).exists(formula.identifier())) {
			return null;
//			throw new ParseException("Duplicate variable declaration: " + formula.identifier(), formula);
		}
//		System.out.println("adding formula variable: " + formula.identifier() + " / type: " + formula.type());
		((VariableTypeStack) data).addVariable(formula.identifier(), formula.type());
		
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
//		System.out.println("Here: "+ statement);
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
		statement.content().accept(this, data);
		if (statement.params() != null) statement.params().accept(this, data);
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
	public Object visit(ListSplitterTerm term, Object data) throws ParseException {
		term.head().accept(this, data);
		term.tail().accept(this, data);
		return null;
	}

	@Override
	public Object visit(BindFormula formula, Object data) throws ParseException {
		formula.variable().accept(this, data);
		formula.term().accept(this, data);
		return null;
	}
	
	@Override
	public Object visit(CountTerm term, Object data) throws ParseException {
		term.term().accept(this, data);
		return null;
	}
}
