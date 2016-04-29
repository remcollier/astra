package astra.acre;

import is.lill.acre.conversation.Conversation;
import is.lill.acre.conversation.ConversationStatus;
import is.lill.acre.message.ACREMessage;
import is.lill.acre.protocol.Protocol;
import is.lill.acre.protocol.ProtocolDescriptor;
import is.lill.acre.protocol.ProtocolVersion;

import java.util.Set;

import astra.core.Agent;
import astra.core.Intention;
import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.messaging.MessageService;
import astra.statement.AbstractStatement;
import astra.statement.Statement;
import astra.statement.StatementHandler;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;
import astra.util.ContextEvaluateVisitor;

public class AcreAdvance extends AbstractStatement {
	private Term performative;
	private Predicate content;
	private Term cid;
	
	public AcreAdvance(String clazz, int[] data, Term performative, Term cid, Predicate content) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.performative = performative;
		this.content = content;
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
				Performative p = (Performative) performative.accept(visitor);
				Predicate c = (Predicate) content.accept(visitor);
				
				Conversation con = context.getAcreAPI().getConversationManager().getConversationByID(c_id.value());
				if (con == null) {
					context.notifyDone("Unknown Conversation: " + c_id.value());
				} else {
					ACREMessage m = new ACREMessage();
					m.setPerformative(p.value());
					m.setReceiver(context.getAcreAPI().getConversationManager().getOtherParticipant(con));
					m.setSender(context.getAcreAPI().getAcreIdentifier());
					m.setContent(c.toString());
					m.setConversationIdentifier(c_id.value());
					m.setProtocol(con.getProtocol().getDescriptor());
					m.setLanguage("ASTRA");
					
					Conversation toAdvance = context.getAcreAPI().getConversationManager().processMessage(m);
					if (toAdvance != null) {
						AstraMessage message = ACREService.messageWithContent(m,c);
					
						if (!MessageService.send(message)) {
							toAdvance.setStatus(ConversationStatus.FAILED);
							context.notifyDone("Failed to send message");
						}
					} else {
						context.notifyDone("Attempt to Send Invalid Message for Conversation: " + c_id);
					}
				}

				
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return AcreAdvance.this;
			}
			
			public String toString() {
				return "acre_advance(" + performative.toString() + "," + cid.toString() + "," + content.toString() + ")";
			}
			
		};
	}

	private Protocol matchProtocol( Intention context, ProtocolDescriptor pd ) {

        Protocol toReturn = null;

        // find how many protocols match the descriptor
        Set<Protocol> matches = Agent.protocolManager.getProtocolsMatchingDescriptor( pd );

        // no matching protocols
        if ( matches.isEmpty() ) {
			context.notifyDone("Failed to Match Protocol: " + pd.toString());
        }
        else if ( matches.size() == 1 ) {
            for ( Protocol p : matches ) {
                toReturn = p;
            }
        }

        // multiple matches but name and namespace must match
        // pick the latest version number
        else if ( pd.getNamespace() != null ) {
            ProtocolVersion maxVersion = null;
            for ( Protocol p : matches ) {
                if ( p.getDescriptor().getVersion().isLaterThan( maxVersion ) ) {
                    toReturn = p;
                    maxVersion = p.getDescriptor().getVersion();
                }
            }
        }

        // multiple matches from different namespaces:
        // ambiguous message
        else {
			context.notifyDone("Ambiguous Protocol: " + pd.toString());
        }
        
        return toReturn;
    }	
}
