package astra.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import astra.debugger.Breakpoints;
import astra.event.Event;
import astra.event.ModuleEvent;
import astra.formula.Formula;
import astra.reasoner.Unifier;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.reasoner.util.Utilities;
import astra.term.Term;

public class Helper {
	private static Event resolveEvent(Rule rule, Agent agent) {
		Event event = rule.event;
//		System.out.println("Event: " + event);
		if (event instanceof ModuleEvent) {
			event = ((ModuleEvent) event).adaptor().generate(agent,((ModuleEvent) event).event());
		}
		return event;
	}

	public static boolean handleEvent(Event event, Agent agent, Map<String, List<Rule>> rules, Intention intention) {
		List<Rule> list = rules.get(event.signature());
		if (list == null) return false;
		
		for (Rule rule : list) {
//			System.out.println("Rule: " + rule.event);
			Event _event = resolveEvent(rule, agent);
//			System.out.println("event: " + _event);
			if (_event != null) {
				Map<Integer, Term> bindings = Unifier.unify(_event, event, agent);
				Breakpoints.getInstance().check(agent, rule, _event, bindings);
				if (bindings != null) {
					Formula context = rule.context;
					if (intention != null) {
//						System.out.println("Context: " +context);
						context = (Formula) context.accept(new ContextEvaluateVisitor(intention));
//						System.out.println("Context: " +context);
					}
//					System.out.println("bindings: "+ bindings);
					List<Map<Integer, Term>> results = agent.query(context, bindings);
//					System.out.println("results: " + results);
					
					Breakpoints.getInstance().check(agent, rule, context, results);
					if (results != null) {
						if (!results.isEmpty()) {
							bindings.putAll(results.get(0));
						}
	
//						System.out.println("Selected Rule: " + rule.statement);
						if (intention != null) {
							// Intention level event
//							System.out.println("[" + agent.name() + "] Intention level event: " + event);
//							System.out.println("[" + agent.name() + "] Bindings: " + Utilities.merge(intention.bindings, bindings));
//							System.out.println(intention.getBindings());
							intention.addSubGoal(event, rule, Utilities.merge(intention.getBindings(), bindings));
							intention.resume();
						} else if (event.getSource() != null) {
							intention = (Intention) event.getSource();
							intention.addSubGoal(event, rule, bindings);
							intention.resume();
						} else {
							agent.addIntention(new Intention(agent, event, rule, bindings));
						}
						return true;
					}
				}				
			}
		}
		
		return false;
	}

	public static void addRule(Map<String, List<Rule>> rules, Set<String> filter, Rule rule) {
		List<Rule> list = rules.get(rule.event.signature());
		if (list == null) {
			filter.add(rule.event.signature());
			list = new LinkedList<Rule>();
			rules.put(rule.event.signature(), list);
		}
		
		list.add(rule);
	}

}
