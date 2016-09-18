package astra.gui;
import astra.event.Event;
import astra.term.ListTerm;
import astra.term.Term;

public class GuiEvent implements Event {
	Term type;
	ListTerm args;
	
	public GuiEvent(Term type) {
		this.type = type;
		args = new ListTerm();
	}
	
	public GuiEvent(Term id, ListTerm args) {
		this.type = id;
		this.args = args;
	}

	@Override
	public Object getSource() {
		return null;
	}

	@Override
	public String signature() {
		return "$gui:";
	}
}
