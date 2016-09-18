package astra.cartago;

import astra.event.Event;
import astra.formula.Formula;
import astra.term.Primitive;
import astra.term.Term;

public class CartagoPropertyEvent implements Event {
	public static final Primitive<String> ADDED = Primitive.newPrimitive("+");
	public static final Primitive<String> REMOVED = Primitive.newPrimitive("-");
	
	private Term type;
	private Term id;
	private Formula formula;

	public CartagoPropertyEvent(Term type, Term id, Formula event) {
		this.type = type;
		this.id = id;
		this.formula = event;
	}
	
	public String signature() {
		return "$cpe";
	}
	
	public String toString() {
		return type + "$cartago.event(" + id + "," + formula + ")";
	}
	
	public Term type() {
		return type;
	}
	
	public Term id() {
		return id;
	}
	
	public Formula content() {
		return formula;
	}

	@Override
	public Object getSource() {
		return null;
	}
}
