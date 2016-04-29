package astra.core;

import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.statement.Statement;

public class Rule {
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
}
