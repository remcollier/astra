package astra.core;

import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.statement.Statement;

public class Rule extends AbstractElement {
	public Event event;
	public Formula context;
	public Statement statement;
	
	public Rule(Event event, Statement statement) {
		this(event, Predicate.TRUE, statement);
	}
	
	public Rule(Event event, Formula true1, Statement statement) {
		this.event = event;
		this.context = true1;
		this.statement = statement;
	}

	public Rule(String clazz, int[] data, Event event, Statement statement) {
		this(clazz, data, event, Predicate.TRUE, statement);
	}
	
	public Rule(String clazz, int[] data, Event event, Formula context, Statement statement) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.event = event;
		this.context = context;
		this.statement = statement;
	}
}
