package astra.cartago;

import astra.event.Event;
import astra.formula.Formula;
import astra.term.Primitive;
import astra.term.Term;

public class CartagoSignalEvent implements Event {
	private Term id;
	private Formula formula;

	public CartagoSignalEvent(Term id, Formula event) {
		this.id = id;
		this.formula = event;
	}
	
	public String signature() {
		return "$cse";
	}
	
	public String toString() {
		return "$cartago.signal(" + id + "," + formula + ")";
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
