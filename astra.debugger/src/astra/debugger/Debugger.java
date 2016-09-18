package astra.debugger;
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
import astra.reasoner.util.*;

public class Debugger extends ASTRAClass {
	public Debugger() {
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
				"astra.debugger.Debugger", new int[] {19,27,28,5},
				new Statement[] {
					new Wait(
						"astra.debugger.Debugger", new int[] {20,8,28,5},
						new Predicate("debugger_state", new Term[] {
							Primitive.newPrimitive("initialized")
						})
					),
					new If(
						"astra.debugger.Debugger", new int[] {21,8,28,5},
						new Comparison("==",
							new ModuleTerm("P", Type.INTEGER,
								new Predicate("size", new Term[] {
									new Variable(Type.LIST, "args")
								}),
								new ModuleTermAdaptor() {
									public Object invoke(Intention intention, Predicate predicate) {
										return ((astra.lang.Prelude) intention.getModule("astra.debugger.Debugger","P")).size(
											(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0))
										);
									}
									public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
										return ((astra.lang.Prelude) visitor.agent().getModule("astra.debugger.Debugger","P")).size(
											(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0))
										);
									}
								}
							),
							Primitive.newPrimitive(2)
						),
						new Block(
							"astra.debugger.Debugger", new int[] {21,30,26,9},
							new Statement[] {
								new Declaration(
									new Variable(Type.STRING, "name"),
									"astra.debugger.Debugger", new int[] {22,12,26,9},
									new ModuleTerm("P", Type.STRING,
										new Predicate("valueAsString", new Term[] {
											new Variable(Type.LIST, "args"),
											Primitive.newPrimitive(0)
										}),
										new ModuleTermAdaptor() {
											public Object invoke(Intention intention, Predicate predicate) {
												return ((astra.lang.Prelude) intention.getModule("astra.debugger.Debugger","P")).valueAsString(
													(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
													(int) intention.evaluate(predicate.getTerm(1))
												);
											}
											public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
												return ((astra.lang.Prelude) visitor.agent().getModule("astra.debugger.Debugger","P")).valueAsString(
													(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
													(int) visitor.evaluate(predicate.getTerm(1))
												);
											}
										}
									)
								),
								new Declaration(
									new Variable(Type.STRING, "type"),
									"astra.debugger.Debugger", new int[] {23,12,26,9},
									new ModuleTerm("P", Type.STRING,
										new Predicate("valueAsString", new Term[] {
											new Variable(Type.LIST, "args"),
											Primitive.newPrimitive(1)
										}),
										new ModuleTermAdaptor() {
											public Object invoke(Intention intention, Predicate predicate) {
												return ((astra.lang.Prelude) intention.getModule("astra.debugger.Debugger","P")).valueAsString(
													(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
													(int) intention.evaluate(predicate.getTerm(1))
												);
											}
											public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
												return ((astra.lang.Prelude) visitor.agent().getModule("astra.debugger.Debugger","P")).valueAsString(
													(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
													(int) visitor.evaluate(predicate.getTerm(1))
												);
											}
										}
									)
								),
								new ModuleCall("S",
									"astra.debugger.Debugger", new int[] {24,12,24,37},
									new Predicate("createAgent", new Term[] {
										new Variable(Type.STRING, "name"),
										new Variable(Type.STRING, "type")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).createAgent(
												(java.lang.String) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
									}
								),
								new ModuleCall("S",
									"astra.debugger.Debugger", new int[] {25,12,25,35},
									new Predicate("setMainGoal", new Term[] {
										new Variable(Type.STRING, "name"),
										new ListTerm(new Term[] {

										})
									}),
									new DefaultModuleCallAdaptor() {
										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).setMainGoal(
												(java.lang.String) intention.evaluate(predicate.getTerm(0)),
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(1))
											);
										}
									}
								)
							}
						)
					),
					new SpawnGoal(
						"astra.debugger.Debugger", new int[] {27,8,28,5},
						new Goal(
							new Predicate("turn", new Term[] {
								Primitive.newPrimitive("on")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("init", new Term[] {})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.debugger.Debugger", new int[] {32,18,36,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.debugger.Debugger", new int[] {33,8,33,34},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("launching gui")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.debugger.Debugger","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("gui",
						"astra.debugger.Debugger", new int[] {34,8,34,47},
						new Predicate("launch", new Term[] {
							Primitive.newPrimitive("astra.debugger.DebuggerUI")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.gui.GuiModule) intention.getModule("astra.debugger.Debugger","gui")).launch(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new BeliefUpdate('+',
						"astra.debugger.Debugger", new int[] {35,8,36,5},
						new Predicate("debugger_state", new Term[] {
							Primitive.newPrimitive("initialized")
						})
					)
				}
			)
		));
		addRule(new Rule(
			new ModuleEvent("gui",
				"$gui:",
				new Predicate("event", new Term[] {
					Primitive.newPrimitive("suspend"),
					new ListTerm(new Term[] {
						new Variable(Type.STRING, "name")
					})
				}),
				new ModuleEventAdaptor() {
					public Event generate(astra.core.Agent agent, Predicate predicate) {
						return ((astra.gui.GuiModule) agent.getModule("astra.debugger.Debugger","gui")).event(
							predicate.getTerm(0),
							predicate.getTerm(1)
						);
					}
				}
			),
			new Comparison("~=",
				new Variable(Type.STRING, "name"),
				new ModuleTerm("S", Type.STRING,
					new Predicate("name", new Term[] {}),
					new ModuleTermAdaptor() {
						public Object invoke(Intention intention, Predicate predicate) {
							return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).name(
							);
						}
						public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
							return ((astra.lang.System) visitor.agent().getModule("astra.debugger.Debugger","S")).name(
							);
						}
					}
				)
			),
			new Block(
				"astra.debugger.Debugger", new int[] {43,65,46,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.debugger.Debugger", new int[] {44,8,44,40},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("suspending: "),
								new Variable(Type.STRING, "name")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.debugger.Debugger","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("S",
						"astra.debugger.Debugger", new int[] {45,8,45,28},
						new Predicate("suspendAgent", new Term[] {
							new Variable(Type.STRING, "name")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).suspendAgent(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					)
				}
			)
		));
		addRule(new Rule(
			new ModuleEvent("gui",
				"$gui:",
				new Predicate("event", new Term[] {
					Primitive.newPrimitive("resume"),
					new ListTerm(new Term[] {
						new Variable(Type.STRING, "name")
					})
				}),
				new ModuleEventAdaptor() {
					public Event generate(astra.core.Agent agent, Predicate predicate) {
						return ((astra.gui.GuiModule) agent.getModule("astra.debugger.Debugger","gui")).event(
							predicate.getTerm(0),
							predicate.getTerm(1)
						);
					}
				}
			),
			new Comparison("~=",
				new Variable(Type.STRING, "name"),
				new ModuleTerm("S", Type.STRING,
					new Predicate("name", new Term[] {}),
					new ModuleTermAdaptor() {
						public Object invoke(Intention intention, Predicate predicate) {
							return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).name(
							);
						}
						public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
							return ((astra.lang.System) visitor.agent().getModule("astra.debugger.Debugger","S")).name(
							);
						}
					}
				)
			),
			new Block(
				"astra.debugger.Debugger", new int[] {48,64,51,5},
				new Statement[] {
					new ModuleCall("C",
						"astra.debugger.Debugger", new int[] {49,8,49,38},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("resuming: "),
								new Variable(Type.STRING, "name")
							)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("astra.debugger.Debugger","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("S",
						"astra.debugger.Debugger", new int[] {50,8,50,27},
						new Predicate("resumeAgent", new Term[] {
							new Variable(Type.STRING, "name")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("astra.debugger.Debugger","S")).resumeAgent(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
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
					new Predicate("turn", new Term[] {
						Primitive.newPrimitive("on")
					})
				)
			),
			new Predicate("light", new Term[] {
				Primitive.newPrimitive("off")
			}),
			new Block(
				"astra.debugger.Debugger", new int[] {53,37,57,5},
				new Statement[] {
					new BeliefUpdate('-',
						"astra.debugger.Debugger", new int[] {54,8,57,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("off")
						})
					),
					new BeliefUpdate('+',
						"astra.debugger.Debugger", new int[] {55,8,57,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("on")
						})
					),
					new SpawnGoal(
						"astra.debugger.Debugger", new int[] {56,8,57,5},
						new Goal(
							new Predicate("turn", new Term[] {
								Primitive.newPrimitive("off")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("turn", new Term[] {
						Primitive.newPrimitive("off")
					})
				)
			),
			new Predicate("light", new Term[] {
				Primitive.newPrimitive("on")
			}),
			new Block(
				"astra.debugger.Debugger", new int[] {59,37,63,5},
				new Statement[] {
					new BeliefUpdate('-',
						"astra.debugger.Debugger", new int[] {60,8,63,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("on")
						})
					),
					new BeliefUpdate('+',
						"astra.debugger.Debugger", new int[] {61,8,63,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("off")
						})
					),
					new SpawnGoal(
						"astra.debugger.Debugger", new int[] {62,8,63,5},
						new Goal(
							new Predicate("turn", new Term[] {
								Primitive.newPrimitive("on")
							})
						)
					)
				}
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
		agent.initialize(
			new Predicate("light", new Term[] {
				Primitive.newPrimitive("off")
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
		fragment.addModule("C",astra.lang.Console.class,agent);
		fragment.addModule("P",astra.lang.Prelude.class,agent);
		fragment.addModule("S",astra.lang.System.class,agent);
		fragment.addModule("gui",astra.gui.GuiModule.class,agent);
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
			astra.core.Agent agent = new Debugger().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
