package astra.acre;

import is.lill.acre.message.IACREMessage;
import astra.core.Intention;
import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.messaging.MessageService;
import astra.statement.AbstractStatement;
import astra.statement.Statement;
import astra.statement.StatementHandler;
import astra.term.Primitive;
import astra.term.Term;
import astra.util.ContentCodec;
import astra.util.ContextEvaluateVisitor;

public class AcreCancel extends AbstractStatement {
	private Term cid;
	
	public AcreCancel(String clazz, int[] data, Term cid) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.cid = cid;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean execute(final Intention context) {
				ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context);
				Primitive<String> c_id = (Primitive<String>) cid.accept(visitor);
				
				IACREMessage am = context.getAcreAPI().getConversationManager().cancel( c_id.value() );
			      if ( am != null ) {
			         AstraMessage toSend = new AstraMessage();
			         toSend.performative = am.getPerformative();
			         toSend.sender = am.getSender().getName();
			         toSend.receivers.add( am.getReceiver().getName() );
			         toSend.language = am.getLanguage();
			         toSend.protocol = am.getProtocol().toString();
			         toSend.conversationId = am.getConversationIdentifier();
			         toSend.content = ContentCodec.getInstance().encode(new Predicate( "", new Term[0] ));
			         
					if (!MessageService.send(toSend)) {
//						toAdvance.setStatus(ConversationStatus.FAILED);
						context.notifyDone("Failed to send message");
					}
			      }
			      else {
			    	  context.notifyDone("Failed to cancel Conversation: " + c_id);
			      }
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return AcreCancel.this;
			}
			
			public String toString() {
				return "acre_cancel(" + cid.toString() + ")";
			}
			
		};
	}
}
