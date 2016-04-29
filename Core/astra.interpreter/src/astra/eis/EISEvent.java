package astra.eis;

import astra.event.Event;
import astra.formula.Formula;
import astra.term.Term;

public class EISEvent implements Event {
	public static final char ADDED = '+';
	public static final char REMOVED = '-';
	public static final char ENVIRONMENT = ' ';
	
	private char type;
	private Term id;
	private Term entity;
	private Formula formula;

	public EISEvent(char type, Term id, Formula event) {
		this(type, id, null, event);
	}
	
	public EISEvent(char type, Term id, Term entity, Formula event) {
		this.type = type;
		this.id = id;
		this.entity = entity;
		this.formula = event;
	}

	public String signature() {
		return type + "@eis:";
	}
	
	public String toString() {
		return type + "@eis(" + id + (entity == null ? "":"," + entity) + "," + formula + ")";
	}
	
	public char type() {
		return type;
	}
	
	public Term id() {
		return id;
	}
	
	public Term entity() {
		return entity;
	}
	
	public Formula content() {
		return formula;
	}

	@Override
	public Object getSource() {
		return null;
	}
}
