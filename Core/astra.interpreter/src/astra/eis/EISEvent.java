package astra.eis;

import astra.event.Event;
import astra.formula.Formula;
import astra.term.Term;

public class EISEvent implements Event {
	public static final char ADDED = '+';
	public static final char REMOVED = '-';
	public static final char ENVIRONMENT = ' ';
	
	private char type;
	private Term entity;
	private Formula formula;

	public EISEvent(char type, Formula event) {
		this(type, null, event);
	}
	
	public EISEvent(char type, Term entity, Formula event) {
		this.type = type;
		this.entity = entity;
		this.formula = event;
	}

	public String signature() {
		return "$eis";
	}
	
	public String toString() {
		return type + "$eis(" + (entity == null ? "":entity+",") + formula + ")";
	}
	
	public char type() {
		return type;
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
