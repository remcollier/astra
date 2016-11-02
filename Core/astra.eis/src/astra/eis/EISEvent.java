package astra.eis;

import astra.event.Event;
import astra.term.Term;

public class EISEvent implements Event {
	public static final char ADDED = '+';
	public static final char REMOVED = '-';
	public static final char ENVIRONMENT = ' ';
	
	private char type;
	private Term entity;
	private Term formula;

	public EISEvent(char type, Term term) {
		this(type, null, term);
	}
	
	public EISEvent(char type, Term entity, Term event) {
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
	
	public Term content() {
		return formula;
	}

	@Override
	public Object getSource() {
		return null;
	}
}
