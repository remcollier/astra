package astra.acre;

import is.lill.acre.conversation.Conversation;
import is.lill.acre.conversation.ConversationStatus;
import is.lill.acre.message.ACREAgentIdentifier;
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
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.statement.AbstractStatement;
import astra.statement.Statement;
import astra.statement.StatementHandler;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;

public class AcreStart extends AbstractStatement {
	private Term protocol;
	private Term receiver;
	private Term performative;
	private Predicate content;
	private Term cid;
	
	public AcreStart(String clazz, int[] data, Term protocol, Term receiver, Term performative, Predicate content, Term cid) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.protocol = protocol;
		this.receiver = receiver;
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
				Primitive<String> p = (Primitive<String>) protocol.accept(visitor);
				Primitive<String> r = (Primitive<String>) receiver.accept(visitor);
				Performative pt = (Performative) performative.accept(visitor);
				Predicate c = (Predicate) content.accept(visitor);
				
				ProtocolDescriptor pd = ProtocolDescriptor.parseString(p.value());
				Protocol protocol = matchProtocol(context, pd);
				if (protocol != null) {
					ACREMessage m = new ACREMessage();
					m.setPerformative(pt.value());
					m.setReceiver(new ACREAgentIdentifier(r.value()));
					m.setSender(context.getAcreAPI().getAcreIdentifier());
					m.setContent(c.toString());
					m.setConversationIdentifier(context.getAcreAPI().getConversationManager().getNextConversationId());
					m.setProtocol(protocol.getDescriptor());
					m.setLanguage("ASTRA");
					AstraMessage message = ACREService.messageWithContent(m,c);
					
					Conversation con = context.getAcreAPI().getConversationManager().processMessage(m);
					
					if (!MessageService.send(message)) {
						con.setStatus(ConversationStatus.FAILED);
						context.notifyDone("Failed to send message");
					}
					context.addVariable((Variable) cid, Primitive.newPrimitive(con.getConversationIdentifier()));
				} else {
					context.notifyDone("Failed to Match Protocol: " + p);
				}

				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return AcreStart.this;
			}
			
			public String toString() {
				return "acre_start(" + protocol.toString() + "," + receiver.toString() + "," + performative.toString() + "," +
						content.toString() + "," + cid.toString() + ")";
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
