package astra.http;
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

import astra.netty.Http;

public class Main extends ASTRAClass {
	public Main() {
		setParents(new Class[] {astra.lang.Agent.class});
		addRule(new Rule(
			"astra.http.Main", new int[] {16,9,16,28},
			new GoalEvent('+',
				new Goal(
					new Predicate("main", new Term[] {
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.Main", new int[] {16,27,20,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.Main", new int[] {17,8,17,20},
						new Predicate("setup", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.Main","http")).setup(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.Main", new int[] {18,8,18,42},
						new Predicate("exportFolder", new Term[] {
							Primitive.newPrimitive("/view"),
							Primitive.newPrimitive("view")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.Main","http")).exportFolder(
									(java.lang.String) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					new ModuleCall("S",
						"astra.http.Main", new int[] {19,8,19,58},
						new Predicate("createAgent", new Term[] {
							Primitive.newPrimitive("user"),
							Primitive.newPrimitive("astra.http.UserController")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("astra.http.Main","S")).createAgent(
									(java.lang.String) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			"astra.http.Main", new int[] {22,9,22,19},
			new GoalEvent('+',
				new Goal(
					new Predicate("test", new Term[] {})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.Main", new int[] {22,18,27,5},
				new Statement[] {
					new BeliefUpdate('-',
						"astra.http.Main", new int[] {24,12,27,5},
						new Predicate("is", new Term[] {
							Primitive.newPrimitive("rem"),
							Primitive.newPrimitive("happy")
						})
					),
					new ModuleCall("C",
						"astra.http.Main", new int[] {25,12,25,41},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("rem is not happy")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Main","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					)
				}
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
		agent.initialize(
			new Predicate("is", new Term[] {
				Primitive.newPrimitive("rem"),
				Primitive.newPrimitive("happy")
			})
		);
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("http",astra.netty.Http.class,agent);
		fragment.addModule("S",astra.lang.System.class,agent);
		fragment.addModule("C",astra.lang.Console.class,agent);
		return fragment;
	}

	public static void main(String[] args) {
		Scheduler.setStrategy(new AdaptiveSchedulerStrategy());
		ListTerm argList = new ListTerm();
		for (String arg: args) {
			argList.add(Primitive.newPrimitive(arg));
		}

		String name = java.lang.System.getProperty("astra.name", "main");
		try {
			astra.core.Agent agent = new Main().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
