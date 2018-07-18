package astra.netty;

import astra.term.Term;

public class DELETEEvent extends RESTEvent {

	public DELETEEvent(Term context, Term request, Term arguments) {
		super(context, request, arguments);
	}

	@Override
	public String signature() {
		return "$de";
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("delete(").append(arguments).append(")");
		return buf.toString();
	}
}
