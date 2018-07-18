package astra.ast.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.AbstractElement;
import astra.ast.core.IAction;
import astra.ast.core.IJavaHelper;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ImportElement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.element.FunctionElement;
import astra.ast.element.GRuleElement;
import astra.ast.element.InferenceElement;
import astra.ast.element.InitialElement;
import astra.ast.element.ModuleElement;
import astra.ast.element.PlanElement;
import astra.ast.element.RuleElement;
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

public class CodeGeneratorVisitor extends AbstractVisitor {
	private StringBuffer code = new StringBuffer();
	private String qualifiedName, pkg, fullName;
	private ComponentStore store;
	
	static Map<String, String> primitiveMap = new HashMap<String, String>();
	static {
		primitiveMap.put("int", "java.lang.Integer");
		primitiveMap.put("long", "java.lang.Long");
		primitiveMap.put("float", "java.lang.Float");
		primitiveMap.put("double", "java.lang.Double");
		primitiveMap.put("char", "java.lang.Character");
		primitiveMap.put("boolean", "java.lang.Boolean");
		primitiveMap.put("list", "astra.term.List");
	}
	protected Map<String, String> modules = new HashMap<String, String>();
	protected IJavaHelper helper;
	protected String astraClassName;
	
	public CodeGeneratorVisitor(IJavaHelper helper, ComponentStore store) {
		this.helper = helper;
		this.store = store;
	}

	public String toString() {
		return code.toString();
	}

	@Override
	public Object visit(ASTRAClassElement element, Object data) throws ParseException {
		helper.setup(element.packageElement(), element.imports());
		if (element.packageElement().packageName().equals("")) {
			astraClassName = element.getClassDeclaration().name();
		} else {
			astraClassName = element.packageElement().packageName() + "." + element.getClassDeclaration().name();
		}
		
		for (ModuleElement module : element.getModules()) {
			if (helper.resolveModule(module.className()) == null) {
				throw new ParseException("Unknown module declaration: " + module.className(), module);
			}
			
			if (modules.containsKey(module.name())) {
				throw new ParseException("Duplicate module name: " + module.name(), module);
			}
			modules.put(module.name(), module.className());
		}

		pkg = "";
		fullName = "";
		if (!element.packageElement().packageName().equals("")) {
			pkg = element.packageElement().packageName();
			fullName = pkg + ".";
			code.append("package " + pkg + ";\n");
		}

		code.append("/**\n").append(" * GENERATED CODE - DO NOT CHANGE\n")
				.append(" */\n\n");

		code.append("import astra.core.*;\n")
				.append("import astra.execution.*;\n")
				.append("import astra.event.*;\n")
				.append("import astra.messaging.*;\n")
				.append("import astra.formula.*;\n")
				.append("import astra.lang.*;\n")
				.append("import astra.statement.*;\n")
				.append("import astra.term.*;\n")
				.append("import astra.type.*;\n")
				.append("import astra.tr.*;\n")
				.append("import astra.reasoner.util.*;\n\n");

		for (ImportElement e : element.imports()) {
			code.append("import ").append(e.name()).append(";\n");
		}
		code.append("\n");
		qualifiedName = element.getClassDeclaration().name();
		fullName += qualifiedName;
		code.append("public class " + qualifiedName + " extends ASTRAClass {\n")
				.append("\tpublic " + qualifiedName + "() {\n");

		// add parent classes here
		code.append("\t\tsetParents(new Class[] {");
		boolean first = true;
		for (String parent : element.getClassDeclaration().parents()) {
			if (first)
				first = false;
			else
				code.append(",");
			code.append(parent + ".class");
		}
		code.append("});\n");

		for (InferenceElement inference : element.getInferences()) {
			inference.accept(this, "\t\t");
		}

		for (RuleElement rule : element.getRules()) {
			rule.accept(this, "\t\t");
		}

		for (GRuleElement rule : element.getGRules()) {
			rule.accept(this, "\t\t");
		}

		for (PlanElement plan : element.getPlans()) {
			plan.accept(this, "\t\t");
		}

		for (FunctionElement function : element.getFunctions()) {
			function.accept(this, "\t\t");
		}

		code.append("\t}\n\n").append(
				"\tpublic void initialize(astra.core.Agent agent) {\n");

		// Create Sensor Adaptors...
		for (ModuleElement module : element.getModules()) {
			List<String> sensors = helper.getSensors(module.qualifiedName());
			if (!sensors.isEmpty()) {
				code.
					append("\t\tagent.addSensorAdaptor(new SensorAdaptor() {\n").
					append("\t\t\tpublic void sense(astra.core.Agent agent) {\n");
				
				for (String sensor : sensors) {
					code.append("\t\t\t\t((" + module.qualifiedName() + ") agent.getModule(\"" + fullName + "\",\"" + module.name() + "\"))." + sensor + "();\n");
				}
				
				code.
					append("\t\t\t}\n").
					append("\t\t});\n\n");
			}
		}

		for (InitialElement initial : element.getInitials()) {
			initial.accept(this, "\t\t");
		}

		code.append("\t}\n\n")
				.append("\tpublic Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {\n")
				.append("\t\tFragment fragment = new Fragment(this);\n");

		for (ModuleElement module : element.getModules()) {
			module.accept(this, "\t\t");
		}

		code.append("\t\treturn fragment;\n")
				.append("\t}\n\n");
		
		if (element.getClassDeclaration().isAbstract()) {
			code.append("\tpublic synchronized astra.core.Agent newInstance(String name) throws AgentCreationException, ASTRAClassNotFoundException {\n")
				.append("\t\tthrow new AgentCreationException(\"Agent Class is declared abstract\");\n")
				.append("\t}\n\n");
		}
//		} else {
			code.append("\tpublic static void main(String[] args) {\n")
				.append("\t\tScheduler.setStrategy(new AdaptiveSchedulerStrategy());\n")
				.append("\t\tListTerm argList = new ListTerm();\n")
				.append("\t\tfor (String arg: args) {\n")
				.append("\t\t\targList.add(Primitive.newPrimitive(arg));\n")
				.append("\t\t}\n\n")
				.append("\t\tString name = java.lang.System.getProperty(\"astra.name\", \"main\");\n")
				.append("\t\ttry {\n")
				.append("\t\t\tastra.core.Agent agent = new " + qualifiedName + "().newInstance(name);\n")
				.append("\t\t\tagent.initialize(new Goal(new Predicate(\"main\", new Term[] { argList })));\n")
				.append("\t\t\tScheduler.schedule(agent);\n")
				.append("\t\t} catch (AgentCreationException e) {\n")
				.append("\t\t\te.printStackTrace();\n")
				.append("\t\t} catch (ASTRAClassNotFoundException e) {\n")
				.append("\t\t\te.printStackTrace();\n").append("\t\t};\n")
				.append("\t}\n}\n");
//		}
		return null;
	}

