package astra.lang;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import astra.acre.ACREService;
import astra.acre.AcreAPI;
import astra.acre.AcreEvent;
import astra.core.ActionParam;
import astra.core.Agent;
import astra.core.AgentMessageListener;
import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.messaging.MessageService;
import astra.reasoner.Unifier;
import astra.reasoner.unifier.AcreEventUnifier;
import astra.reasoner.util.ContentCodec;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import is.lill.acre.conversation.Conversation;
import is.lill.acre.conversation.ConversationManager;
import is.lill.acre.conversation.ConversationStatus;
import is.lill.acre.message.ACREAgentIdentifier;
import is.lill.acre.message.ACREMessage;
import is.lill.acre.message.IACREAgentIdentifier;
import is.lill.acre.message.IACREMessage;
import is.lill.acre.protocol.Protocol;
import is.lill.acre.protocol.ProtocolDescriptor;
import is.lill.acre.protocol.ProtocolManager;
import is.lill.acre.protocol.ProtocolVersion;
import is.lill.acre.protocol.RepositoryException;
import is.lill.acre.protocol.RepositoryFactory;

/**
 * This API implements additional support for ACRE.
 * 
 * <p>
 * Core ACRE operations are implemented as statements in ASTRA. This class
 * contains additional operations that are useful to developers using ACRE.
 * </p>
 * 
 * @author Rem Collier
 *
 */
public class ACRE extends Module {
	private AcreAPI acreAPI;
	public static final ProtocolManager protocolManager = new ProtocolManager();

	static {
		Unifier.eventFactory.put(AcreEvent.class, new AcreEventUnifier());
	}

	/**
	 * This internal method is overridden to allow the agent to turn on ACRE
	 * support. By default this is turned off because ACRE does impact on system
	 * performance.
	 * 
	 * @param agent
	 *            the agent instance
	 */
	public void setAgent(Agent agent) {
		acreAPI = new AcreAPI(agent);
		agent.addSource(acreAPI);
		agent.addAgentMessageListener(new AgentMessageListener() {
			@Override
			public void receive(AstraMessage message) {
				ACREMessage m = ACREService.message(message);
				acreAPI.getConversationManager().processMessage(m);
			}
		});
		super.setAgent(agent);
	}

	/**
	 * Action that turns the ACRE Logging framework on (true) or off (false).
	 * 
	 * @param logging
	 *            the desired state of the logging framework.
	 * @return
	 */
	@ACTION
	public boolean setLogging(boolean logging) {
		Logger.getLogger(ConversationManager.class.getName()).setLevel(logging ? Level.ALL : Level.OFF);
		return true;
	}

	/**
	 * Formula that can be used to check to see if a given protocol exists.
	 * 
	 * @param name
	 *            the protocol identifier
	 * @return
	 */
	@FORMULA
	public Formula protocolExists(String name) {
		return protocolManager.getProtocolByDescriptor(ProtocolDescriptor.parseString(name)) != null ? Predicate.TRUE
				: Predicate.FALSE;
	}

