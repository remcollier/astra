package astra.debugger;
/**
 * GENERATED CODE - DO NOT CHANGE
 */

import astra.core.ASTRAClass;
import astra.core.ASTRAClassNotFoundException;
import astra.core.AgentCreationException;
import astra.core.Fragment;
import astra.core.Intention;
import astra.core.Rule;
import astra.core.Scheduler;
import astra.event.Event;
import astra.event.GoalEvent;
import astra.event.ModuleEvent;
import astra.event.ModuleEventAdaptor;
import astra.execution.BasicSchedulerStrategy;
import astra.formula.Comparison;
import astra.formula.Goal;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.statement.BeliefUpdate;
import astra.statement.Block;
import astra.statement.Declaration;
import astra.statement.DefaultModuleCallAdaptor;
import astra.statement.If;
import astra.statement.ModuleCall;
import astra.statement.Statement;
import astra.statement.Wait;
import astra.term.ListTerm;
import astra.term.ModuleTerm;
import astra.term.ModuleTermAdaptor;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;

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
				"astra.debugger.Debugger", new int[] {18,27,26,5},
				new Statement[] {
					new Wait(
						"astra.debugger.Debugger", new int[] {19,8,26,5},
						new Predicate("debugger_state", new Term[] {
							Primitive.newPrimitive("initialized")
						})
					),
					new If(
						"astra.debugger.Debugger", new int[] {20,8,26,5},
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
							"astra.debugger.Debugger", new int[] {20,30,25,9},
							new Statement[] {
								new Declaration(
									new Variable(Type.STRING, "name"),
									"astra.debugger.Debugger", new int[] {21,12,25,9},
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
									"astra.debugger.Debugger", new int[] {22,12,25,9},
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
									"astra.debugger.Debugger", new int[] {23,12,23,37},
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
									"astra.debugger.Debugger", new int[] {24,12,24,35},
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
				"astra.debugger.Debugger", new int[] {28,18,31,5},
				new Statement[] {
					new ModuleCall("gui",
						"astra.debugger.Debugger", new int[] {29,8,29,47},
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
						"astra.debugger.Debugger", new int[] {30,8,31,5},
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
				"astra.debugger.Debugger", new int[] {33,65,35,5},
				new Statement[] {
					new ModuleCall("Ctrl",
						"astra.debugger.Debugger", new int[] {34,8,34,26},
						new Predicate("suspend", new Term[] {
							new Variable(Type.STRING, "name")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.debugger.DebuggerCtrl) intention.getModule("astra.debugger.Debugger","Ctrl")).suspend(
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
				"astra.debugger.Debugger", new int[] {37,64,39,5},
				new Statement[] {
					new ModuleCall("Ctrl",
						"astra.debugger.Debugger", new int[] {38,8,38,25},
						new Predicate("resume", new Term[] {
							new Variable(Type.STRING, "name")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.debugger.DebuggerCtrl) intention.getModule("astra.debugger.Debugger","Ctrl")).resume(
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
					Primitive.newPrimitive("step"),
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
				"astra.debugger.Debugger", new int[] {41,62,43,5},
				new Statement[] {
					new ModuleCall("Ctrl",
						"astra.debugger.Debugger", new int[] {42,8,42,23},
						new Predicate("step", new Term[] {
							new Variable(Type.STRING, "name")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.debugger.DebuggerCtrl) intention.getModule("astra.debugger.Debugger","Ctrl")).step(
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
		fragment.addModule("Ctrl",astra.debugger.DebuggerCtrl.class,agent);
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
