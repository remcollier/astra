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
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {20,27,24,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.TestController", new int[] {21,8,21,20},
						new Predicate("setup", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).setup(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.TestController", new int[] {22,8,22,23},
						new Predicate("register", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).register(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.TestController", new int[] {23,8,23,42},
						new Predicate("exportFolder", new Term[] {
							Primitive.newPrimitive("/view"),
							Primitive.newPrimitive("view")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

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
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
						new Variable(new ObjectType(FullHttpRequest.class), "req",false),
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {26,99,28,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.TestController", new int[] {27,8,27,46},
						new Predicate("loadView", new Term[] {
							new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
							new Variable(new ObjectType(FullHttpRequest.class), "req"),
							Primitive.newPrimitive("/Hello.html")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).loadView(
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
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test3", new Term[] {
						Primitive.newPrimitive("GET"),
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
						new Variable(new ObjectType(FullHttpRequest.class), "req",false),
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {30,91,32,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.TestController", new int[] {31,8,31,58},
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
							public boolean inline() {
								return true;
							}

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
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
						new Variable(new ObjectType(FullHttpRequest.class), "req",false),
						new Variable(Type.LIST, "args",false),
						new Variable(Type.LIST, "fields",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {34,116,37,5},
				new Statement[] {
					new Subgoal(
						"astra.http.TestController", new int[] {35,8,37,5},
						new Goal(
							new Predicate("validateUser", new Term[] {
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
								),
								new ModuleTerm("P", Type.STRING,
									new Predicate("stringValueFor", new Term[] {
										new Variable(Type.LIST, "fields"),
										Primitive.newPrimitive("password")
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
								),
								new Variable(Type.FUNCTION, "response",false)
							})
						)
					),
					new ModuleCall("http",
						"astra.http.TestController", new int[] {36,8,36,41},
						new Predicate("sendJSON", new Term[] {
							new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
							new Variable(new ObjectType(FullHttpRequest.class), "req"),
							new Variable(Type.FUNCTION, "response")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

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
					new Predicate("validateUser", new Term[] {
						new Variable(Type.STRING, "U",false),
						new Variable(Type.STRING, "P",false),
						new Variable(Type.FUNCTION, "response",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {39,68,50,5},
				new Statement[] {
					new TryRecover(
						"astra.http.TestController", new int[] {40,8,50,5},
						new Block(
							"astra.http.TestController", new int[] {40,12,47,9},
							new Statement[] {
								new Query(
									"astra.http.TestController", new int[] {41,12,41,51},
									new Predicate("registration", new Term[] {
										new Variable(Type.STRING, "U"),
										new Variable(Type.STRING, "Password",false)
									})
								),
								new If(
									"astra.http.TestController", new int[] {42,12,47,9},
									new Comparison("==",
										new Variable(Type.STRING, "P"),
										new Variable(Type.STRING, "Password")
									),
									new Block(
										"astra.http.TestController", new int[] {42,31,44,13},
										new Statement[] {
											new Assignment(
												new Variable(Type.FUNCTION, "response"),
												"astra.http.TestController", new int[] {43,16,44,13},
												new Funct("response", new Term[] {
													new Funct("result", new Term[] {
														Primitive.newPrimitive("OK")
													})
												})
											)
										}
									),
									new Block(
										"astra.http.TestController", new int[] {44,19,47,9},
										new Statement[] {
											new Assignment(
												new Variable(Type.FUNCTION, "response"),
												"astra.http.TestController", new int[] {45,16,46,13},
												new Funct("response", new Term[] {
													new Funct("result", new Term[] {
														Primitive.newPrimitive("FAILED")
													}),
													new Funct("reason", new Term[] {
														Operator.newOperator('+',
															Primitive.newPrimitive("Incorrect Password for User: "),
															new Variable(Type.STRING, "U")
														)
													})
												})
											)
										}
									)
								)
							}
						),
						new Block(
							"astra.http.TestController", new int[] {47,18,50,5},
							new Statement[] {
								new Assignment(
									new Variable(Type.FUNCTION, "response"),
									"astra.http.TestController", new int[] {48,12,49,9},
									new Funct("response", new Term[] {
										new Funct("result", new Term[] {
											Primitive.newPrimitive("FAILED")
										}),
										new Funct("reason", new Term[] {
											Operator.newOperator('+',
												Primitive.newPrimitive("No Such User: "),
												new Variable(Type.STRING, "U")
											)
										})
									})
								)
							}
						)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("register", new Term[] {
						Primitive.newPrimitive("POST"),
						new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
						new Variable(new ObjectType(FullHttpRequest.class), "req",false),
						new Variable(Type.LIST, "args",false),
						new Variable(Type.LIST, "fields",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.TestController", new int[] {52,116,62,5},
				new Statement[] {
					new Declaration(
						new Variable(Type.STRING, "U"),
						"astra.http.TestController", new int[] {53,8,62,5},
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
					new If(
						"astra.http.TestController", new int[] {55,8,62,5},
						new Predicate("registration", new Term[] {
							new Variable(Type.STRING, "U"),
							new Variable(Type.STRING, "Password",false)
						}),
						new Block(
							"astra.http.TestController", new int[] {55,46,57,9},
							new Statement[] {
								new ModuleCall("http",
									"astra.http.TestController", new int[] {56,12,56,87},
									new Predicate("sendJSON", new Term[] {
										new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
										new Variable(new ObjectType(FullHttpRequest.class), "req"),
										new ListTerm(new Term[] {
											new Funct("result", new Term[] {
												Primitive.newPrimitive("FAILED")
											}),
											new Funct("reason", new Term[] {
												Operator.newOperator('+',
													Primitive.newPrimitive("Duplicate User: "),
													new Variable(Type.STRING, "U")
												)
											})
										})
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).sendJSON(
												(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
												(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(2))
											);
										}
									}
								)
							}
						),
						new Block(
							"astra.http.TestController", new int[] {57,15,62,5},
							new Statement[] {
								new Declaration(
									new Variable(Type.STRING, "P"),
									"astra.http.TestController", new int[] {58,12,61,9},
									new ModuleTerm("P", Type.STRING,
										new Predicate("stringValueFor", new Term[] {
											new Variable(Type.LIST, "fields"),
											Primitive.newPrimitive("password")
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
								new BeliefUpdate('+',
									"astra.http.TestController", new int[] {59,12,61,9},
									new Predicate("registration", new Term[] {
										new Variable(Type.STRING, "U"),
										new Variable(Type.STRING, "P")
									})
								),
								new ModuleCall("http",
									"astra.http.TestController", new int[] {60,12,60,51},
									new Predicate("sendJSON", new Term[] {
										new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
										new Variable(new ObjectType(FullHttpRequest.class), "req"),
										new ListTerm(new Term[] {
											new Funct("result", new Term[] {
												Primitive.newPrimitive("OK")
											})
										})
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.netty.Http) intention.getModule("astra.http.TestController","http")).sendJSON(
												(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
												(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(2))
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
		agent.initialize(
			new Predicate("registration", new Term[] {
				Primitive.newPrimitive("rcollier"),
				Primitive.newPrimitive("passw0rd")
			})
		);
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
