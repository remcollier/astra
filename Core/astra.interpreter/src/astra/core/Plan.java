package astra.core;

import astra.formula.Predicate;
import astra.statement.Statement;

public class Plan {
	public Predicate id;
	public Statement statement;
	
	public Plan(Predicate id, Statement statement) {
		this.id = id;
		this.statement = statement;
	}
	
	public Predicate id() {
		return id;
	}
}
