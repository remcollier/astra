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

import astra.unit.TestSuite;
import astra.unit.UnitTest;

public class Core extends ASTRAClass {
	public Core() {
		setParents(new Class[] {astra.unit.ASTRAUnitTest.class});
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test_maintain", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {14,42,22,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {15,8,22,5},
						new Predicate("timed_out", new Term[] {})
					),
					new ModuleCall("C",
						"Core", new int[] {16,8,16,27},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("before")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new MaintainBlock(
						"Core", new int[] {17,8,22,5},
						new Predicate("timed_out", new Term[] {}),
						new Block(
							"Core", new int[] {17,8,22,5},
							new Statement[] {
								new ModuleCall("C",
									"Core", new int[] {18,12,18,27},
									new Predicate("println", new Term[] {
										Primitive.newPrimitive("in")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("Core","C")).println(
												(java.lang.String) intention.evaluate(predicate.getTerm(0))
											);
										}
									}
								)
							}
						)
					),
					new ModuleCall("C",
						"Core", new int[] {20,8,20,26},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("after")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
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
					new Predicate("test_maintain_failed", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {24,49,32,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {25,8,32,5},
						new Predicate("timed_out", new Term[] {})
					),
					new MaintainBlock(
						"Core", new int[] {26,8,32,5},
						new Predicate("timed_out", new Term[] {}),
						new Block(
							"Core", new int[] {26,8,32,5},
							new Statement[] {
								new ModuleCall("C",
									"Core", new int[] {27,12,27,31},
									new Predicate("println", new Term[] {
										Primitive.newPrimitive("before")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("Core","C")).println(
												(java.lang.String) intention.evaluate(predicate.getTerm(0))
											);
										}
									}
								),
								new BeliefUpdate('-',
									"Core", new int[] {28,12,30,9},
									new Predicate("timed_out", new Term[] {})
								),
								new ModuleCall("C",
									"Core", new int[] {29,12,29,30},
									new Predicate("println", new Term[] {
										Primitive.newPrimitive("after")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("Core","C")).println(
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
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test_hello", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {34,39,36,5},
				new Statement[] {
					new ModuleCall("C",
						"Core", new int[] {35,8,35,26},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("hello")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
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
					new Predicate("test_beliefevent", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {38,45,40,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {39,8,40,5},
						new Predicate("test", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite")
						})
					)
				}
			)
		));
		addRule(new Rule(
			new BeliefEvent('+',
				new Predicate("test", new Term[] {
					new Variable(new ObjectType(TestSuite.class), "suite",false)
				})
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {43,32,45,5},
				new Statement[] {
					new ModuleCall("UT",
						"Core", new int[] {44,8,44,25},
						new Predicate("success", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.unit.UnitTest) intention.getModule("Core","UT")).success(
									(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0))
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
					new Predicate("test_fail", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {47,38,49,5},
				new Statement[] {
					new ModuleCall("S",
						"Core", new int[] {48,8,48,16},
						new Predicate("fail", new Term[] {}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("Core","S")).fail(
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
					new Predicate("test_recovered_failure", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {51,51,57,5},
				new Statement[] {
					new TryRecover(
						"Core", new int[] {52,8,57,5},
						new Block(
							"Core", new int[] {52,12,54,9},
							new Statement[] {
								new ModuleCall("S",
									"Core", new int[] {53,12,53,20},
									new Predicate("fail", new Term[] {}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.System) intention.getModule("Core","S")).fail(
											);
										}
									}
								)
							}
						),
						new Block(
							"Core", new int[] {54,18,57,5},
							new Statement[] {
								new ModuleCall("UT",
									"Core", new int[] {55,12,55,29},
									new Predicate("success", new Term[] {
										new Variable(new ObjectType(TestSuite.class), "suite")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.unit.UnitTest) intention.getModule("Core","UT")).success(
												(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0))
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
					new Predicate("test_assignment", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {59,44,62,5},
				new Statement[] {
					new Declaration(
						new Variable(Type.INTEGER, "Y"),
						"Core", new int[] {60,8,62,5},
						Primitive.newPrimitive(5)
					),
					new ModuleCall("UT",
						"Core", new int[] {61,8,61,35},
						new Predicate("assertEquals", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite"),
							new Variable(Type.INTEGER, "Y"),
							Primitive.newPrimitive(5)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.unit.UnitTest) intention.getModule("Core","UT")).assertEquals(
									(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0)),
									(java.lang.Integer) intention.evaluate(predicate.getTerm(1)),
									(java.lang.Integer) intention.evaluate(predicate.getTerm(2))
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
					new Predicate("test_subgoal_assignment", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {64,52,67,5},
				new Statement[] {
					new Subgoal(
						"Core", new int[] {65,8,67,5},
						new Goal(
							new Predicate("subgoal", new Term[] {
								new Variable(Type.INTEGER, "X",false)
							})
						)
					),
					new ModuleCall("UT",
						"Core", new int[] {66,8,66,35},
						new Predicate("assertEquals", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite"),
							new Variable(Type.INTEGER, "X"),
							Primitive.newPrimitive(5)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.unit.UnitTest) intention.getModule("Core","UT")).assertEquals(
									(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0)),
									(java.lang.Integer) intention.evaluate(predicate.getTerm(1)),
									(java.lang.Integer) intention.evaluate(predicate.getTerm(2))
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
					new Predicate("subgoal", new Term[] {
						new Variable(Type.INTEGER, "Y",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {69,34,71,5},
				new Statement[] {
					new Assignment(
						new Variable(Type.INTEGER, "Y"),
						"Core", new int[] {70,8,71,5},
						Primitive.newPrimitive(5)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test_actionparam", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {73,45,76,5},
				new Statement[] {
					new ModuleCall("M",
						"Core", new int[] {74,8,74,21},
						new Predicate("get", new Term[] {
							new Variable(Type.LONG, "Y",false)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((MyModule) intention.getModule("Core","M")).get(
									(ActionParam<java.lang.Long>) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new ModuleCall("UT",
						"Core", new int[] {75,8,75,36},
						new Predicate("assertEquals", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite"),
							new Variable(Type.LONG, "Y"),
							Primitive.newPrimitive(5l)
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.unit.UnitTest) intention.getModule("Core","UT")).assertEquals(
									(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0)),
									(java.lang.Long) intention.evaluate(predicate.getTerm(1)),
									(java.lang.Long) intention.evaluate(predicate.getTerm(2))
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
					new Predicate("test_modterm", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {78,41,80,5},
				new Statement[] {
					new ModuleCall("UT",
						"Core", new int[] {79,8,79,51},
						new Predicate("assertEquals", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite"),
							new ModuleTerm("M", Type.STRING,
								new Predicate("answer", new Term[] {}),
								new ModuleTermAdaptor() {
									public Object invoke(Intention intention, Predicate predicate) {
										return ((MyModule) intention.getModule("Core","M")).answer(
										);
									}
									public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
										return ((MyModule) visitor.agent().getModule("Core","M")).answer(
										);
									}
								}
							),
							Primitive.newPrimitive("happy")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.unit.UnitTest) intention.getModule("Core","UT")).assertEquals(
									(astra.unit.TestSuite) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1)),
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
					new Predicate("test_wait", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {82,38,87,5},
				new Statement[] {
					new SpawnGoal(
						"Core", new int[] {83,8,87,5},
						new Goal(
							new Predicate("timeout", new Term[] {
								Primitive.newPrimitive(1000)
							})
						)
					),
					new ModuleCall("C",
						"Core", new int[] {84,8,84,31},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("waiting...")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new Wait(
						"Core", new int[] {85,8,87,5},
						new Predicate("timed_out", new Term[] {})
					),
					new ModuleCall("C",
						"Core", new int[] {86,8,86,25},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("done")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
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
					new Predicate("timeout", new Term[] {
						new Variable(Type.INTEGER, "time",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {89,29,93,5},
				new Statement[] {
					new ModuleCall("S",
						"Core", new int[] {90,8,90,21},
						new Predicate("sleep", new Term[] {
							new Variable(Type.INTEGER, "time")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return false;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("Core","S")).sleep(
									(java.lang.Integer) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new BeliefUpdate('+',
						"Core", new int[] {91,8,93,5},
						new Predicate("timed_out", new Term[] {})
					),
					new BeliefUpdate('-',
						"Core", new int[] {92,8,93,5},
						new Predicate("timed_out", new Term[] {})
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("test_when", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {95,38,102,5},
				new Statement[] {
					new SpawnGoal(
						"Core", new int[] {96,8,102,5},
						new Goal(
							new Predicate("timeout", new Term[] {
								Primitive.newPrimitive(1000)
							})
						)
					),
					new ModuleCall("C",
						"Core", new int[] {97,8,97,31},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("waiting...")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
									(java.lang.String) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new When(
						"Core", new int[] {98,8,102,5},
						new Predicate("timed_out", new Term[] {}),
						new Block(
							"Core", new int[] {98,26,100,9},
							new Statement[] {
								new ModuleCall("C",
									"Core", new int[] {99,12,99,29},
									new Predicate("println", new Term[] {
										Primitive.newPrimitive("done")
									}),
									new DefaultModuleCallAdaptor() {
										public boolean inline() {
											return true;
										}

										public boolean invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Console) intention.getModule("Core","C")).println(
												(java.lang.String) intention.evaluate(predicate.getTerm(0))
											);
										}
									}
								)
							}
						)
					),
					new ModuleCall("C",
						"Core", new int[] {101,8,101,26},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("after")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return true;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Console) intention.getModule("Core","C")).println(
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
		fragment.addModule("C",astra.lang.Console.class,agent);
		fragment.addModule("S",astra.lang.System.class,agent);
		fragment.addModule("M",MyModule.class,agent);
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
			astra.core.Agent agent = new Core().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
