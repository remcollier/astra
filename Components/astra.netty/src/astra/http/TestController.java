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

import astra.util.DB;
import astra.netty.Http;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

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
				"astra.http.TestController", new int[] {13,27,18,5},
				new Statement[] {
					new ModuleCall("ws",
						"astra.http.TestController", new int[] {14,8,14,18},
						new Predicate("setup", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","ws")).setup(
								);
							}
						}
					),
					new ModuleCall("ws",
						"astra.http.TestController", new int[] {15,8,15,21},
						new Predicate("register", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","ws")).register(
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
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
						new Variable(new ObjectType(FullHttpRequest.class), "req"),
						new Variable(Type.LIST, "args")
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {20,98,23,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.http.TestController", new int[] {21,8,21,40},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("handing get request")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.TestController","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("ws",
						"astra.http.TestController", new int[] {22,8,22,93},
						new Predicate("sendHTML", new Term[] {
							new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
							new Variable(new ObjectType(FullHttpRequest.class), "req"),
							Operator.newOperator('+',
								Primitive.newPrimitive("<html><body><h1>Hello World: "),
								Operator.newOperator('+',
									new Variable(Type.LIST, "args"),
									Primitive.newPrimitive("</h1></body></html>")
								)
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","ws")).sendHTML(
									(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
									(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
									(java.lang.String) intention.evaluate(predicate.getTerm(2))
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
		fragment.addModule("ws",astra.netty.Http.class,agent);
		fragment.addModule("C",astra.lang.Console.class,agent);
		fragment.addModule("S",astra.lang.System.class,agent);
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
