package astra.netty;

import astra.event.Event;
import astra.term.Term;

public abstract class RESTEvent implements Event {
	public Term context;
	public Term request;
	public Term arguments;
	
	public RESTEvent(Term context, Term request, Term arguments) {
		this.context = context;
		this.request = request;
		this.arguments = arguments;
	}

	@Override
	public Object getSource() {
		return null;
	}
}
