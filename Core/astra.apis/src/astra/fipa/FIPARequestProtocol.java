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


public class FIPARequestProtocol extends ASTRAClass {
	public FIPARequestProtocol() {
		setParents(new Class[] {FIPAProtocol.class});
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_request", new Term[] {
						new Variable(Type.STRING, "receiver",false),
						new Variable(Type.FUNCTION, "action",false),
						new Variable(Type.FUNCTION, "result",true)
					})
				)
			),
			new Predicate("fipa_default_timeout", new Term[] {
				new Variable(Type.INTEGER, "timeout",false)
			}),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {4,113,6,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {5,8,6,5},
						new Goal(
							new Predicate("fipa_request", new Term[] {
								new Variable(Type.STRING, "receiver"),
								new Variable(Type.FUNCTION, "action"),
								new Variable(Type.INTEGER, "timeout"),
								new Variable(Type.FUNCTION, "result")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_request", new Term[] {
						new Variable(Type.STRING, "receiver",false),
						new Variable(Type.FUNCTION, "action",false),
						new Variable(Type.INTEGER, "timeout",false),
						new Variable(Type.FUNCTION, "answer",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {8,90,24,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {10,8,24,5},
						new Goal(
							new Predicate("fipa_conversation_id", new Term[] {
								new Variable(Type.STRING, "id",false)
							})
						)
					),
					new Send("astra.fipa.FIPARequestProtocol", new int[] {12,8,12,121},
						new Performative("request"),
						new Variable(Type.STRING, "receiver"),
						new ModuleFormula("fipa_logic",
							new Predicate("toPredicate", new Term[] {
								new Variable(Type.FUNCTION, "action")
							}),
						new ModuleFormulaAdaptor() {
								public Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
									return ((astra.lang.Logic) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toPredicate(
										(astra.term.Funct) visitor.evaluate(predicate.getTerm(0))
									);
							}
						}
							),
						new ListTerm(new Term[] {
							new Funct("conversation_id", new Term[] {
								new Variable(Type.STRING, "id")
							}),
							new Funct("protocol", new Term[] {
								Primitive.newPrimitive("fipa_request_protocol")
							})
						})
					),
					new SpawnGoal(
						"astra.fipa.FIPARequestProtocol", new int[] {14,8,24,5},
						new Goal(
							new Predicate("fipa_timeout", new Term[] {
								new Variable(Type.STRING, "id"),
								new Variable(Type.INTEGER, "timeout")
							})
						)
					),
					new When(
						"astra.fipa.FIPARequestProtocol", new int[] {16,8,24,5},
						new AND(
							new Predicate("fipa_state", new Term[] {
								new Variable(Type.STRING, "id"),
								new Variable(Type.STRING, "state",false)
							}),
							new ModuleFormula("fipa_prelude",
								new Predicate("contains", new Term[] {
									new ListTerm(new Term[] {
										Primitive.newPrimitive("COMPLETED"),
										Primitive.newPrimitive("FAILED"),
										Primitive.newPrimitive("REFUSED"),
										Primitive.newPrimitive("CANCELLED")
									}),
									new Variable(Type.STRING, "state")
								}),
							new ModuleFormulaAdaptor() {
									public Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
										return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).contains(
											(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
											(java.lang.String) visitor.evaluate(predicate.getTerm(1))
										);
								}
							}
								)
						),
						new Block(
							"astra.fipa.FIPARequestProtocol", new int[] {16,123,23,9},
							new Statement[] {
								new If(
									"astra.fipa.FIPARequestProtocol", new int[] {17,12,23,9},
									new ModuleFormula("fipa_prelude",
										new Predicate("contains", new Term[] {
											new ListTerm(new Term[] {
												Primitive.newPrimitive("REFUSED"),
												Primitive.newPrimitive("CANCELLED"),
												Primitive.newPrimitive("FAILED")
											}),
											new Variable(Type.STRING, "state")
										}),
									new ModuleFormulaAdaptor() {
											public Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
												return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).contains(
													(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
													(java.lang.String) visitor.evaluate(predicate.getTerm(1))
												);
										}
									}
										),
									new Block(
										"astra.fipa.FIPARequestProtocol", new int[] {17,82,19,13},
										new Statement[] {
											new Subgoal(
												"astra.fipa.FIPARequestProtocol", new int[] {18,16,19,13},
												new Goal(
													new Predicate("fipa_failed", new Term[] {
														new Variable(Type.STRING, "id")
													})
												)
											)
										}
									),
									new If(
										"astra.fipa.FIPARequestProtocol", new int[] {19,19,23,9},
										new Predicate("fipa_completed", new Term[] {
											new Variable(Type.STRING, "id"),
											new Variable(Type.FUNCTION, "answer")
										}),
										new Block(
											"astra.fipa.FIPARequestProtocol", new int[] {19,51,22,13},
											new Statement[] {
												new BeliefUpdate('-',
													"astra.fipa.FIPARequestProtocol", new int[] {20,16,22,13},
													new Predicate("fipa_completed", new Term[] {
														new Variable(Type.STRING, "id"),
														new Variable(Type.FUNCTION, "answer")
													})
												),
												new Subgoal(
													"astra.fipa.FIPARequestProtocol", new int[] {21,16,22,13},
													new Goal(
														new Predicate("fipa_completed", new Term[] {
															new Variable(Type.STRING, "id")
														})
													)
												)
											}
										)
									)
								)
							}
						)
					)
				}
			)
		));
		addRule(new Rule(
			new BeliefEvent('+',
				new Predicate("fipa_timedout", new Term[] {
					new Variable(Type.STRING, "id",false)
				})
			),
			new Predicate("fipa_state", new Term[] {
				new Variable(Type.STRING, "id"),
				Primitive.newPrimitive("SENT")
			}),
			new SynchronizedBlock(
				"astra.fipa.FIPARequestProtocol", new int[] {26,73,28,5},
				"synchronized",
				new Block(
					"astra.fipa.FIPARequestProtocol", new int[] {26,73,28,5},
					new Statement[] {
						new Subgoal(
							"astra.fipa.FIPARequestProtocol", new int[] {27,8,28,5},
							new Goal(
								new Predicate("fipa_set_state", new Term[] {
									new Variable(Type.STRING, "id"),
									Primitive.newPrimitive("FAILED")
								})
							)
						)
					}
				)
			)
		));
		addRule(new Rule(
			new MessageEvent(
				new Performative("refuse"),
				new Variable(Type.STRING, "sender",false),
				new FormulaVariable(new Variable(Type.FORMULA,"action")),
				new Variable(Type.LIST, "params",false)
			),
			new AND(
				new Predicate("fipa_state", new Term[] {
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("conversation_id")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("NEW")
				}),
				new Comparison("==",
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("protocol")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("fipa_request_protocol")
				)
			),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {32,91,34,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {33,8,34,5},
						new Goal(
							new Predicate("fipa_set_state", new Term[] {
								new ModuleTerm("fipa_prelude", Type.STRING,
									new Predicate("stringValueFor", new Term[] {
										new Variable(Type.LIST, "params"),
										Primitive.newPrimitive("conversation_id")
									}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
												(java.lang.String) visitor.evaluate(predicate.getTerm(1))
											);
										}
									}
								),
								Primitive.newPrimitive("REFUSED")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new MessageEvent(
				new Performative("agree"),
				new Variable(Type.STRING, "sender",false),
				new FormulaVariable(new Variable(Type.FORMULA,"action")),
				new Variable(Type.LIST, "params",false)
			),
			new AND(
				new Predicate("fipa_state", new Term[] {
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("conversation_id")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("NEW")
				}),
				new Comparison("==",
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("protocol")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("fipa_request_protocol")
				)
			),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {38,91,40,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {39,8,40,5},
						new Goal(
							new Predicate("fipa_set_state", new Term[] {
								new ModuleTerm("fipa_prelude", Type.STRING,
									new Predicate("stringValueFor", new Term[] {
										new Variable(Type.LIST, "params"),
										Primitive.newPrimitive("conversation_id")
									}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
												(java.lang.String) visitor.evaluate(predicate.getTerm(1))
											);
										}
									}
								),
								Primitive.newPrimitive("AGREED")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new MessageEvent(
				new Performative("inform"),
				new Variable(Type.STRING, "sender",false),
				new FormulaVariable(new Variable(Type.FORMULA,"answer")),
				new Variable(Type.LIST, "params",false)
			),
			new AND(
				new Predicate("fipa_state", new Term[] {
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("conversation_id")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("AGREED")
				}),
				new Comparison("==",
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("protocol")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("fipa_request_protocol")
				)
			),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {44,91,48,5},
				new Statement[] {
					new Declaration(
						new Variable(Type.STRING, "id"),
						"astra.fipa.FIPARequestProtocol", new int[] {45,8,48,5},
						new ModuleTerm("fipa_prelude", Type.STRING,
							new Predicate("stringValueFor", new Term[] {
								new Variable(Type.LIST, "params"),
								Primitive.newPrimitive("conversation_id")
							}),
							new ModuleTermAdaptor() {
								public Object invoke(Intention intention, Predicate predicate) {
									return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
										(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
										(java.lang.String) intention.evaluate(predicate.getTerm(1))
									);
								}
								public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
									return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
										(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
										(java.lang.String) visitor.evaluate(predicate.getTerm(1))
									);
								}
							}
						)
					),
					new BeliefUpdate('+',
						"astra.fipa.FIPARequestProtocol", new int[] {46,8,48,5},
						new Predicate("fipa_completed", new Term[] {
							new Variable(Type.STRING, "id"),
							new ModuleTerm("fipa_logic", Type.FUNCTION,
								new Predicate("toFunctor", new Term[] {
									new Variable(Type.FORMULA, "answer")
								}),
								new ModuleTermAdaptor() {
									public Object invoke(Intention intention, Predicate predicate) {
										return ((astra.lang.Logic) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toFunctor(
											(astra.formula.Formula) intention.evaluate(predicate.getTerm(0))
										);
									}
									public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
										return ((astra.lang.Logic) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toFunctor(
											(astra.formula.Formula) visitor.evaluate(predicate.getTerm(0))
										);
									}
								}
							)
						})
					),
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {47,8,48,5},
						new Goal(
							new Predicate("fipa_set_state", new Term[] {
								new Variable(Type.STRING, "id"),
								Primitive.newPrimitive("COMPLETED")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new MessageEvent(
				new Performative("failure"),
				new Variable(Type.STRING, "sender",false),
				new FormulaVariable(new Variable(Type.FORMULA,"action")),
				new Variable(Type.LIST, "params",false)
			),
			new AND(
				new Predicate("fipa_state", new Term[] {
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("conversation_id")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("AGREED")
				}),
				new Comparison("==",
					new ModuleTerm("fipa_prelude", Type.STRING,
						new Predicate("stringValueFor", new Term[] {
							new Variable(Type.LIST, "params"),
							Primitive.newPrimitive("protocol")
						}),
						new ModuleTermAdaptor() {
							public Object invoke(Intention intention, Predicate predicate) {
								return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
									(java.lang.String) intention.evaluate(predicate.getTerm(1))
								);
							}
							public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
								return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
									(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
									(java.lang.String) visitor.evaluate(predicate.getTerm(1))
								);
							}
						}
					),
					Primitive.newPrimitive("fipa_request_protocol")
				)
			),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {52,91,54,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {53,8,54,5},
						new Goal(
							new Predicate("fipa_set_state", new Term[] {
								new ModuleTerm("fipa_prelude", Type.STRING,
									new Predicate("stringValueFor", new Term[] {
										new Variable(Type.LIST, "params"),
										Primitive.newPrimitive("conversation_id")
									}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
												(java.lang.String) intention.evaluate(predicate.getTerm(1))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
												(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
												(java.lang.String) visitor.evaluate(predicate.getTerm(1))
											);
										}
									}
								),
								Primitive.newPrimitive("FAILED")
							})
						)
					)
				}
			)
		));
		addRule(new Rule(
			new MessageEvent(
				new Performative("request"),
				new Variable(Type.STRING, "sender",false),
				new FormulaVariable(new Variable(Type.FORMULA,"action")),
				new Variable(Type.LIST, "params",false)
			),
			new Comparison("==",
				new ModuleTerm("fipa_prelude", Type.STRING,
					new Predicate("stringValueFor", new Term[] {
						new Variable(Type.LIST, "params"),
						Primitive.newPrimitive("protocol")
					}),
					new ModuleTermAdaptor() {
						public Object invoke(Intention intention, Predicate predicate) {
							return ((astra.lang.Prelude) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
								(astra.term.ListTerm) intention.evaluate(predicate.getTerm(0)),
								(java.lang.String) intention.evaluate(predicate.getTerm(1))
							);
						}
						public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
							return ((astra.lang.Prelude) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_prelude")).stringValueFor(
								(astra.term.ListTerm) visitor.evaluate(predicate.getTerm(0)),
								(java.lang.String) visitor.evaluate(predicate.getTerm(1))
							);
						}
					}
				),
				Primitive.newPrimitive("fipa_request_protocol")
			),
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {57,91,73,5},
				new Statement[] {
					new Subgoal(
						"astra.fipa.FIPARequestProtocol", new int[] {58,8,73,5},
						new Goal(
							new Predicate("fipa_request_validate", new Term[] {
								new Variable(Type.STRING, "sender"),
								new ModuleTerm("fipa_logic", Type.FUNCTION,
									new Predicate("toFunctor", new Term[] {
										new Variable(Type.FORMULA, "action")
									}),
									new ModuleTermAdaptor() {
										public Object invoke(Intention intention, Predicate predicate) {
											return ((astra.lang.Logic) intention.getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toFunctor(
												(astra.formula.Formula) intention.evaluate(predicate.getTerm(0))
											);
										}
										public Object invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
											return ((astra.lang.Logic) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toFunctor(
												(astra.formula.Formula) visitor.evaluate(predicate.getTerm(0))
											);
										}
									}
								),
								new Variable(Type.BOOLEAN, "outcome",false)
							})
						)
					),
					new If(
						"astra.fipa.FIPARequestProtocol", new int[] {60,8,73,5},
						new ModuleFormula("fipa_logic",
							new Predicate("toPredicate", new Term[] {
								new Variable(Type.BOOLEAN, "outcome")
							}),
						new ModuleFormulaAdaptor() {
								public Formula invoke(BindingsEvaluateVisitor visitor, Predicate predicate) {
									return ((astra.lang.Logic) visitor.agent().getModule("astra.fipa.FIPARequestProtocol","fipa_logic")).toPredicate(
										(boolean) visitor.evaluate(predicate.getTerm(0))
									);
							}
						}
							),
						new Block(
							"astra.fipa.FIPARequestProtocol", new int[] {60,45,70,9},
							new Statement[] {
								new TryRecover(
									"astra.fipa.FIPARequestProtocol", new int[] {61,12,70,9},
									new Block(
										"astra.fipa.FIPARequestProtocol", new int[] {61,16,67,13},
										new Statement[] {
											new Send("astra.fipa.FIPARequestProtocol", new int[] {62,16,62,51},
												new Performative("agree"),
												new Variable(Type.STRING, "sender"),
												new FormulaVariable(new Variable(Type.FORMULA,"action")),
												new Variable(Type.LIST, "params")
											),
											new Send("astra.fipa.FIPARequestProtocol", new int[] {64,16,64,52},
												new Performative("cancel"),
												new Variable(Type.STRING, "sender"),
												new FormulaVariable(new Variable(Type.FORMULA,"action")),
												new Variable(Type.LIST, "params")
											)
										}
									),
									new Block(
										"astra.fipa.FIPARequestProtocol", new int[] {67,22,70,9},
										new Statement[] {
											new Send("astra.fipa.FIPARequestProtocol", new int[] {68,16,68,53},
												new Performative("failure"),
												new Variable(Type.STRING, "sender"),
												new FormulaVariable(new Variable(Type.FORMULA,"action")),
												new Variable(Type.LIST, "params")
											)
										}
									)
								)
							}
						),
						new Block(
							"astra.fipa.FIPARequestProtocol", new int[] {70,15,73,5},
							new Statement[] {
								new Send("astra.fipa.FIPARequestProtocol", new int[] {71,12,71,48},
									new Performative("refuse"),
									new Variable(Type.STRING, "sender"),
									new FormulaVariable(new Variable(Type.FORMULA,"action")),
									new Variable(Type.LIST, "params")
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
					new Predicate("fipa_request_validate", new Term[] {
						new Variable(Type.STRING, "sender",false),
						new Variable(Type.FUNCTION, "action",false),
						new Variable(Type.BOOLEAN, "result",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {75,86,77,5},
				new Statement[] {
					new Assignment(
						new Variable(Type.BOOLEAN, "result"),
						"astra.fipa.FIPARequestProtocol", new int[] {76,8,77,5},
						Primitive.newPrimitive(true)
					)
				}
			)
		));
		addRule(new Rule(
			new GoalEvent('+',
				new Goal(
					new Predicate("fipa_request_execute", new Term[] {
						new Variable(Type.STRING, "sender",false),
						new Variable(Type.FUNCTION, "action",false),
						new Variable(Type.FUNCTION, "answer",true)
					})
				)
			),
			Predicate.TRUE,
			new Block(
				"astra.fipa.FIPARequestProtocol", new int[] {79,83,81,5},
				new Statement[] {
					new Assignment(
						new Variable(Type.FUNCTION, "answer"),
						"astra.fipa.FIPARequestProtocol", new int[] {80,8,81,5},
						new Funct("done", new Term[] {})
					)
				}
			)
		));
	}

	public void initialize(astra.core.Agent agent) {
	}

	public Fragment createFragment(astra.core.Agent agent) throws ASTRAClassNotFoundException {
		Fragment fragment = new Fragment(this);
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
			astra.core.Agent agent = new FIPARequestProtocol().newInstance(name);
			agent.initialize(new Goal(new Predicate("main", new Term[] { argList })));
			Scheduler.schedule(agent);
		} catch (AgentCreationException e) {
			e.printStackTrace();
		} catch (ASTRAClassNotFoundException e) {
			e.printStackTrace();
		};
	}
}
