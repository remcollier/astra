package astra.acre;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.core.Agent;
import astra.formula.AcreFormula;
import astra.formula.Formula;
import astra.lang.ACRE;
import astra.reasoner.Queryable;
import astra.reasoner.util.LogicUtilities;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Variable;
import is.lill.acre.conversation.Conversation;
import is.lill.acre.conversation.ConversationManager;
import is.lill.acre.event.ACREEvent;
import is.lill.acre.event.AmbiguousMessageEvent;
import is.lill.acre.event.ConversationAdvancedEvent;
import is.lill.acre.event.ConversationCancelConfirmEvent;
import is.lill.acre.event.ConversationCancelFailEvent;
import is.lill.acre.event.ConversationCancelRequestEvent;
import is.lill.acre.event.ConversationEndedEvent;
import is.lill.acre.event.ConversationFailedEvent;
import is.lill.acre.event.ConversationStartedEvent;
import is.lill.acre.event.ConversationTimeoutEvent;
import is.lill.acre.event.IConversationEvent;
import is.lill.acre.event.UnmatchedMessageEvent;
import is.lill.acre.logic.Utilities;
import is.lill.acre.message.ACREAgentIdentifier;
import is.lill.acre.message.IACREAgentIdentifier;
import is.lill.acre.message.IACREMessage;

public class AcreAPI implements Observer,Queryable {
	private ConversationManager conversationManager;
	private IACREAgentIdentifier acreId;

	private Agent agent;

	public AcreAPI(Agent agent) {
		this.agent = agent;
		Logger.getLogger( ConversationManager.class.getName() ).setLevel( Level.OFF );

		acreId = new ACREAgentIdentifier(agent.name());

		conversationManager = new ConversationManager();
		conversationManager.setOwner(acreId);
		conversationManager.setProtocolManager(ACRE.protocolManager);
		conversationManager.addObserver(this);

		// NOTE: May need to observe protocol manager too...

		// for now, syntax of 'acre' and 'astra' is the same
		Utilities.addTermParser("astra", Utilities.getTermParser("acre"));
		Utilities.addTermFormatter("astra", Utilities.getTermFormatter("acre"));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		ACREEvent e = (ACREEvent) arg1;

		if (e instanceof IConversationEvent) {
			Conversation c = ((IConversationEvent) e).getConversation();
			if (e instanceof ConversationStartedEvent) {
				// @acre(started, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.STARTED, Primitive
						.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationEndedEvent) {
				// @acre(ended, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.ENDED, Primitive
						.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationFailedEvent) {
				// @acre(failed, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.FAILED, Primitive
						.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationCancelRequestEvent) {
				// @acre(cancel_request, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.CANCEL_REQUEST,
						Primitive.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationCancelFailEvent) {
				// @acre(cancel_fail, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.CANCEL_FAIL, Primitive
						.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationCancelConfirmEvent) {
				// @acre(cancel_confirm, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.CANCEL_CONFIRM,
						Primitive.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationTimeoutEvent) {
				// @acre(timeout, string cid)
				agent.addEvent(new AcreEvent(AcreEvent.TIMEOUT, Primitive
						.newPrimitive(c.getConversationIdentifier())));
			} else if (e instanceof ConversationAdvancedEvent) {
				// @acre(advanced, string cid, string state, int length)
				ConversationAdvancedEvent event = (ConversationAdvancedEvent) e;
				agent.addEvent(new AcreEvent(AcreEvent.ADVANCED, 
						Primitive.newPrimitive(c.getConversationIdentifier()),
						Primitive.newPrimitive(event.getState().getName()),
						Primitive.newPrimitive(event.getLength())));

				// @acre(message, string cid, performative, predicate content)
				IACREMessage m = c.getHistory().get(event.getLength() - 1);
				agent.addEvent(new AcreEvent(AcreEvent.MESSAGE, Primitive
						.newPrimitive(c.getConversationIdentifier()), Primitive
						.newPrimitive(m.getPerformative()), ACREService
						.toPredicate(m.getContent())));
			}
		} else if (e instanceof UnmatchedMessageEvent) {
			// @acre(unmatched, string description)
			UnmatchedMessageEvent event = (UnmatchedMessageEvent) e;
			IACREMessage m = event.getMessage();

			String details = "performative=["
					+ m.getPerformative().toLowerCase() + "],sender=["
					+ m.getSender().getName() + "],content=[" + m.getContent()
					+ "]";

			if (m.getProtocol() != null) {
				details += ",protocol=[" + m.getProtocol().getUniqueID() + "]";
			}
			if (m.getConversationIdentifier() != null) {
				details += ",conversation=[" + m.getConversationIdentifier()
						+ "]";
			}

			agent.addEvent(new AcreEvent(AcreEvent.UNMATCHED, Primitive
					.newPrimitive(details)));

		} else if (e instanceof AmbiguousMessageEvent) {
			// @acre(ambiguous, string description)
			AmbiguousMessageEvent event = (AmbiguousMessageEvent) e;
			IACREMessage m = event.getMessage();

			agent.addEvent(new AcreEvent(AcreEvent.AMBIGUOUS, Primitive
					.newPrimitive("performative=["
							+ m.getPerformative().toLowerCase() + "],sender=["
							+ m.getSender().getName() + "],content=["
							+ m.getContent() + "]")));
		}

	}

	public void update() {
		conversationManager.removeFinished();
        conversationManager.doTimeouts();
    }
	
    public IACREAgentIdentifier getAcreIdentifier() {
        return acreId;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Formula> getMatchingFormulae(Formula predicate) {
		List<Formula> list = new LinkedList<Formula>();
		if (predicate instanceof AcreFormula) {
			AcreFormula formula = (AcreFormula) predicate;
		
			List<Conversation> conversations = new ArrayList<Conversation>();
			if (formula.cid() instanceof Variable) {
				conversations.addAll(conversationManager.getAllConversations().values());
			} else {
				conversations.add(conversationManager.getConversationByID(((Primitive<String>) formula.cid()).value()));
			}
			
			// Check all possible conversations
			for (Conversation conversation : conversations) {
				if (formula.index() instanceof Variable) {
					int i = 0;
					for (IACREMessage message : conversation.getHistory()) {
						addMessage(list, conversation, message, i);
						i++;
					}
				} else {
					int index = ((Primitive<Integer>) formula.index()).value();
					List<IACREMessage> history = conversation.getHistory();
					if (index <= history.size() && index >= 1) {
						addMessage(list, conversation, history.get(index-1), index-1);
					}
				}
			}
		}
		
		return list;
	}
	
	private void addMessage(List<Formula> list, Conversation conversation, IACREMessage message, int i) {
		Primitive<String> type = null;
		IACREAgentIdentifier participant = conversationManager.getOtherParticipant( conversation );
	    if ( message.getSender().equals( participant ) ) {
			type = Primitive.newPrimitive("send");
		} else if ( message.getReceiver().equals( participant ) ) {
			type = Primitive.newPrimitive("receive");
		}
	    
		list.add(new AcreFormula(
				Primitive.newPrimitive(conversation.getConversationIdentifier()),
				Primitive.newPrimitive(i),
				type,
				new Performative(message.getPerformative()),
				LogicUtilities.toPredicate(message.getContent())
		));
	}
}
