package astra.netty;

import astra.term.Term;

public class POSTEvent extends RESTEvent {
	public Term fields;
	
	public POSTEvent(Term context, Term request, Term arguments, Term fields) {
		super(context, request, arguments);
		this.fields = fields;
	}

	@Override
	public String signature() {
		return "$pe";
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("post(").append(arguments).append(",").append(fields).append(")");
		return buf.toString();
	}
}
