package astra.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import astra.event.Event;
import astra.formula.Formula;
import astra.formula.IsDone;
import astra.formula.Predicate;
import astra.statement.Statement;

public class Rule extends AbstractElement {
	public Event event;
	public Formula context;
	public Statement statement;
	public Formula dropCondition;
	public Map<String, List<Rule>> rules = new HashMap<String, List<Rule>>();
	public Set<String> filter = new HashSet<String>();
	
	public Rule(Event event, Statement statement) {
		this(event, Predicate.TRUE, new IsDone(), statement);
	}
	
	public Rule(Event event, Formula context, Statement statement) {
		this(event, context, new IsDone(), statement);
	}
	
	public Rule(Event event, Formula context, Formula dropCondition, Statement statement) {
		this.event = event;
		this.context = context;
		this.dropCondition = dropCondition;
		this.statement = statement;
	}

	public Rule(String clazz, int[] data, Event event, Statement statement) {
		this(clazz, data, event, Predicate.TRUE, new IsDone(), statement);
	}
	
	public Rule(String clazz, int[] data, Event event, Formula context, Statement statement) {
		this(clazz, data, event, context, new IsDone(), statement);
	}
	public Rule(String clazz, int[] data, Event event, Formula context, Statement statement, Rule[] rules) {
		this(clazz, data, event, context, new IsDone(), statement);
		
		for (int i=0;i<rules.length;i++) {
			System.out.println("adding: " + rules[i]);
			addRule(rules[i]);
		}
	}
	
	public Rule(String clazz, int[] data, Event event, Formula context, Formula dropCondition, Statement statement) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.event = event;
		this.context = context;
		this.dropCondition = dropCondition;
		this.statement = statement;
	}

	public Rule(String clazz, int[] data, Event event, Formula context, Formula dropCondition, Statement statement, Rule[] rules) {
		this(clazz, data, event, context, dropCondition, statement);

		for (int i=0;i<rules.length;i++) {
			addRule(rules[i]);
		}
	}

	public Map<String, List<Rule>> rules() {
		return rules;
	}
	
	public void addRule(Rule rule) {
		Helper.addRule(rules, filter, rule);
	}

	public Set<String> filter() {
		return filter;
	}
}
