package astra.lang;

import java.util.logging.Level;
import java.util.logging.Logger;

import astra.core.Agent;
import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
import is.lill.acre.conversation.Conversation;
import is.lill.acre.conversation.ConversationManager;
import is.lill.acre.message.IACREAgentIdentifier;
import is.lill.acre.protocol.Protocol;
import is.lill.acre.protocol.ProtocolDescriptor;
import is.lill.acre.protocol.RepositoryException;
import is.lill.acre.protocol.RepositoryFactory;

/**
 * This API implements additional support for ACRE.
 * 
 * <p>Core ACRE operations are implemented as statements in ASTRA. This class contains 
 * additional operations that are useful to developers using ACRE.
 * </p>
 * 
 * @author Rem Collier
 *
 */
public class ACRE extends Module {
	/**
	 * This internal method is overridden to allow the agent to turn on ACRE support.
	 * By default this is turned off because ACRE does impact on system performance.
	 * 
	 * @param agent the agent instance
	 */
	public void setAgent(Agent agent) {
		super.setAgent(agent);
		agent.useAcre(true);
	}
	
	/**
	 * Action that turns the ACRE Logging framework on (true) or off (false).
	 * 
	 * @param logging the desired state of the logging framework.
	 * @return
	 */
	@ACTION
	public boolean setLogging(boolean logging) {
		Logger.getLogger( ConversationManager.class.getName() ).setLevel( logging ? Level.ALL:Level.OFF );
		return true;
	}
	
	/**
	 * Formula that can be used to check to see if a given protocol exists.
	 * 
	 * @param name the protocol identifier
	 * @return
	 */
	@FORMULA
	public Formula protocolExists(String name) {
		return Agent.protocolManager.getProtocolByDescriptor(ProtocolDescriptor.parseString(name)) != null ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Action that loads a new repository into the ACRE repository manager.
	 * 
	 * @param url the url of the repository
	 * @return
	 */
	@ACTION
	public boolean addRepository(String url) {
		try {
			Agent.protocolManager.addRepository( RepositoryFactory.openRepository( url ));
	    } catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Action that sets the timeout of a given conversation.
	 * 
	 * @param cid the conversation id
	 * @param timeout the timeout length (in milliseconds)
	 * @return
	 */
	@ACTION
	public boolean setTimeout(String cid, int timeout) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		if (con != null) {
			agent.getAcreAPI().getConversationManager().setTimeout(con, timeout);
		}
		return con != null;
	}
	
	/**
	 * Action that tells the conversation manager to forget a conversation
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@ACTION
	public boolean forget(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		if (con != null) {
			agent.getAcreAPI().getConversationManager().forget(cid);
		}
		return con != null;
	}
	
	/**
	 * Term that returns a list of protocols currently loaded into the repository manager
	 * 
	 * @return
	 */
	@TERM
	public ListTerm getKnownProtocols() {
		ListTerm list = new ListTerm();
        for ( Protocol p : Agent.protocolManager.getProtocols() ) {
        	list.add( Primitive.newPrimitive( p.getDescriptor().getUniqueID() ) );
        }
		return list;
	}
	
	/**
	 * Term that gets the status of a conversation
	 *  
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getStatus(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		return con.getStatus().toString();
	}

	/**
	 * Term that gets the name of the participant in a conversation
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getParticipant(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		IACREAgentIdentifier participant = agent.getAcreAPI().getConversationManager().getOtherParticipant( con );
		return participant.getName();
	}

	/**
	 * Term that gets the protocol id for a given conversation
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolID(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getUniqueID();
	}

	/**
	 * Term that gets the name of the protocol for a given conversation
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolName(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getName();
	}

	/**
	 * Term that gets the namespace of the protocol used for a given conversation.
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getProtocolNamespace(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		ProtocolDescriptor pid = con.getProtocol().getDescriptor();
		return pid.getNamespace();
	}

	/**
	 * Term that gets the state of a conversation.
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public String getState(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		return con.getState().getName();
	}

	/**
	 * Term that gets the length of a conversation.
	 * 
	 * @param cid the conversation id
	 * @return
	 */
	@TERM
	public int getLength(String cid) {
		Conversation con = agent.getAcreAPI().getConversationManager().getConversationByID(cid);
		return con.getLength();
	}
}
