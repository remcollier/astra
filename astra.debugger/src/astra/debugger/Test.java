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

public class Test extends ASTRAClass {
	public Test() {
		setParents(new Class[] {astra.lang.Agent.class});
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
				"astra.debugger.Test", new int[] {14,37,18,5},
				new Statement[] {
					new BeliefUpdate('-',
						"astra.debugger.Test", new int[] {15,8,18,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("off")
						})
					),
					new BeliefUpdate('+',
						"astra.debugger.Test", new int[] {16,8,18,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("on")
						})
					),
					new SpawnGoal(
						"astra.debugger.Test", new int[] {17,8,18,5},
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
				"astra.debugger.Test", new int[] {20,37,24,5},
				new Statement[] {
					new BeliefUpdate('-',
						"astra.debugger.Test", new int[] {21,8,24,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("on")
						})
					),
					new BeliefUpdate('+',
						"astra.debugger.Test", new int[] {22,8,24,5},
						new Predicate("light", new Term[] {
							Primitive.newPrimitive("off")
						})
					),
					new SpawnGoal(
						"astra.debugger.Test", new int[] {23,8,24,5},
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
				new Predicate("turn", new Term[] {
					Primitive.newPrimitive("on")
				})
			)
		);
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
