package http;
/**
 * GENERATED CODE - DO NOT CHANGE
 */

import astra.acre.*;
import astra.cartago.*;
import astra.core.*;
import astra.execution.*;
import astra.event.*;
import astra.messaging.*;
import astra.formula.*;
import astra.lang.*;
import astra.eis.*;
import astra.statement.*;
import astra.term.*;
import astra.type.*;
import astra.tr.*;
import astra.util.*;

public class TestController extends ASTRAClass {
	public TestController() {
		setParents(new Class[] {astra.lang.Agent.class});
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("main", new Term[] {
						new Variable(Type.LIST, "args")
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"http.TestController", new int[] {6,27,9,5},
				new Statement[] {
					new ModuleCall("ws",
						"http.TestController", new int[] {7,8,7,18},
						new Predicate("setup", new Term[] {}),
						new ModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((http.WebServer) intention.getModule("http.TestController","ws")).setup(
								);
							}
						}
					),
					new ModuleCall("ws",
						"http.TestController", new int[] {8,8,8,21},
						new Predicate("register", new Term[] {}),
						new ModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((http.WebServer) intention.getModule("http.TestController","ws")).register(
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test", new Term[] {
						Primitive.newPrimitive("GET"),
						new Variable(new ObjectType(com.sun.net.httpserver.HttpExchange.class), "exchange"),
						new Variable(Type.LIST, "args")
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"http.TestController", new int[] {11,88,14,5},
				new Statement[] {
					new ModuleCall("ws",
						"http.TestController", new int[] {12,8,12,92},
						new Predicate("sendHTML", new Term[] {
							new Variable(new ObjectType(com.sun.net.httpserver.HttpExchange.class), "exchange"),
							Operator.newOperator('+',
								Primitive.newPrimitive("<html><body><h1>Hello World: "),
								Operator.newOperator('+',
									new Variable(Type.LIST, "args"),
									Primitive.newPrimitive("</h1></body></html>")
								)
							)
						}),
						new ModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((http.WebServer) intention.getModule("http.TestController","ws")).sendHTML(
									(com.sun.net.httpserver.HttpExchange) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
						}
					)
				}
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("ws",http.WebServer.class,agent);
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
			astra.core.Agent agent = new TestController().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
