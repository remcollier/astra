package astra.acre;

import astra.event.Event;
import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;

public class AcreEvent implements Event {
	public static final Primitive<String> STARTED = Primitive.newPrimitive("started");
	public static final Primitive<String> ADVANCED = Primitive.newPrimitive("advanced");
	public static final Primitive<String> UNMATCHED = Primitive.newPrimitive("unmatched");
	public static final Primitive<String> AMBIGUOUS= Primitive.newPrimitive("ambiguous");
	public static final Primitive<String> ENDED = Primitive.newPrimitive("ended");
	public static final Primitive<String> FAILED = Primitive.newPrimitive("failed");
	public static final Primitive<String> CANCEL_REQUEST = Primitive.newPrimitive("cancel_request");
	public static final Primitive<String> CANCEL_FAIL = Primitive.newPrimitive("cancel_fail");
	public static final Primitive<String> CANCEL_CONFIRM = Primitive.newPrimitive("cancel_confirm");
	public static final Primitive<String> TIMEOUT = Primitive.newPrimitive("timeout");
	public static final Primitive<String> MESSAGE = Primitive.newPrimitive("message");

//	@acre(ended, string cid)
//	@acre(failed, string cid)
//	@acre(cancel-request, string cid)
//	@acre(cancel-fail, string cid)
//	@acre(cancel-confirm, string cid)
//	@acre(timeout, string cid)
	
	Term type;
	Term conversationId;
	Term state;
	Term sender;
	Term length;
	Term performative;
	Predicate content;
	
	public AcreEvent(Term type, Term conversationId) {
	   // this is used quite hackishly elsewhere
	   // for ambiguous and unmatched messages, the second parameter is the 'description' string
	   // and is not a conversation identifier
	   this.type=type;
		this.conversationId = conversationId;
	}
	
	public AcreEvent(Term type, Term cid, Term state, Term length) {
		this.type=type;
		this.conversationId = cid;
		this.state = state;
		this.length = length;
	}

	public AcreEvent(Term type, Term conversationId, Term performative, Predicate content) {
	   this.type=type;
	   this.conversationId = conversationId;
	   this.performative = performative;
	   this.content = content;
	}
	
	@Override
	public String signature() {
		return "@acre:"+type;
	}

	@Override
	public String toString() {
	      StringBuilder toReturn = new StringBuilder( "@acre(" );
	      if ( this.type.equals( ADVANCED ) ) {
	         toReturn.append( this.type );
	         toReturn.append( ", " + this.conversationId.toString() );
	         toReturn.append( ", " + this.state.toString() );
	         toReturn.append(  ", " + this.length.toString() );
	      }
	      else if ( type.equals( MESSAGE ) ) {
	         toReturn.append( this.type );
	         toReturn.append( ", " + this.conversationId.toString() );
	         toReturn.append( ", " + this.performative );
	         toReturn.append( ", " + this.content.toString() );
	      }
	      else {
	         toReturn.append( this.type + ", "  + this.conversationId.toString() );
	      }
	      toReturn.append( ")" );
	      return toReturn.toString();
	}

	public Term type() {
		return type;
	}

	public Term performative() {
		return performative;
	}

	public Term conversationId() {
		return conversationId;
	}
	
	public Term length() {
		return this.length;
	}
	
	public Term sender() {
		return this.sender;
	}
	
	public Term state() {
		return state;
	}

	public Predicate content() {
		return content;
	}

	@Override
	public Object getSource() {
		return null;
	}
}
