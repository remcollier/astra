package astra.acre;

import is.lill.acre.message.ACREAgentIdentifier;
import is.lill.acre.message.ACREMessage;
import is.lill.acre.protocol.ProtocolDescriptor;
import is.lill.acre.protocol.ProtocolManager;

import java.util.ArrayList;
import java.util.List;

import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.reasoner.util.ContentCodec;
import astra.term.Primitive;
import astra.term.Term;

public class ACREService {
	public static AstraMessage messageWithContent(ACREMessage m, Predicate content) {
		AstraMessage message = new AstraMessage();
		message.performative = m.getPerformative();
		message.sender = m.getSender().getName();
		message.receivers.add(m.getReceiver().getName());
		message.content = ContentCodec.getInstance().encode(content);
		message.language = m.getLanguage();
		if (m.getProtocol() != null) {
			message.protocol = m.getProtocol().getUniqueID();
			message.conversationId = m.getConversationIdentifier();
		}
		if (m.getReplyWith() != null) message.replyWith = m.getReplyWith();
		if (m.getInReplyTo() != null) message.replyTo = m.getInReplyTo();
		if (m.getReplyBy() != null) message.replyBy = m.getReplyBy().toString();
		return message;
	}

	public static ACREMessage message(AstraMessage message) {
		ACREMessage m = new ACREMessage();
		m.setPerformative(message.performative);
		m.setReceiver(new ACREAgentIdentifier(message.receivers.get(0)));
		m.setSender(new ACREAgentIdentifier(message.sender));
		m.setContent(message.content == null ? "" : message.content.toString());
		m.setConversationIdentifier(message.conversationId);
		m.setLanguage(message.language);
		if (message.protocol != null && !message.protocol.equals("none")) {
			m.setProtocol(ProtocolDescriptor.parseString(message.protocol));
		}
		if (message.replyWith != null) m.setReplyWith( message.replyWith );
		if (message.inReplyTo != null) m.setInReplyTo( message.inReplyTo );
		if (message.replyBy != null) m.setReplyBy( Long.parseLong(message.replyBy) );
		return m;
	}
	
	public static Predicate toPredicate(String content) {
		int index = content.indexOf('(');
		if (index == -1) {
			return new Predicate(content, new Term[0]);
		} else {
			char ch;
			String identifier = content.substring(0, index);
			List<Term> terms = new ArrayList<Term>();
			List<Term> termList = null;
			boolean inList = false;
			index++;
			while ((ch = content.charAt(index)) != ')') {

			   if (ch == '[') {
//			      inList = true;
//			      termList = new ArrayList<ITerm>();
//			      index++;
				   System.out.println("Need to implement support for lists in ACREService");
			      continue;
			   }
//			   if (ch == ']') {
//			      inList = false;
//			      terms.add(new astra.term.List( termList.toArray( new ITerm[0] ) ));
//			      index++;
//			      continue;
//			   }
				if (ch == ',' || ch == ' ') {
					index++;
					continue;
				}
				if (ch == '"') {
					// Have a string, so add it...
				   // TODO: add support for escaped double quotes within the string
					int i = content.indexOf('"', index+1);
					if ( inList ) { 
					   termList.add(Primitive.newPrimitive(content.substring(index+1,i)));
					}
					else {
					   terms.add(Primitive.newPrimitive(content.substring(index+1, i)));
					}
					index = i+1;
				} else {
					StringBuffer buf = new StringBuffer();
					while (ch != ',' && ch != ')' && ch != ']' && ch != ' ') {
						buf.append(ch);
						ch = content.charAt(++index);
					}
					//index++;
					String number = buf.toString();
					
					try {
						int val = Integer.parseInt(number);
						if ( inList ) {
						   termList.add(Primitive.newPrimitive(val));
						}
						else {
						   terms.add(Primitive.newPrimitive(val));
						}
					} catch (NumberFormatException nfe) {
						try {
							long val = Long.parseLong(number);
							if ( inList ) {
	                     termList.add(Primitive.newPrimitive(val));
	                  }
	                  else {
	                     terms.add(Primitive.newPrimitive(val));
	                  }
						} catch (NumberFormatException nfe2) {
							try {
								float val = Float.parseFloat(number);
								if ( inList ) {
		                     termList.add(Primitive.newPrimitive(val));
		                  }
		                  else {
		                     terms.add(Primitive.newPrimitive(val));
		                  }
							} catch (NumberFormatException nfe3) {
								try {
									double val = Double.parseDouble(number);
									if ( inList ) {
			                     termList.add(Primitive.newPrimitive(val));
			                  }
			                  else {
			                     terms.add(Primitive.newPrimitive(val));
			                  }
								} catch (NumberFormatException nfe4) {
									System.out.println("[ACREService.toPredicate] failed to parse: " + number + " with: " + ch);
								}
							}
						}
					}
					if (ch == ')') break;
				}
			}
			
			return new Predicate(identifier, terms.toArray(new Term[terms.size()]));
		}
	}
	
	public static void main(String[] args) {
		System.out.println(toPredicate("likes(\"rem\",\"beer\")"));
		System.out.println(toPredicate("likes(3,44444444444444)"));
		System.out.println(toPredicate("likes(3.33435, 7 )"));
		System.out.println( toPredicate( "stocks([[\"here\" , 3 ],[\"Goofup Capital\",\"Applet Computer\",\"Noidea Corporation\",\"Western Dirigible\",\"Fakebook Intl.\",\"Lamazon Corp.\",\"Motoroller International\",\"Dead Hat\",\"fleaBay\",\"McCoffee International\"]])" ) );
	}
}