	@Override
	public Object visit(InferenceElement element, Object data)
			throws ParseException {
		code.append(data.toString() + "addInference(new Inference(\n");
		element.head().accept(this, data + "\t");
		code.append(",\n");
		element.body().accept(this, data + "\t");
		code.append("\n" + data + "));\n");
		return null;
	}

	@Override
	public Object visit(RuleElement element, Object data) throws ParseException {
		code.append(data.toString() + "addRule(new Rule(\n");
		code.append(data+"\t"+locationData(element)+",\n");
		element.event().accept(this, data + "\t");
		code.append(",\n");
		element.context().accept(this, data + "\t");
		code.append(",\n");
		element.statement().accept(this, data + "\t");
		code.append("\n" + data + "));\n");
		return null;
	}

	@Override
	public Object visit(GRuleElement element, Object data) throws ParseException {
		code.append(data.toString() + "addRule(new Rule(\n");
		code.append(data+"\t"+locationData(element)+",\n");
		element.event().accept(this, data + "\t");
		code.append(",\n");
		element.context().accept(this, data + "\t");
		code.append(",\n");
		element.dropCondition().accept(this, data + "\t");
		code.append(",\n");
		element.statement().accept(this, data + "\t");
		code.append(", new Rule[] {\n\t");
		boolean first = true;
		for (RuleElement rule : element.rules()) {
			if (first) {
				first=false;
			} else {
				code.append(",\n\t");
			}
			code.append(data.toString() + "\tnew Rule(\n");
			code.append(data+"\t\t\t"+locationData(element)+",\n");
			rule.event().accept(this, data + "\t\t\t");
			code.append(",\n");
			rule.context().accept(this, data + "\t\t\t");
			code.append(",\n");
			rule.statement().accept(this, data + "\t\t\t");
			code.append("\n" + data + "\t\t)");
		}
		code.append("\n" + data + "}));\n");
		return null;
	}

	@Override
	public Object visit(PlanElement element, Object data) throws ParseException {
		code.append(data.toString() + "addPlan(new Plan(\n");
		element.signature().accept(this, data + "\t");
		code.append(",\n");
		element.statement().accept(this, data + "\t");
		code.append("\n" + data + "));\n");
		return null;
	}

	@Override
	public Object visit(FunctionElement element, Object data)
			throws ParseException {
		code.append(data.toString() + "addFunction(new Function(\n");
		element.signature().accept(this, data + "\t");
		code.append(",\n" + data + "\tnew TRRule[] {\n");
		boolean first = true;
		for (TRRuleElement rule : element.rules()) {
			if (first)
				first = false;
			else
				code.append(",\n");
			rule.accept(this, data + "\t\t");
		}
		code.append("\n\t" + data + "}\n");
		code.append("\n" + data + "));\n");
		return null;
	}

	@Override
	public Object visit(InitialElement element, Object data)
			throws ParseException {

		code.append(data + "agent.initialize(\n");
		element.formula().accept(this, data + "\t");
		code.append("\n" + data + ");\n");

		return null;
	}

	@Override
	public Object visit(ModuleElement element, Object data)
			throws ParseException {
		code.append(data + "fragment.addModule(\"" + element.name() + "\","
				+ element.qualifiedName()
				+ ".class,agent);\n");

		return null;
	}

