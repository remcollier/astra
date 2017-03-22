package astra.fipa;
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


public class FIPAProtocol extends ASTRAClass {
	public FIPAProtocol() {
		setParents(new Class[] {astra.lang.Agent.class});
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {24,22,31,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_conversation_id", new Term[] {
						new Variable(Type.STRING, "id",false)
					})
				)
			),
			new Predicate("fipa_conversation_counter", new Term[] {
				new Variable(Type.INTEGER, "count",false)
			}),
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {24,95,31,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {24,95,31,5},
					new Statement[] {
						new BeliefUpdate('-',
							"astra.fipa.FIPAProtocol", new int[] {25,8,31,5},
							new Predicate("fipa_conversation_counter", new Term[] {
								new Variable(Type.INTEGER, "count")
							})
						),
						new BeliefUpdate('+',
							"astra.fipa.FIPAProtocol", new int[] {26,8,31,5},
							new Predicate("fipa_conversation_counter", new Term[] {
								Operator.newOperator('+',
									new Variable(Type.INTEGER, "count"),
									Primitive.newPrimitive(1)
								)
							})
						),
						new Assignment(
							new Variable(Type.STRING, "id"),
							"astra.fipa.FIPAProtocol", new int[] {29,8,31,5},
							Operator.newOperator('+',
								new ModuleTerm("fipa_system", Type.STRING,
									new Predicate("name", new Term[] {}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.System) intention.getModule("astra.fipa.FIPAProtocol","fipa_system")).name(
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.System) visitor.agent().getModule("astra.fipa.FIPAProtocol","fipa_system")).name(
											);
										}
									}
								),
								Operator.newOperator('+',
									Primitive.newPrimitive("_"),
									new Brackets(
										Operator.newOperator('+',
											new Variable(Type.INTEGER, "count"),
											Primitive.newPrimitive(1)
										)
									)
								)
							)
						),
						new BeliefUpdate('+',
							"astra.fipa.FIPAProtocol", new int[] {30,8,31,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "id"),
								Primitive.newPrimitive("NEW")
							})
						)
					}
				)
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {33,9,33,62},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_timeout", new Term[] {
						new Variable(Type.STRING, "conversation_id",false),
						new Variable(Type.INTEGER, "timeout",false)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.FIPAProtocol", new int[] {33,61,39,5},
				new Statement[] {
					new ModuleCall("fipa_system",
						"astra.fipa.FIPAProtocol", new int[] {34,8,34,34},
						new Predicate("sleep", new Term[] {
							new Variable(Type.INTEGER, "timeout")
						}),
						new DefaultModuleCallAdaptor() {
							public boolean inline() {
								return false;
							}

							public boolean invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.System) intention.getModule("astra.fipa.FIPAProtocol","fipa_system")).sleep(
									(java.lang.Integer) intention.evaluate(predicate.getTerm(0))
								);
							}
						}
					),
					new BeliefUpdate('+',
						"astra.fipa.FIPAProtocol", new int[] {37,8,39,5},
						new Predicate("fipa_timedout", new Term[] {
							new Variable(Type.STRING, "conversation_id")
						})
					),
					new BeliefUpdate('-',
						"astra.fipa.FIPAProtocol", new int[] {38,8,39,5},
						new Predicate("fipa_timedout", new Term[] {
							new Variable(Type.STRING, "conversation_id")
						})
					)
				}
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {41,9,41,49},
			new BeliefEvent('+',
				new Predicate("fipa_timedout", new Term[] {
					new Variable(Type.STRING, "conversation_id",false)
				})
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.FIPAProtocol", new int[] {41,48,43,5},
				new Statement[] {
				}
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {45,22,48,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_set_state", new Term[] {
						new Variable(Type.STRING, "conversation_id",false),
						new Variable(Type.STRING, "state",false)
					})
				)
			),
			new Predicate("fipa_state", new Term[] {
				new Variable(Type.STRING, "conversation_id"),
				new Variable(Type.STRING, "old_state",false)
			}),
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {45,125,48,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {45,125,48,5},
					new Statement[] {
						new BeliefUpdate('-',
							"astra.fipa.FIPAProtocol", new int[] {46,8,48,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "conversation_id"),
								new Variable(Type.STRING, "old_state")
							})
						),
						new BeliefUpdate('+',
							"astra.fipa.FIPAProtocol", new int[] {47,8,48,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "conversation_id"),
								new Variable(Type.STRING, "state")
							})
						)
					}
				)
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {49,22,51,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_set_state", new Term[] {
						new Variable(Type.STRING, "conversation_id",false),
						new Variable(Type.STRING, "state",false)
					})
				)
			),
			Predicate.TRUE,
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {49,77,51,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {49,77,51,5},
					new Statement[] {
						new BeliefUpdate('+',
							"astra.fipa.FIPAProtocol", new int[] {50,8,51,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "conversation_id"),
								new Variable(Type.STRING, "state")
							})
						)
					}
				)
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {53,22,56,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_failed", new Term[] {
						new Variable(Type.STRING, "conversation_id",false)
					})
				)
			),
			new Predicate("fipa_state", new Term[] {
				new Variable(Type.STRING, "conversation_id"),
				new Variable(Type.STRING, "state",false)
			}),
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {53,104,56,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {53,104,56,5},
					new Statement[] {
						new BeliefUpdate('-',
							"astra.fipa.FIPAProtocol", new int[] {54,8,56,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "conversation_id"),
								new Variable(Type.STRING, "state")
							})
						),
						new ModuleCall("fipa_system",
							"astra.fipa.FIPAProtocol", new int[] {55,8,55,26},
							new Predicate("fail", new Term[] {}),
							new DefaultModuleCallAdaptor() {
								public boolean inline() {
									return true;
								}

								public boolean invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.System) intention.getModule("astra.fipa.FIPAProtocol","fipa_system")).fail(
									);
								}
							}
						)
					}
				)
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {58,22,60,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_failed", new Term[] {
						new Variable(Type.STRING, "conversation_id",false)
					})
				)
			),
			Predicate.TRUE,
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {58,60,60,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {58,60,60,5},
					new Statement[] {
						new ModuleCall("fipa_system",
							"astra.fipa.FIPAProtocol", new int[] {59,8,59,26},
							new Predicate("fail", new Term[] {}),
							new DefaultModuleCallAdaptor() {
								public boolean inline() {
									return true;
								}

								public boolean invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.System) intention.getModule("astra.fipa.FIPAProtocol","fipa_system")).fail(
									);
								}
							}
						)
					}
				)
			)
		));
		addRule(new Rule(
			"astra.fipa.FIPAProtocol", new int[] {62,22,64,5},
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_completed", new Term[] {
						new Variable(Type.STRING, "conversation_id",false)
					})
				)
			),
			new Predicate("fipa_state", new Term[] {
				new Variable(Type.STRING, "conversation_id"),
				new Variable(Type.STRING, "state",false)
			}),
			new SynchronizedBlock(
				"astra.fipa.FIPAProtocol", new int[] {62,107,64,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPAProtocol", new int[] {62,107,64,5},
					new Statement[] {
						new BeliefUpdate('-',
							"astra.fipa.FIPAProtocol", new int[] {63,8,64,5},
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "conversation_id"),
								new Variable(Type.STRING, "state")
							})
						)
					}
				)
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
		agent.initialize(
			new Predicate("fipa_conversation_counter", new Term[] {
				Primitive.newPrimitive(0)
			})
		);
		agent.initialize(
			new Predicate("fipa_default_timeout", new Term[] {
				Primitive.newPrimitive(1000)
			})
		);
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
		fragment.addModule("fipa_system",astra.lang.System.class,agent);
		fragment.addModule("fipa_logic",astra.lang.Logic.class,agent);
		fragment.addModule("fipa_prelude",astra.lang.Prelude.class,agent);
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
			astra.core.Agent agent = new FIPAProtocol().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
