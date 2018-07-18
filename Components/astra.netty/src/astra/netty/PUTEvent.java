package astra.netty;

import astra.term.Term;

public class PUTEvent extends RESTEvent {
	public Term fields;
	
	public PUTEvent(Term context, Term request, Term arguments, Term fields) {
		super(context, request, arguments);
		this.fields = fields;
	}

	@Override
	public String signature() {
		return "$pte";
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("put(").append(arguments).append(",").append(fields).append(")");
		return buf.toString();
	}
}
