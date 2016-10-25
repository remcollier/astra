package astra.fipa;
/**
 * GENERATED CODE - DO NOT CHANGE
 */

import astra.core.*;
import astra.execution.*;
import astra.event.*;
import astra.messaging.*;
import astra.formula.*;
import astra.lang.*;
import astra.statement.*;
import astra.term.*;
import astra.type.*;
import astra.tr.*;
import astra.reasoner.util.*;


public class Request extends ASTRAClass {
	public Request() {
		setParents(new Class[] {astra.fipa.FIPARequestProtocol.class});
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("main", new Term[] {
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.Request", new int[] {6,27,16,5},
				new Statement[] {
					new TryRecover(
						"astra.fipa.Request", new int[] {7,8,16,5},
						new Block(
							"astra.fipa.Request", new int[] {7,12,13,9},
							new Statement[] {
								new Subgoal(
									"astra.fipa.Request", new int[] {8,12,13,9},
									new Goal(
										new Predicate("fipa_request", new Term[] {
											new ModuleTerm("fipa_system", Type.STRING,
												new Predicate("name", new Term[] {}),
												new ModuleTermAdaptor() {
													public Object invoke(Intention intention, Predicate predicate) {
														return ((astra.lang.System) intention.getModule("astra.fipa.Request","fipa_system")).name(
														);
													}
													public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
														return ((astra.lang.System) visitor.agent().getModule("astra.fipa.Request","fipa_system")).name(
														);
													}
												}
											),
											new Funct("say", new Term[] {
												Primitive.newPrimitive("hello")
											}),
											new Variable(Type.FUNCTION, "result",false)
										})
									)
								),
								new ModuleCall("C",
									"astra.fipa.Request", new int[] {12,12,12,43},
									new Predicate("println", new Term[] {
										Operator.newOperator('+',
											Primitive.newPrimitive("SUCCESS: "),
											new Variable(Type.FUNCTION, "result")
										)
									}),
									new DefaultModuleCallAdaptor() {
										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("astra.fipa.Request","C")).println(
												(java.lang.String) intention.evaluate(predicate.getTerm(0))
											);
										}
									}
								)
							}
						),
						new Block(
							"astra.fipa.Request", new int[] {13,18,16,5},
							new Statement[] {
								new ModuleCall("C",
									"astra.fipa.Request", new int[] {14,12,14,31},
									new Predicate("println", new Term[] {
										Primitive.newPrimitive("FAILED")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("astra.fipa.Request","C")).println(
												(java.lang.String) intention.evaluate(predicate.getTerm(0))
											);
										}
									}
								)
							}
						)
					)
				}
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("C",astra.lang.Console.class,agent);
		return fragment;
	}

	public static void main(String[] args) {
		Scheduler.setStrategy(new BasicSchedulerStrategy());
		ListTerm argList = new ListTerm();
		for (String arg: args) {
			argList.add(Primitive.newPrimitive(arg));
		}

		String name = java.lang.System.getProperty("astra.name", "main");
		try {
			astra.core.Agent agent = new Request().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
