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

public class UserController extends ASTRAClass {
	public UserController() {
		setParents(new Class[] {astra.lang.Agent.class});
		addRule(new Rule(
			"astra.http.UserController", new int[] {19,9,19,19},
			new GoalEvent('+',
				new Goal(
					new Predicate("init", new Term[] {})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.UserController", new int[] {19,18,21,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.UserController", new int[] {20,8,20,23},
						new Predicate("register", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.UserController","http")).register(
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			"astra.http.UserController", new int[] {23,9,23,101},
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
				"astra.http.UserController", new int[] {23,100,26,5},
				new Statement[] {
					new Subgoal(
						"astra.http.UserController", new int[] {24,8,26,5},
						new Goal(
							new Predicate("validateUser", new Term[] {
								new ModuleTerm("P", Type.STRING,
									new Predicate("stringValueFor", new Term[] {
										new Variable(Type.LIST, "fields"),
										Primitive.newPrimitive("username")
									}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Prelude) intention.getModule("astra.http.UserController","P")).stringValueFor(
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Prelude) visitor.agent().getModule("astra.http.UserController","P")).stringValueFor(
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
											return ((astra.lang.Prelude) intention.getModule("astra.http.UserController","P")).stringValueFor(
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Prelude) visitor.agent().getModule("astra.http.UserController","P")).stringValueFor(
												(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
												(java.lang.String) visitor.evaluate(predicate.getTerm(1))
											);
										}
									}
								),
								new Variable(Type.LIST, "response",false)
							})
						)
					),
					new ModuleCall("http",
						"astra.http.UserController", new int[] {25,8,25,41},
						new Predicate("sendJSON", new Term[] {
							new Variable(new ObjectType(ChannelHandlerContext.class), "ctx"),
							new Variable(new ObjectType(FullHttpRequest.class), "req"),
							new Variable(Type.LIST, "response")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.UserController","http")).sendJSON(
									(io.netty.channel.ChannelHandlerContext) intention.evaluate(predicate.getTerm(0)),
									(io.netty.handler.codec.http.FullHttpRequest) intention.evaluate(predicate.getTerm(1)),
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(2))
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			"astra.http.UserController", new int[] {28,9,28,101},
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
				"astra.http.UserController", new int[] {28,100,38,5},
				new Statement[] {
					new Declaration(
						new Variable(Type.STRING, "U"),
						"astra.http.UserController", new int[] {29,8,38,5},
						new ModuleTerm("P", Type.STRING,
							new Predicate("stringValueFor", new Term[] {
								new Variable(Type.LIST, "fields"),
								Primitive.newPrimitive("username")
							}),
							new ModuleTermAdaptor() {
								public Object invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.Prelude) intention.getModule("astra.http.UserController","P")).stringValueFor(
										(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
										(java.lang.String) intention.evaluate(predicate.getTerm(1))
									);
								}
								public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
									return ((astra.lang.Prelude) visitor.agent().getModule("astra.http.UserController","P")).stringValueFor(
										(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
										(java.lang.String) visitor.evaluate(predicate.getTerm(1))
									);
								}
							}
						)
					),
					new If(
						"astra.http.UserController", new int[] {31,8,38,5},
						new Predicate("registration", new Term[] {
							new Variable(Type.STRING, "U"),
							new Variable(Type.STRING, "Password",false)
						}),
						new Block(
							"astra.http.UserController", new int[] {31,46,33,9},
							new Statement[] {
								new ModuleCall("http",
									"astra.http.UserController", new int[] {32,12,32,87},
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
											return ((astra.netty.Http) intention.getModule("astra.http.UserController","http")).sendJSON(
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
							"astra.http.UserController", new int[] {33,15,38,5},
							new Statement[] {
								new Declaration(
									new Variable(Type.STRING, "P"),
									"astra.http.UserController", new int[] {34,12,37,9},
									new ModuleTerm("P", Type.STRING,
										new Predicate("stringValueFor", new Term[] {
											new Variable(Type.LIST, "fields"),
											Primitive.newPrimitive("password")
										}),
										new ModuleTermAdaptor() {
											public Object invoke(Intention intention, Predicate predicate) {
												return ((astra.lang.Prelude) intention.getModule("astra.http.UserController","P")).stringValueFor(
													(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
													(java.lang.String) intention.evaluate(predicate.getTerm(1))
												);
											}
											public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
												return ((astra.lang.Prelude) visitor.agent().getModule("astra.http.UserController","P")).stringValueFor(
													(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
													(java.lang.String) visitor.evaluate(predicate.getTerm(1))
												);
											}
										}
									)
								),
								new BeliefUpdate('+',
									"astra.http.UserController", new int[] {35,12,37,9},
									new Predicate("registration", new Term[] {
										new Variable(Type.STRING, "U"),
										new Variable(Type.STRING, "P")
									})
								),
								new ModuleCall("http",
									"astra.http.UserController", new int[] {36,12,36,51},
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
											return ((astra.netty.Http) intention.getModule("astra.http.UserController","http")).sendJSON(
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
		addRule(new Rule(
			"astra.http.UserController", new int[] {40,9,40,68},
			new GoalEvent('+',
				new Goal(
					new Predicate("validateUser", new Term[] {
						new Variable(Type.STRING, "U",false),
						new Variable(Type.STRING, "P",false),
						new Variable(Type.LIST, "response",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.UserController", new int[] {40,67,51,5},
				new Statement[] {
					new TryRecover(
						"astra.http.UserController", new int[] {41,8,51,5},
						new Block(
							"astra.http.UserController", new int[] {41,12,48,9},
							new Statement[] {
								new Query(
									"astra.http.UserController", new int[] {42,12,42,51},
									new Predicate("registration", new Term[] {
										new Variable(Type.STRING, "U"),
										new Variable(Type.STRING, "Password",false)
									})
								),
								new If(
									"astra.http.UserController", new int[] {43,12,48,9},
									new Comparison("==",
										new Variable(Type.STRING, "P"),
										new Variable(Type.STRING, "Password")
									),
									new Block(
										"astra.http.UserController", new int[] {43,31,45,13},
										new Statement[] {
											new Assignment(
												new Variable(Type.LIST, "response"),
												"astra.http.UserController", new int[] {44,16,45,13},
												new ListTerm(new Term[] {
													new Funct("result", new Term[] {
														Primitive.newPrimitive("OK")
													})
												})
											)
										}
									),
									new Block(
										"astra.http.UserController", new int[] {45,19,48,9},
										new Statement[] {
											new Assignment(
												new Variable(Type.LIST, "response"),
												"astra.http.UserController", new int[] {46,16,47,13},
												new ListTerm(new Term[] {
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
							"astra.http.UserController", new int[] {48,18,51,5},
							new Statement[] {
								new Assignment(
									new Variable(Type.LIST, "response"),
									"astra.http.UserController", new int[] {49,12,50,9},
									new ListTerm(new Term[] {
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
	}

	public void initialize(astra.core.Agent agent) {
		agent.initialize(
			new Predicate("registration", new Term[] {
				Primitive.newPrimitive("rcollier"),
				Primitive.newPrimitive("passw0rd")
			})
		);
		agent.initialize(
			new Goal(
				new Predicate("init", new Term[] {})
			)
		);
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("http",astra.netty.Http.class,agent);
		fragment.addModule("P",astra.lang.Prelude.class,agent);
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
			astra.core.Agent agent = new UserController().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
