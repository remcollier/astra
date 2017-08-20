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
			"Core", new int[] {15,9,15,43},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_maintain", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {15,42,22,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {16,8,22,5},
						new Predicate("timed_out", new Term[] {})
					),
					new ModuleCall("C",
						"Core", new int[] {17,8,17,27},
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
						"Core", new int[] {18,8,22,5},
						new Predicate("timed_out", new Term[] {}),
						new Block(
							"Core", new int[] {18,8,22,5},
							new Statement[] {
								new ModuleCall("C",
									"Core", new int[] {19,12,19,27},
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
						"Core", new int[] {21,8,21,26},
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
			"Core", new int[] {24,9,24,50},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_maintain_failed", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {24,49,31,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {25,8,31,5},
						new Predicate("timed_out", new Term[] {})
					),
					new MaintainBlock(
						"Core", new int[] {26,8,31,5},
						new Predicate("timed_out", new Term[] {}),
						new Block(
							"Core", new int[] {26,8,31,5},
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
			"Core", new int[] {33,9,33,40},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_hello", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {33,39,35,5},
				new Statement[] {
					new ModuleCall("C",
						"Core", new int[] {34,8,34,26},
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
			"Core", new int[] {37,9,37,46},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_beliefevent", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {37,45,39,5},
				new Statement[] {
					new BeliefUpdate('+',
						"Core", new int[] {38,8,39,5},
						new Predicate("test", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite")
						})
					)
				}
			)
		));
		addRule(new Rule(
			"Core", new int[] {41,9,41,33},
			new BeliefEvent('+',
				new Predicate("test", new Term[] {
					new Variable(new ObjectType(TestSuite.class), "suite",false)
				})
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {41,32,43,5},
				new Statement[] {
					new ModuleCall("UT",
						"Core", new int[] {42,8,42,25},
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
			"Core", new int[] {45,9,45,39},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_fail", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {45,38,47,5},
				new Statement[] {
					new ModuleCall("S",
						"Core", new int[] {46,8,46,16},
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
			"Core", new int[] {49,9,49,52},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_recovered_failure", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {49,51,55,5},
				new Statement[] {
					new TryRecover(
						"Core", new int[] {50,8,55,5},
						new Block(
							"Core", new int[] {50,12,52,9},
							new Statement[] {
								new ModuleCall("S",
									"Core", new int[] {51,12,51,20},
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
							"Core", new int[] {52,18,55,5},
							new Statement[] {
								new ModuleCall("UT",
									"Core", new int[] {53,12,53,29},
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
			"Core", new int[] {57,9,57,45},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_assignment", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {57,44,60,5},
				new Statement[] {
					new Declaration(
						new Variable(Type.INTEGER, "Y"),
						"Core", new int[] {58,8,60,5},
						Primitive.newPrimitive(5)
					),
					new ModuleCall("UT",
						"Core", new int[] {59,8,59,35},
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
			"Core", new int[] {62,9,62,53},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_subgoal_assignment", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {62,52,65,5},
				new Statement[] {
					new Subgoal(
						"Core", new int[] {63,8,65,5},
						new Goal(
							new Predicate("subgoal", new Term[] {
								new Variable(Type.INTEGER, "X",false)
							})
						)
					),
					new ModuleCall("UT",
						"Core", new int[] {64,8,64,35},
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
			"Core", new int[] {67,9,67,35},
			new GoalEvent('+',
				new Goal(
					new Predicate("subgoal", new Term[] {
						new Variable(Type.INTEGER, "Y",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {67,34,69,5},
				new Statement[] {
					new Assignment(
						new Variable(Type.INTEGER, "Y"),
						"Core", new int[] {68,8,69,5},
						Primitive.newPrimitive(5)
					)
				}
			)
		));
		addRule(new Rule(
			"Core", new int[] {71,9,71,46},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_actionparam", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {71,45,74,5},
				new Statement[] {
					new ModuleCall("M",
						"Core", new int[] {72,8,72,21},
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
						"Core", new int[] {73,8,73,36},
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
			"Core", new int[] {76,9,76,42},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_modterm", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {76,41,78,5},
				new Statement[] {
					new ModuleCall("UT",
						"Core", new int[] {77,8,77,51},
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
			"Core", new int[] {80,9,80,39},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_wait", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {80,38,85,5},
				new Statement[] {
					new SpawnGoal(
						"Core", new int[] {81,8,85,5},
						new Goal(
							new Predicate("timeout", new Term[] {
								Primitive.newPrimitive(1000)
							})
						)
					),
					new ModuleCall("C",
						"Core", new int[] {82,8,82,31},
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
						"Core", new int[] {83,8,85,5},
						new Predicate("timed_out", new Term[] {})
					),
					new ModuleCall("C",
						"Core", new int[] {84,8,84,25},
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
			"Core", new int[] {87,9,87,30},
			new GoalEvent('+',
				new Goal(
					new Predicate("timeout", new Term[] {
						new Variable(Type.INTEGER, "time",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {87,29,93,5},
				new Statement[] {
					new ModuleCall("C",
						"Core", new int[] {88,8,88,40},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("Starting timeout...")
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
					new ModuleCall("S",
						"Core", new int[] {89,8,89,21},
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
					new ModuleCall("C",
						"Core", new int[] {90,8,90,33},
						new Predicate("println", new Term[] {
							Primitive.newPrimitive("Timed out...")
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
			"Core", new int[] {95,9,95,40},
			new GoalEvent('+',
				new Goal(
					new Predicate("timeout", new Term[] {
						new Variable(Type.INTEGER, "time",false),
						new Variable(Type.STRING, "X",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {95,39,101,5},
				new Statement[] {
					new ModuleCall("C",
						"Core", new int[] {96,8,96,39},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("in !timeout("),
								Operator.newOperator('+',
									new Variable(Type.STRING, "X"),
									Primitive.newPrimitive(")")
								)
							)
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
					new ModuleCall("S",
						"Core", new int[] {97,8,97,21},
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
					new ModuleCall("C",
						"Core", new int[] {98,8,98,45},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("slept in !timeout("),
								Operator.newOperator('+',
									new Variable(Type.STRING, "X"),
									Primitive.newPrimitive(")")
								)
							)
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
					new BeliefUpdate('+',
						"Core", new int[] {99,8,101,5},
						new Predicate("timed_out", new Term[] {
							new Variable(Type.STRING, "X")
						})
					),
					new BeliefUpdate('-',
						"Core", new int[] {100,8,101,5},
						new Predicate("timed_out", new Term[] {})
					)
				}
			)
		));
		addRule(new Rule(
			"Core", new int[] {103,9,103,40},
			new GoalEvent('+',
				new Goal(
					new Predicate("test_wait2", new Term[] {
						new Variable(new ObjectType(TestSuite.class), "suite",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"Core", new int[] {103,39,109,5},
				new Statement[] {
					new SpawnGoal(
						"Core", new int[] {104,8,109,5},
						new Goal(
							new Predicate("timeout", new Term[] {
								Primitive.newPrimitive(1000),
								Primitive.newPrimitive("A")
							})
						)
					),
					new ModuleCall("C",
						"Core", new int[] {105,8,105,31},
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
						"Core", new int[] {106,8,109,5},
						new Predicate("timed_out", new Term[] {
							new Variable(Type.STRING, "X",false)
						})
					),
					new ModuleCall("C",
						"Core", new int[] {107,8,107,31},
						new Predicate("println", new Term[] {
							Operator.newOperator('+',
								Primitive.newPrimitive("done: "),
								new Variable(Type.STRING, "X")
							)
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
					new ModuleCall("UT",
						"Core", new int[] {108,8,108,38},
						new Predicate("assertEquals", new Term[] {
							new Variable(new ObjectType(TestSuite.class), "suite"),
							new Variable(Type.STRING, "X"),
							Primitive.newPrimitive("A")
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
