package astra.http;
/**
 * GENERATED CODE - DO NOT CHANGE
 */

import astra.core.ASTRAClass;
import astra.core.ASTRAClassNotFoundException;
import astra.core.ActionParam;
import astra.core.AgentCreationException;
import astra.core.Fragment;
import astra.core.Intention;
import astra.core.Rule;
import astra.core.Scheduler;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.event.ModuleEvent;
import astra.event.ModuleEventAdaptor;
import astra.execution.AdaptiveSchedulerStrategy;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.statement.Block;
import astra.statement.DefaultModuleCallAdaptor;
import astra.statement.ModuleCall;
import astra.statement.Statement;
import astra.term.ListTerm;
import astra.term.Operator;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.ObjectType;
import astra.type.Type;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class Test extends ASTRAClass {
	public Test() {
		setParents(new Class[] {astra.lang.Agent.class});
		addRule(new Rule(
			"astra.http.Test", new int[] {12,9,12,28},
			new GoalEvent('+',
				new Goal(
					new Predicate("main", new Term[] {
						new Variable(Type.LIST, "args",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.http.Test", new int[] {12,27,20,5},
				new Statement[] {
					new ModuleCall("http",
						"astra.http.Test", new int[] {13,8,13,20},
						new Predicate("setup", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.Test","http")).setup(
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.Test", new int[] {14,8,14,23},
						new Predicate("register", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.Test","http")).register(
								);
							}
						}
					),
					new ModuleCall("S",
						"astra.http.Test", new int[] {15,8,15,20},
						new Predicate("sleep", new Term[] {
							Primitive.newPrimitive(1000)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return false;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("astra.http.Test","S")).sleep(
									(java.lang.Integer) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("C",
						"astra.http.Test", new int[] {16,8,16,25},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("Here")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Test","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.Test", new int[] {17,8,17,86},
						new Predicate("plain_get", new Term[] {
							Primitive.newPrimitive("http://www.google.com/search?q=rem"),
							new Variable(Type.STRING, "content",false),
							new Variable(Type.INTEGER, "code",false)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.netty.Http) intention.getModule("astra.http.Test","http")).plain_get(
									(java.lang.String) intention.evaluate(predicate.getTerm(0)),
									(ActionParam<java.lang.String>) intention.evaluate(predicate.getTerm(1)),
									(ActionParam<java.lang.Integer>) intention.evaluate(predicate.getTerm(2))
								);
							}
						}
					),
					new ModuleCall("C",
						"astra.http.Test", new int[] {18,8,18,40},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("content: "),
								new Variable(Type.STRING, "content")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Test","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("C",
						"astra.http.Test", new int[] {19,8,19,34},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("code: "),
								new Variable(Type.INTEGER, "code")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Test","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			"astra.http.Test", new int[] {22,9,22,87},
			new ModuleEvent("http",
				"$ge",
				new Predicate("get", new Term[] {
					new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
					new Variable(new ObjectType(FullHttpRequest.class), "req",false),
					new ListTerm(new Term[] {
						Primitive.newPrimitive("hello"),
						Primitive.newPrimitive("world")
					})
				}),
				new ModuleEventAdaptor() {
					public Event generate(astra.core.Agent agent, Predicate predicate) {
						return ((astra.netty.Http) agent.getModule("astra.http.Test","http")).get(
							predicate.getTerm(0),
							predicate.getTerm(1),
							predicate.getTerm(2)
						);
					}
				}
			),
			Predicate.TRUE,
			new Block(
				"astra.http.Test", new int[] {22,86,25,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.http.Test", new int[] {23,8,23,32},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("hello World")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Test","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("http",
						"astra.http.Test", new int[] {24,8,24,46},
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
								return ((astra.netty.Http) intention.getModule("astra.http.Test","http")).loadView(
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
			"astra.http.Test", new int[] {27,9,27,96},
			new ModuleEvent("http",
				"$pe",
				new Predicate("post", new Term[] {
					new Variable(new ObjectType(ChannelHandlerContext.class), "ctx",false),
					new Variable(new ObjectType(FullHttpRequest.class), "req",false),
					new ListTerm(new Term[] {
						Primitive.newPrimitive("validate")
					}),
					new Variable(Type.LIST, "fields",false)
				}),
				new ModuleEventAdaptor() {
					public Event generate(astra.core.Agent agent, Predicate predicate) {
						return ((astra.netty.Http) agent.getModule("astra.http.Test","http")).post(
							predicate.getTerm(0),
							predicate.getTerm(1),
							predicate.getTerm(2),
							predicate.getTerm(3)
						);
					}
				}
			),
			Predicate.TRUE,
			new Block(
				"astra.http.Test", new int[] {27,95,29,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.http.Test", new int[] {28,8,28,43},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("received login request")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.http.Test","C")).println(
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
			astra.core.Agent agent = new Test().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
