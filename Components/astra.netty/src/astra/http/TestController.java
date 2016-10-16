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
				"astra.http.TestController", new int[] {14,27,20,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.TestController", new int[] {15,8,15,20},
						new Predicate("setup", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).setup(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.TestController", new int[] {16,8,16,23},
						new Predicate("register", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).register(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.TestController", new int[] {17,8,17,42},
						new Predicate("exportFolder", new Term[] {
							Primitive.newPrimitive("/view"),
							Primitive.newPrimitive("view")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).exportFolder(
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
			new GoalEvent('+',
				new Goal(
					new Predicate("test2", new Term[] {
						Primitive.newPrimitive("GET"),
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
						new Variable(new ObjectType(FullHttpRequest.class), "req"),
						new Variable(Type.LIST, "args")
					})
				)
			),
			Predicate.TRUE,
			new SynchronizedBlock(
				"astra.http.TestController", new int[] {22,112,26,5},
				"synchronized",
				new Block(
					"astra.http.TestController", new int[] {22,112,26,5},
					new Statement[] {
						new ModuleCall("C",
							"astra.http.TestController", new int[] {23,8,23,35},
							new Predicate("println", new Term[] {
								Primitive.newPrimitive("handling test2")
							}),
							new DefaultModuleCallAdaptor() {
								public boolean invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.Console) intention.getModule("astra.http.TestController","C")).println(
										(java.lang.String) intention.evaluate(predicate.getTerm(0))
									);
								}
							}
						),
						new ModuleCall("http",
							"astra.http.TestController", new int[] {24,8,24,46},
							new Predicate("loadView", new Term[] {
								new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
								new Variable(new ObjectType(FullHttpRequest.class), "req"),
								Primitive.newPrimitive("/Hello.html")
							}),
							new DefaultModuleCallAdaptor() {
								public boolean invoke(Intention intention, Predicate predicate) {
									return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).loadView(
										(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
										(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
										(java.lang.String) intention.evaluate(predicate.getTerm(2))
									);
								}
							}
						),
						new ModuleCall("C",
							"astra.http.TestController", new int[] {25,8,25,29},
							new Predicate("println", new Term[] {
								Primitive.newPrimitive("finished")
							}),
							new DefaultModuleCallAdaptor() {
								public boolean invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.Console) intention.getModule("astra.http.TestController","C")).println(
										(java.lang.String) intention.evaluate(predicate.getTerm(0))
									);
								}
							}
						)
					}
				)
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test3", new Term[] {
						Primitive.newPrimitive("GET"),
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
						new Variable(new ObjectType(FullHttpRequest.class), "req"),
						new Variable(Type.LIST, "args")
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {28,99,30,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.TestController", new int[] {29,8,29,58},
						new Predicate("sendJSON", new Term[] {
							new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
							new Variable(new ObjectType(FullHttpRequest.class), "req"),
							new Funct("is", new Term[] {
								new ListTerm(new Term[] {
									new Funct("happy", new Term[] {
										Primitive.newPrimitive("rem")
									}),
									Primitive.newPrimitive("bob")
								})
							})
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).sendJSON(
									(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
									(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
									(astra.term.Funct) intention.evaluate(predicate.getTerm(2))
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
					new Predicate("validate", new Term[] {
						Primitive.newPrimitive("POST"),
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
						new Variable(new ObjectType(FullHttpRequest.class), "req"),
						new Variable(Type.LIST, "args"),
						new Variable(Type.LIST, "fields")
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {32,116,36,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.http.TestController", new int[] {33,8,33,35},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("user: "),
								new Variable(Type.LIST, "fields")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.TestController","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new Declaration(
						new Variable(Type.STRING, "U"),
						"astra.http.TestController", new int[] {34,8,36,5},
						new ModuleTerm("P", Type.STRING,
							new Predicate("stringValueFor", new Term[] {
								new Variable(Type.LIST, "fields"),
								Primitive.newPrimitive("username")
							}),
							new ModuleTermAdaptor() {
								public Object invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.Prelude) intention.getModule("astra.http.TestController","P")).stringValueFor(
										(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
										(java.lang.String) intention.evaluate(predicate.getTerm(1))
									);
								}
								public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
									return ((astra.lang.Prelude) visitor.agent().getModule("astra.http.TestController","P")).stringValueFor(
										(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
										(java.lang.String) visitor.evaluate(predicate.getTerm(1))
									);
								}
							}
						)
					),
					new ModuleCall("C",
						"astra.http.TestController", new int[] {35,8,35,25},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("U="),
								new Variable(Type.STRING, "U")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.TestController","C")).println(
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
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("http",astra.netty.Http.class,agent);
		fragment.addModule("C",astra.lang.Console.class,agent);
		fragment.addModule("S",astra.lang.System.class,agent);
		fragment.addModule("P",astra.lang.Prelude.class,agent);
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
