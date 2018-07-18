package astra.netty;

import astra.term.Term;

public class GETEvent extends RESTEvent {

	public GETEvent(Term context, Term request, Term arguments) {
		super(context, request, arguments);
	}

	@Override
	public String signature() {
		return "$ge";
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("get(").append(arguments).append(")");
		return buf.toString();
	}
}