	/**
	 * Action that loads a new repository into the ACRE repository manager.
	 * 
	 * @param url
	 *            the url of the repository
	 * @return
	 */
	@ACTION
	public boolean addRepository(String url) {
		try {
			protocolManager.addRepository(RepositoryFactory.openRepository(url));
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Action that sets the timeout of a given conversation.
	 * 
	 * @param cid
	 *            the conversation id
	 * @param timeout
	 *            the timeout length (in milliseconds)
	 * @return
	 */
	@ACTION
	public boolean setTimeout(String cid, int timeout) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		if (con != null) {
			acreAPI.getConversationManager().setTimeout(con, timeout);
		}
		return con != null;
	}

	/**
	 * Action that tells the conversation manager to forget a conversation
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@ACTION
	public boolean forget(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		if (con != null) {
			acreAPI.getConversationManager().forget(cid);
		}
		return con != null;
	}

	/**
	 * Term that returns a list of protocols currently loaded into the
	 * repository manager
	 * 
	 * @return
	 */
	@TERM
	public ListTerm getKnownProtocols() {
		ListTerm list = new ListTerm();
		for (Protocol p : protocolManager.getProtocols()) {
			list.add(Primitive.newPrimitive(p.getDescriptor().getUniqueID()));
		}
		return list;
	}

	/**
	 * Term that gets the status of a conversation
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getStatus(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		return con.getStatus().toString();
	}

	/**
	 * Term that gets the name of the participant in a conversation
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getParticipant(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		IACREAgentIdentifier participant = acreAPI.getConversationManager().getOtherParticipant(con);
		return participant.getName();
	}

	/**
	 * Term that gets the protocol id for a given conversation
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolID(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getUniqueID();
	}

	/**
	 * Term that gets the name of the protocol for a given conversation
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolName(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getName();
	}

	/**
	 * Term that gets the namespace of the protocol used for a given
	 * conversation.
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolNamespace(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getNamespace();
	}

	/**
	 * Term that gets the state of a conversation.
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public String getState(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		return con.getState().getName();
	}

	/**
	 * Term that gets the length of a conversation.
	 * 
	 * @param cid
	 *            the conversation id
	 * @return
	 */
	@TERM
	public int getLength(String cid) {
		Conversation con = acreAPI.getConversationManager().getConversationByID(cid);
		return con.getLength();
	}

	@SENSOR
	public void sense() {
		acreAPI.update();
	}

	@ACTION
	public boolean advance(String performative, String c_id, Funct content) {
		Predicate c = new Predicate(content.functor(), content.terms());

		Conversation con = acreAPI.getConversationManager().getConversationByID(c_id);
		if (con == null) {
			System.out.println("Unknown Conversation: " + c_id);
			return false;
		} else {
			ACREMessage m = new ACREMessage();
			m.setPerformative(performative);
			m.setReceiver(acreAPI.getConversationManager().getOtherParticipant(con));
			m.setSender(acreAPI.getAcreIdentifier());
			m.setContent(c.toString());
			m.setConversationIdentifier(c_id);
			m.setProtocol(con.getProtocol().getDescriptor());
			m.setLanguage("ASTRA");

			Conversation toAdvance = acreAPI.getConversationManager().processMessage(m);
			if (toAdvance != null) {
				AstraMessage message = ACREService.messageWithContent(m, c);

				if (!MessageService.send(message)) {
					toAdvance.setStatus(ConversationStatus.FAILED);
					System.out.println("Failed to send message");
					return false;
				}
			} else {
				System.out.println("Attempt to Send Invalid Message for Conversation: " + c_id);
				return false;
			}
		}

		return true;
	}

	@ACTION
	public boolean start(String protocolName, String receiver, String performative, Funct content,
			ActionParam<String> cid) {
		Predicate c = new Predicate(content.functor(), content.terms());
		ProtocolDescriptor pd = ProtocolDescriptor.parseString(protocolName);
		Protocol protocol = matchProtocol(pd);
		if (protocol != null) {
			ACREMessage m = new ACREMessage();
			m.setPerformative(performative);
			m.setReceiver(new ACREAgentIdentifier(receiver));
			m.setSender(acreAPI.getAcreIdentifier());
			m.setContent(content.toString());
			m.setConversationIdentifier(acreAPI.getConversationManager().getNextConversationId());
			m.setProtocol(protocol.getDescriptor());
			m.setLanguage("ASTRA");
			AstraMessage message = ACREService.messageWithContent(m, c);

			Conversation con = acreAPI.getConversationManager().processMessage(m);

			if (!MessageService.send(message)) {
				con.setStatus(ConversationStatus.FAILED);
				System.out.println("Failed to send message");
				return false;
			}
			cid.set(con.getConversationIdentifier());
		} else {
			System.out.println("Failed to Match Protocol: " + protocolName);
			return false;
		}

		return true;
	}

	private Protocol matchProtocol(ProtocolDescriptor pd) {
		Protocol toReturn = null;

		// find how many protocols match the descriptor
		Set<Protocol> matches = protocolManager.getProtocolsMatchingDescriptor(pd);

		// no matching protocols
		if (matches.isEmpty()) {
			System.out.println("Failed to Match Protocol: " + pd.toString());
		} else if (matches.size() == 1) {
			for (Protocol p : matches) {
				toReturn = p;
			}
		}

		// multiple matches but name and namespace must match
		// pick the latest version number
		else if (pd.getNamespace() != null) {
			ProtocolVersion maxVersion = null;
			for (Protocol p : matches) {
				if (p.getDescriptor().getVersion().isLaterThan(maxVersion)) {
					toReturn = p;
					maxVersion = p.getDescriptor().getVersion();
				}
			}
		}

		// multiple matches from different namespaces:
		// ambiguous message
		else {
			System.out.println("Ambiguous Protocol: " + pd.toString());
		}

		return toReturn;
	}

	@ACTION
	public boolean cancel(String cid) {
		IACREMessage am = acreAPI.getConversationManager().cancel(cid);
		if (am != null) {
			AstraMessage toSend = new AstraMessage();
			toSend.performative = am.getPerformative();
			toSend.sender = am.getSender().getName();
			toSend.receivers.add(am.getReceiver().getName());
			toSend.language = am.getLanguage();
			toSend.protocol = am.getProtocol().toString();
			toSend.conversationId = am.getConversationIdentifier();
			toSend.content = ContentCodec.getInstance().encode(new Predicate("", new Term[0]));

			if (!MessageService.send(toSend)) {
				// toAdvance.setStatus(ConversationStatus.FAILED);
				System.out.println("Failed to send message");
				return false;
			}
		} else {
			System.out.println("Failed to cancel Conversation: " + cid);
			return false;
		}
		return true;
	}

	@ACTION
	public boolean confirmCancel(String cid) {
		IACREMessage am = acreAPI.getConversationManager().confirmCancel(cid);
		if (am != null) {
			AstraMessage toSend = new AstraMessage();
			toSend.performative = am.getPerformative();
			toSend.sender = am.getSender().getName();
			toSend.receivers.add(am.getReceiver().getName());
			toSend.language = am.getLanguage();
			toSend.protocol = am.getProtocol().toString();
			toSend.conversationId = am.getConversationIdentifier();
			toSend.content = ContentCodec.getInstance().encode(new Predicate("", new Term[0]));

			if (!MessageService.send(toSend)) {
				System.out.println("Failed to send message");
				return false;
			}
		} else {
			System.out.println("Failed to confirm cancellation of Conversation: " + cid);
			return false;
		}
		return true;
	}

	@ACTION
	public boolean denyCancel(String cid) {
		IACREMessage am = acreAPI.getConversationManager().failCancel(cid);
		if (am != null) {
			AstraMessage toSend = new AstraMessage();
			toSend.performative = am.getPerformative();
			toSend.sender = am.getSender().getName();
			toSend.receivers.add(am.getReceiver().getName());
			toSend.language = am.getLanguage();
			toSend.protocol = am.getProtocol().toString();
			toSend.conversationId = am.getConversationIdentifier();
			toSend.content = ContentCodec.getInstance().encode(new Predicate("", new Term[0]));

			if (!MessageService.send(toSend)) {
				System.out.println("Failed to send message");
				return false;
			}
		} else {
			System.out.println("Failed to confirm cancellation of Conversation: " + cid);
			return false;
		}
		return true;

	}
}