	// **********************************************************************************
	// TR Elements
	// **********************************************************************************
	public Object visit(TRRuleElement element, Object data)
			throws ParseException {
		code.append(data + "new TRRule(\n");
		element.formula().accept(this, data + "\t");
		code.append(",\n");
		element.action().accept(this, data + "\t");
		code.append("\n" + data + ")");

		return null;
	}

	public Object visit(TRModuleCallAction action, Object data)
			throws ParseException {
		code.append(data + "new ModuleAction(\"" + action.module() + "\",\n");
		code.append(data + "\t" + locationData(action));
		code.append(",\n");
		action.method().accept(this, data + "\t");

		ModuleElement element = store.modules.get(action.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + action.module(), action);
		}

		MethodSignature signature = new MethodSignature(action.method(), IJavaHelper.ACTION);
		if (!helper.validate(element.qualifiedName(), signature)) {
			if (helper.hasTRAutoAction(element.className())) {
				// Insert code here... below is module call code...
				code.append(",\n\t" + data + "new DefaultModuleActionAdaptor() {\n");
				code.append(data 
						+ "\t\tpublic boolean invoke(TRContext context, Predicate predicate) {\n");
				code
					.append(data + "\t\t\treturn ((" + element.qualifiedName()
						+ ") context.getModule(\"" + fullName + "\",\""
						+ action.module() + "\")).auto_action(context, evaluate(context, predicate));\n")
					.append(data + "\t\t}\n")
					.append(data + "\t}").append("\n" + data + ")");
				return null;
				
			} else {
				System.out.println("class: " + element.className());
				throw new ParseException(
						"Could not find matching method for action call: "
								+ action.method() + " on module: "
								+ action.module(), action);
			}
		}

