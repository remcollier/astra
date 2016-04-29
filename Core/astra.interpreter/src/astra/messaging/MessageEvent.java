package astra.messaging;

import astra.event.Event;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.Performative;
import astra.term.Term;
import astra.term.Variable;

public class MessageEvent implements Event {
	private Term performative;
	private Term sender;
	private Formula content;
	private Term params;
	
	public MessageEvent(Term performative, Term sender, Formula content) {
		this(performative, sender, content, null);
	}

	public MessageEvent(Term performative, Term sender, Formula content, Term params) {
		this.performative = performative;
		this.sender = sender;
		this.content = content;
		this.params = params;
	}

	public String signature() {
		return "@message";
	}
	
	public String toString() {
		return "@message(" + performative + "," + sender + "," + content + (params == null ? "":","+params) +")";
	}
	
	public Term performative() {
		return performative;
	}
	
	public Term sender() {
		return sender;
	}
	
	public Formula content() {
		return content;
	}

	@Override
	public Object getSource() {
		return null;
	}

	public Term params() {
		return params;
	}
}