		code.append(",\n\t" + data + "new ModuleActionAdaptor() {\n");
		code.append(data 
				+ "\t\tpublic boolean invoke(TRContext context, Predicate predicate) {\n");
		code
			.append(data + "\t\t\treturn ((" + element.qualifiedName()
				+ ") context.getModule(\"" + fullName + "\",\""
				+ action.module() + "\"))."
				+ action.method().predicate() + "(");

		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0)
				code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\t(" + signature.type(i).toClassString()
					+ ") context.getValue(predicate.getTerm(" + i + "))");
		}

		code.append("\n" + data + "\t\t\t);\n").append(data + "\t\t}\n")
				.append(data + "\t}").append("\n" + data + ")");

		return null;
	}

	@Override
	public Object visit(UpdateAction action, Object data) throws ParseException {
		code.append(data + "new TRBeliefUpdate('" + action.type() + "',\n");
		code.append(data + "\t" + locationData(action));
		code.append(",\n");
		action.call().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	public Object visit(CartagoAction action, Object data)
			throws ParseException {
		code.append(data + "new CartagoAction(\n");
		code.append(data + "\t" + locationData(action));
		code.append(",\n");
		if (action.artifact() != null) {
			action.artifact().accept(this, data + "\t");
			code.append(",\n");
		}
		action.call().accept(this, data + "\t");
		code.append("\n" + data + ")\n");

		return null;
	}

	@Override
	public Object visit(TRAction action, Object data) throws ParseException {
		if (action.type().equals("start")) {
			code.append(data + "new TRStartAction(\n");
		} else {
			code.append(data + "new TRStopAction(\n");
		}
		action.call().accept(this, data + "\t");
		code.append("\n" + data + ")\n");
		return null;
	}

	@Override
	public Object visit(FunctionCallAction action, Object data)
			throws ParseException {
		code.append(data + "new FunctionCall(\n");
		action.call().accept(this, data + "\t");
		code.append("\n" + data + ")\n");
		return null;
	}

	@Override
	public Object visit(BlockAction action, Object data) throws ParseException {
		code.append(data + "new CompositeAction(\n");
		boolean first = true;
		for (IAction act : action.actions()) {
			if (first)
				first = false;
			else
				code.append(",\n");
			act.accept(this, data + "\t");
		}
		code.append(data + ")");
		return null;
	}

	// **********************************************************************************
	// STATEMENTS
	// **********************************************************************************
	@Override
	public Object visit(BlockStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Block(\n")
				.append(data + "\t" + locationData(statement) + ",\n")
				.append(data + "\tnew Statement[] {");

		boolean first = true;
		for (IStatement s : statement.statements()) {
			if (first)
				first = false;
			else
				code.append(",");
			code.append("\n");
			s.accept(this, data + "\t\t");
		}

		code.append("\n" + data + "\t}\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(SynchronizedBlockStatement statement, Object data)
			throws ParseException {
		code.append(data + "new SynchronizedBlock(\n")
				.append(data + "\t" + locationData(statement) + ",\n")
				.append(data + "\t\"" + statement.token() + "\",\n")
				.append(data + "\tnew Block(\n")
				.append(data + "\t\t" + locationData(statement) + ",\n")
				.append(data + "\t\tnew Statement[] {");

		boolean first = true;
		for (IStatement s : statement.statements()) {
			if (first)
				first = false;
			else
				code.append(",");
			code.append("\n");
			s.accept(this, data + "\t\t\t");
		}

		code.append("\n" + data + "\t\t}\n" + data + "\t)");

		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(MaintainBlockStatement statement, Object data)
			throws ParseException {
		code.append(data + "new MaintainBlock(\n")
				.append(data + "\t" + locationData(statement) + ",\n");
		statement.formula().accept(this, data+"\t");
		code.append(",\n")
				.append(data + "\tnew Block(\n")
				.append(data + "\t\t" + locationData(statement) + ",\n")
				.append(data + "\t\tnew Statement[] {");

		boolean first = true;
		for (IStatement s : statement.statements()) {
			if (first)
				first = false;
			else
				code.append(",");
			code.append("\n");
			s.accept(this, data + "\t\t\t");
		}

		code.append("\n" + data + "\t\t}\n" + data + "\t)");

		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(DeclarationStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Declaration(\n").append(
				data + "\tnew Variable(");

		statement.type().accept(this, data);

		code.append(", \"" + statement.variable() + "\"),\n").append(
				data + "\t" + locationData(statement));

		if (statement.term() != null) {
			code.append(",\n");
			statement.term().accept(this, data + "\t");
		}
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(AssignmentStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Assignment(\n")
				.append(data + "\tnew Variable(");
		statement.type().accept(this, data);
		code.append(", \"" + statement.variable() + "\"),\n").append(
				data + "\t" + locationData(statement) + ",\n");
		statement.term().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(PlusPlusStatement statement, Object data)
			throws ParseException {
		code.append(data + "new PlusPlus(\n")
				.append(data + "\tnew Variable(");
		statement.type().accept(this, data);
		code.append(", \"" + statement.variable() + "\"),\n").append(
				data + "\t" + locationData(statement) + "\n");
		code.append(data + ")");
		return null;
	}

	@Override
	public Object visit(MinusMinusStatement statement, Object data)
			throws ParseException {
		code.append(data + "new MinusMinus(\n")
				.append(data + "\tnew Variable(");
		statement.type().accept(this, data);
		code.append(", \"" + statement.variable() + "\"),\n").append(
				data + "\t" + locationData(statement) + "\n");
		code.append(data + ")");
		return null;
	}

	private String locationData(AbstractElement element) {
		return "\"" + this.fullName + "\", new int[] {" + element.getBeginLine()
				+ "," + element.getBeginColumn() + "," + element.getEndLine() + ","
				+ element.getEndColumn() + "}";
	}

	@Override
	public Object visit(ModuleCallStatement statement, Object data)
			throws ParseException {
		ModuleElement element = store.modules.get(statement.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + statement.module(), statement);
		}

		MethodSignature signature = new MethodSignature(statement.method(), IJavaHelper.ACTION);
		
		if (!helper.validate(element.qualifiedName(), signature)) {
			if (helper.hasAutoAction(element.className())) {
				code.append(data + "new ModuleCall(\"" + statement.module() + "\",\n\t"
						+ data + locationData(statement) + ",\n");
				statement.method().accept(this, data + "\t");

				code.append(",\n\t" + data + "new DefaultModuleCallAdaptor() {\n");
				code.append(data 
						+ "\t\tpublic boolean inline() {\n");
				code.append(data 
						+ "\t\t\treturn " + helper.isInline(element.qualifiedName(),signature)+";\n");
				code.append(data 
						+ "\t\t}\n\n");
				code.append(data + "\t\tpublic boolean invoke(Intention intention, Predicate predicate) {\n");
				code.append(data + "\t\t\treturn ((" + element.qualifiedName());
				code.append(") intention.getModule(\"" + fullName + "\",\"");
				code.append(statement.module() + "\")).auto_action(intention,evaluate(intention,predicate));\n");
				code.append(data + "\t\t}\n");
				
				if (helper.suppressAutoActionNotifications(element.className())) {
					code.append(data + "\t\tpublic boolean suppressNotification() {\n");
					code.append(data + "\t\t\treturn true;\n");
					code.append(data + "\t\t}\n");
				}
					
				code.append(data + "\t}\n" + data + ")");
				return null;
			} else {
				throw new ParseException(
						"Could not find matching method for action call: "
								+ statement.method() + " on module: "
								+ statement.module(), statement);
			}
		}

		code.append(data + "new ModuleCall(\"" + statement.module() + "\",\n\t"
				+ data + locationData(statement) + ",\n");
		statement.method().accept(this, data + "\t");

		code.append(",\n\t" + data + "new DefaultModuleCallAdaptor() {\n");
		code.append(data + "\t\tpublic boolean inline() {\n"
				+ data + "\t\t\treturn " + helper.isInline(element.qualifiedName(),signature)+";\n"
				+ data + "\t\t}\n\n");
		code.append(data
				+ "\t\tpublic boolean invoke(Intention intention, Predicate predicate) {\n");
		code.append(data + "\t\t\treturn ((" + element.qualifiedName()
				+ ") intention.getModule(\"" + fullName + "\",\""
				+ statement.module() + "\"))."
				+ statement.method().predicate() + "(");

		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0)
				code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\t(" + filterPrimitives(signature.type(i).toClassString())
					+ ") intention.evaluate(predicate.getTerm(" + i + "))");
		}

		code.append("\n" + data + "\t\t\t);\n").append(data + "\t\t}\n")
				.append(data + "\t}").append("\n" + data + ")");
		return null;
	}

	private String filterPrimitives(String classString) {
		String check = primitiveMap.get(classString);
		if (check == null) return classString;
		return check;
	}

	@Override
	public Object visit(PlanCallStatement statement, Object data)
			throws ParseException {
		code.append(data + "new PlanCall(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.call().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(IfStatement statement, Object data)
			throws ParseException {
		code.append(data + "new If(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.guard().accept(this, data + "\t");
		code.append(",\n");
		statement.ifStatement().accept(this, data + "\t");
		if (statement.elseStatement() != null) {
			code.append(",\n");
			statement.elseStatement().accept(this, data + "\t");
		}
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(SendStatement statement, Object data)
			throws ParseException {

		code.append(data + "new Send(" + locationData(statement) + ",\n");
		statement.performative().accept(this, data + "\t");
		code.append(",\n");
		statement.sender().accept(this, data + "\t");
		code.append(",\n");
		statement.content().accept(this, data + "\t");
		if (statement.params() != null) {
			code.append(",\n");
			statement.params().accept(this, data + "\t");
		}
		code.append("\n" + data + ")");
		;
		return null;
	}

	@Override
	public Object visit(UpdateStatement statement, Object data)
			throws ParseException {
		if (statement.op().equals("-+")) {
			code.append(data + "new SpecialBeliefUpdate(\n\t");
			
		} else {
			code.append(data).append("new BeliefUpdate('").append(statement.op()).append("',\n\t");
		}
		code.append(data).append(locationData(statement)).append(",\n");
		statement.formula().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(SpawnGoalStatement statement, Object data)
			throws ParseException {
		code.append(data + "new SpawnGoal(\n\t" + data
				+ locationData(statement) + ",\n");
		statement.goal().accept(this, data + "\t");
		code.append("\n" + data + ")");
		;
		return null;
	}

	@Override
	public Object visit(SubGoalStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Subgoal(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.goal().accept(this, data + "\t");
		code.append("\n" + data + ")");
		;
		return null;
	}

	@Override
	public Object visit(QueryStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Query(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.formula().accept(this, data + "\t");
		code.append("\n" + data + ")");
		;
		return null;
	}

	@Override
	public Object visit(WhileStatement statement, Object data)
			throws ParseException {
		code.append(data + "new While(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.guard().accept(this, data + "\t");
		code.append(",\n");
		statement.statement().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(ForEachStatement statement, Object data)
			throws ParseException {
		code.append(data + "new ForEach(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.guard().accept(this, data + "\t");
		code.append(",\n");
		statement.statement().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(ForAllStatement statement, Object data)
			throws ParseException {
		code.append(data + "new ForAll(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.variable().accept(this, data + "\t");
		code.append(",\n");
		statement.list().accept(this, data + "\t");
		code.append(",\n");
		statement.statement().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	public Object visit(WhenStatement statement, Object data)
			throws ParseException {
		code.append(data + "new When(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.guard().accept(this, data + "\t");
		code.append(",\n");
		statement.statement().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(WaitStatement statement, Object data)
			throws ParseException {
		code.append(data + "new Wait(\n\t" + data + locationData(statement)
				+ ",\n");
		statement.guard().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(TRStatement statement, Object data)
			throws ParseException {
		if (statement.type().equals("start")) {
			code.append(data + "new TRStart(\n\t" + data
					+ locationData(statement) + ",\n");
			statement.function().accept(this, data + "\t");
		} else {
			code.append(data + "new TRStop(\n\t" + data
					+ locationData(statement) + "\n");
		}
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(TryRecoverStatement statement, Object data)
			throws ParseException {
		code.append(data + "new TryRecover(\n\t" + data
				+ locationData(statement) + ",\n");
		statement.tryStatement().accept(this, data + "\t");
		code.append(",\n");
		statement.recoverStatement().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(ScopedStatement statement, Object data)
			throws ParseException {
		if (statement.statement() instanceof SubGoalStatement) {
			SubGoalStatement s = (SubGoalStatement) statement.statement();
			code.append(data + "new ScopedSubgoal(\n\t" + data
					+ locationData(statement) + ",\n\t" + data + "\""
					+ statement.scope() + "\",\n");
			s.goal().accept(this, data + "\t");
			code.append("\n" + data + ")");
			;
		} else if (statement.statement() instanceof SpawnGoalStatement) {
			SpawnGoalStatement s = (SpawnGoalStatement) statement.statement();
			code.append(data + "new ScopedSpawnGoal(\n\t" + data
					+ locationData(statement) + ",\n\t" + data + "\""
					+ statement.scope() + "\",\n");
			s.goal().accept(this, data + "\t");
			code.append("\n" + data + ")");
			;
		} else if (statement.statement() instanceof PlanCallStatement) {
			PlanCallStatement s = (PlanCallStatement) statement.statement();
			code.append(data + "new ScopedPlanCall(\n\t" + data
					+ locationData(statement) + ",\t" + data + "\""
					+ statement.scope() + "\",\n");
			s.call().accept(this, data + "\t");
			code.append("\n" + data + ")");
		} else if (statement.statement() instanceof UpdateStatement) {
			UpdateStatement s = (UpdateStatement) statement.statement();
			code.append(data + "new ScopedBeliefUpdate(\n\t" + data
					+ locationData(statement) + ",\n\t" + data + "\""
					+ statement.scope() + "\",\n"
					+ data + "\t'" + s.op() + "',\n");
			s.formula().accept(this, data + "\t");
			code.append("\n" + data + ")");
		} else {
			throw new ParseException("Illegal use of scope operator.",
					statement);
		}
		return null;
	}

	// **********************************************************************************
	// EVENTS
	// **********************************************************************************
	@Override
	public Object visit(UpdateEvent event, Object data) throws ParseException {
		if (event.content() instanceof GoalFormula) {
			code.append(data + "new GoalEvent('" + event.type() + "',\n");
			event.content().accept(this, data + "\t");
			code.append("\n" + data + ")");
		} else if (event.content() instanceof PredicateFormula) {
			code.append(data + "new BeliefEvent('" + event.type() + "',\n");
			event.content().accept(this, data + "\t");
			code.append("\n" + data + ")");
		}
		return null;
	}

	@Override
	public Object visit(MessageEvent event, Object data) throws ParseException {
		code.append(data + "new MessageEvent(\n");
		event.speechact().accept(this, data + "\t");
		code.append(",\n");
		event.sender().accept(this, data + "\t");
		code.append(",\n");
		event.content().accept(this, data + "\t");
		if (event.params() != null) {
			code.append(",\n");
			event.params().accept(this, data + "\t");
		}
		code.append("\n" + data + ")");
		;
		return null;
	}

	@Override
	public Object visit(ModuleEvent event, Object data) throws ParseException {
		ModuleElement element = store.modules.get(event.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + event.module(), event);
		}

		MethodSignature signature = new MethodSignature(event.event(), IJavaHelper.EVENT, event.symbol() != null);
		
		if (!helper.validate(element.qualifiedName(), signature)) {
			throw new ParseException(
					"Could not find matching method for event call: "
							+ event.event() + " on module: "
							+ event.module(), event);
		}

		code.append(data + "new ModuleEvent(\"" + event.module() + "\",\n");
		code.append(data + "\t\"" + signature.signature() + "\",\n");
		event.event().accept(this, data + "\t");

		code.append(",\n\t" + data + "new ModuleEventAdaptor() {\n")
				.append(data
						+ "\t\tpublic Event generate(astra.core.Agent agent, Predicate predicate) {\n")
				.append(data + "\t\t\treturn ((" + element.qualifiedName()
						+ ") agent.getModule(\"" + fullName + "\",\""
						+ event.module() + "\"))."
						+ event.event().predicate() + "(");

		if (event.symbol() != null) {
			if (helper.getEventSymbols(element.qualifiedName(), signature, event.symbol())) {
				code.append("\n" + data + "\t\t\t\t\""+event.symbol()+"\",");
			} else {
				throw new ParseException(
						"Invalid eveny symbol: " 
								+ event.symbol() + " for event: "
								+ event.event() + " on module: "
								+ event.module(), event);
				
			}
		}
		
		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0)
				code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\tpredicate.getTerm(" + i + ")");
		}

		code.append("\n" + data + "\t\t\t);\n").append(data + "\t\t}\n")
				.append(data + "\t}").append("\n" + data + ")");

		return null;
	}
	
	// **********************************************************************************
	// FORMULAS
	// **********************************************************************************
	@Override
	public Object visit(AndFormula formula, Object data) throws ParseException {
		code.append(data + "new AND(\n");
		formula.left().accept(this, data + "\t");
		code.append(",\n");
		formula.right().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(OrFormula formula, Object data) throws ParseException {
		code.append(data + "new OR(\n");
		formula.left().accept(this, data + "\t");
		code.append(",\n");
		formula.right().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(ComparisonFormula formula, Object data)
			throws ParseException {
		code.append(data + "new Comparison(\"" + formula.operator() + "\",\n");
		formula.left().accept(this, data + "\t");
		code.append(",\n");
		formula.right().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(PredicateFormula formula, Object data)
			throws ParseException {
		if (formula.predicate().equals("true")) {
			code.append(data + "Predicate.TRUE");
		} else if (formula.predicate().equals("false")) {
			code.append(data + "Predicate.FALSE");
		} else {
			code.append(data + "new Predicate(\"" + formula.predicate()
					+ "\", new Term[] {");
			boolean first = true;
			for (ITerm term : formula.terms()) {
				if (first)
					first = false;
				else
					code.append(",");
				code.append("\n");
				term.accept(this, data + "\t");
			}
			code.append((first ? "" : "\n" + data) + "})");
		}
		return null;
	}

	@Override
	public Object visit(Function function, Object data) throws ParseException {
		code.append(data + "new Funct(\"" + function.functor()
				+ "\", new Term[] {");
		boolean first = true;
		for (ITerm term : function.terms()) {
			if (first)
				first = false;
			else
				code.append(",");
			code.append("\n");
			term.accept(this, data + "\t");
		}
		code.append((first ? "" : "\n" + data) + "})");
		return null;
	}

	@Override
	public Object visit(GoalFormula formula, Object data) throws ParseException {
		code.append(data + "new Goal(\n");
		formula.predicate().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(BracketFormula formula, Object data)
			throws ParseException {
		code.append(data + "new BracketFormula(\n");
		formula.formula().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(ScopedGoalFormula formula, Object data)
			throws ParseException {
		code.append(data + "new ScopedGoal(\"" + formula.scope() + "\",\n");
		formula.goal().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(NOTFormula formula, Object data) throws ParseException {
		code.append(data + "new NOT(\n");
		formula.formula().accept(this, data + "\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(FormulaVariable formula, Object data)
			throws ParseException {
		code.append(data + "new FormulaVariable(new Variable(Type.FORMULA,\""
				+ formula.identifier() + "\"))");
		return null;
	}

	@Override
	public Object visit(ModuleFormula formula, Object data) throws ParseException {
		ModuleElement element = store.modules.get(formula.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + formula.module(), formula);
		}
		
		MethodSignature signature = new MethodSignature(formula.method(), IJavaHelper.FORMULA);
		if (!helper.validate(element.qualifiedName(), signature)) {
			if (helper.hasAutoFormula(element.className())) {
				code.append(data + "new ModuleFormula(\"" + formula.module() + "\",\n");
				formula.method().accept(this, data + "\t");

				code.append(",\n\t" + data + "new ModuleFormulaAdaptor() {\n")
						.append(data + "\t\tpublic Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {\n")
						.append(data + "\t\t\treturn ((" + element.qualifiedName())
						.append(") visitor.agent().getModule(\"" + fullName + "\",\"")
						.append(formula.module() + "\")).auto_formula((Predicate) predicate.accept(visitor));\n")
						.append(data + "\t\t}\n" + data + "\t}")
						.append("\n" + data + ")");
				return null;
			} else {
				throw new ParseException(
						"Could not find matching method for formula call: "
								+ formula.method() + " on module: "
								+ formula.module(), formula);
			}
		}

		code.append(data + "new ModuleFormula(\"" + formula.module() + "\",\n");
		formula.method().accept(this, data + "\t");

		code.append(",\n" + data + "new ModuleFormulaAdaptor() {\n")
			.append(data + "\t\tpublic Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {\n")
			.append(data + "\t\t\treturn ((" + element.qualifiedName()
					+ ") visitor.agent().getModule(\"" + fullName + "\",\""
					+ formula.module() + "\"))."
					+ formula.method().predicate() + "(");

		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0) code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\t(" + signature.type(i).toClassString() + ") ");
			code.append("visitor.evaluate(predicate.getTerm(" + i + "))");
		}

		code.append("\n" + data + "\t\t\t);\n")
			.append(data + "\t}\n")
			.append(data + "}")
			.append("\n\t" + data + ")");
		return null;
	}

	// **********************************************************************************
	// TERMS
	// **********************************************************************************
	@Override
	public Object visit(ModuleTerm term, Object data) throws ParseException {
		code.append(data + "new ModuleTerm(\"" + term.module() + "\", ");
		term.type().accept(this, data);
		code.append(",\n");
		term.method().accept(this, data + "\t");

		ModuleElement element = store.modules.get(term.module());
		if (element == null) {
			throw new ParseException("Could not locate declaration for module: " + term.module(), term);
		}

		MethodSignature signature = new MethodSignature(term.method(),
				IJavaHelper.TERM);
		if (!helper.validate(element.qualifiedName(), signature)) {
			throw new ParseException(
					"Could not find matching method for action call: " +
					term.method() + " on module: " + term.module(),
					term);
		}

		code.append(",\n\t" + data + "new ModuleTermAdaptor() {\n")
				.append(data
						+ "\t\tpublic Object invoke(Intention intention, Predicate predicate) {\n")
				.append(data + "\t\t\treturn ((" + element.qualifiedName()
						+ ") intention.getModule(\"" + fullName + "\",\""
						+ term.module() + "\"))." + term.method().predicate()
						+ "(");

		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0)
				code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\t(" + signature.type(i).toClassString()
					+ ") intention.evaluate(predicate.getTerm(" + i + "))");
		}

		code.append("\n" + data + "\t\t\t);\n")
				.append(data + "\t\t}\n")
				.append(data
						+ "\t\tpublic Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {\n")
				.append(data + "\t\t\treturn ((" + element.qualifiedName()
						+ ") visitor.agent().getModule(\"" + fullName + "\",\""
						+ term.module() + "\"))." + term.method().predicate()
						+ "(");

		for (int i = 0; i < signature.types().length; i++) {
			if (i > 0)
				code.append(",");
			code.append("\n");
			code.append(data + "\t\t\t\t(" + signature.type(i).toClassString()
					+ ") visitor.evaluate(predicate.getTerm(" + i + "))");
		}

		code.append("\n" + data + "\t\t\t);\n").append(data + "\t\t}\n")
				.append(data + "\t}\n").append(data + ")");
		return null;
	}

	@Override
	public Object visit(InlineVariableDeclaration term, Object data)
			throws ParseException {
		code.append(data + "new Variable(");
		term.type().accept(this, data);
		code.append(", \"" + term.identifier() + "\"," + term.returns() + ")");
		return null;
	}

	private static Map<Integer, String> types = new HashMap<Integer, String>();
	static {
		types.put(Token.INTEGER, "Type.INTEGER");
		types.put(Token.LONG, "Type.LONG");
		types.put(Token.FLOAT, "Type.FLOAT");
		types.put(Token.DOUBLE, "Type.DOUBLE");
		types.put(Token.BOOLEAN, "Type.BOOLEAN");
		types.put(Token.CHARACTER, "Type.CHAR");
		types.put(Token.STRING, "Type.STRING");
		types.put(Token.LIST, "Type.LIST");
		types.put(Token.FORMULA, "Type.FORMULA");
		types.put(Token.SPEECHACT, "Type.PERFORMATIVE");
		types.put(Token.FUNCT, "Type.FUNCTION");
	}

	@Override
	public Object visit(Literal term, Object data) throws ParseException {
		if (term.type().type() == Token.SPEECHACT) {
			code.append(data + "new Performative(\"" + term.value() + "\")");
		} else {
			code.append(data + "Primitive.newPrimitive(" + term.value() + ")");
		}
		return null;
	}

	@Override
	public Object visit(ListTerm term, Object data) throws ParseException {
		code.append(data + "new ListTerm(new Term[] {\n");
		boolean first = true;
		for (ITerm t : term.terms()) {
			if (first)
				first = false;
			else
				code.append(",\n");
			t.accept(this, data+"\t");
		}
		code.append("\n");
		code.append(data + "})");
		return null;
	}

	@Override
	public Object visit(Operator term, Object data) throws ParseException {
		code.append(data + "Operator.newOperator('" + term.op() + "',\n");
		term.left().accept(this, data + "\t");
		code.append(",\n");
		term.right().accept(this, data + "\t");
		code.append("\n" + data + ")");

		return null;
	}

	@Override
	public Object visit(Brackets brackets, Object data) throws ParseException {
		code.append(data + "new Brackets(\n");
		brackets.contents().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(Variable term, Object data) throws ParseException {
		code.append(data + "new Variable(");
		if (term.type() == null) {
			throw new ParseException("Untyped term: : " + term.identifier(), term);
//			System.out.println("[CodeGeneratorVisitor].visit(Variable,Object)");
//			System.out.println("untyped term: " + term.identifier());
//			System.out.println("\tline: " + term.getBeginLine());
//			System.exit(0);
		}
		term.type().accept(this, data);
		code.append(", \"" + term.identifier() + "\")");
		return null;
	}

	@Override
	public Object visit(BasicType type, Object data) throws ParseException {
		code.append(types.get(type.type()));
		return null;
	}

	@Override
	public Object visit(ObjectType type, Object data) throws ParseException {
		code.append("new ObjectType(" + type.getClazz() + ".class)");
		return null;
	}

	@Override
	public Object visit(QueryTerm term, Object data) throws ParseException {
		code.append(data + "new QueryTerm(\n");
		term.formula().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}


	@Override
	public Object visit(ListSplitterTerm term, Object data) throws ParseException {
		code.append(data + "new ListSplitter(\n");
		term.head().accept(this, data+"\t");
		code.append(",\n");
		term.tail().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}

	@Override
	public Object visit(BindFormula formula, Object data) throws ParseException {
		code.append(data + "new Bind(\n");
		formula.variable().accept(this, data+"\t");
		code.append(",\n");
		formula.term().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}
	
	@Override
	public Object visit(CountTerm formula, Object data) throws ParseException {
		code.append(data + "new Count(\n");
		formula.term().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}
	
	@Override
	public Object visit(HeadTerm term, Object data) throws ParseException {
		code.append(data + "new Head(\n");
		term.term().accept(this, data+"\t");
		code.append(",\n" + data + "\t");
		term.type().accept(this, data+"\t");
		code.append("\n" + data + ")");
		
		return null;
	}
	
	@Override
	public Object visit(TailTerm term, Object data) throws ParseException {
		code.append(data + "new Tail(\n");
		term.term().accept(this, data+"\t");
		code.append("\n" + data + ")");
		return null;
	}
	
	@Override
	public Object visit(AtIndexTerm term, Object data) throws ParseException {
		code.append(data + "new AtIndex(\n");
		term.term().accept(this, data+"\t");
		code.append(",\n");
		term.index().accept(this, data+"\t");
		code.append(",\n" + data + "\t");
		term.type().accept(this, data+"\t");
		code.append("\n" + data + ")");
		
		return null;
	}
	
	@Override
	public Object visit(IsDoneFormula formula, Object data) throws ParseException {
		code.append(data + "new IsDone()");
		return null;
	}
	
}
