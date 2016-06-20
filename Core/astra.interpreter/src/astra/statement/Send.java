package astra.statement;

import astra.core.Intention;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.messaging.AstraMessage;
import astra.messaging.MessageService;
import astra.reasoner.util.ContentCodec;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Performative;
import astra.term.Primitive;
import astra.term.Term;

public class Send extends AbstractStatement {
	Term performative;
	Term name;
	Formula content;
	Term params;
	
	public Send(String clazz, int[] data, Term performative, Term name, Formula content) {
		this(clazz, data, performative, name, content, null);
	}

	public Send(String clazz, int[] data, Term performative, Term name, Formula content, Term params) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.performative = performative;
		this.name = name;
		this.content = content;
		this.params = params;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean execute(Intention context) {
				try {
					ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context); 
					AstraMessage message = new AstraMessage();
					message.sender = context.name();
					Term receiver = (Term) name.accept(visitor);
					if (Primitive.class.isInstance(receiver)) {
						message.receivers.add(((Primitive<String>) receiver).value());
					} else {
						for (Term term : (ListTerm) receiver) {
							message.receivers.add(((Primitive<String>) term).value());
						}
					}
					if (ListTerm.class.isInstance(params)) {
						for (Term t : (ListTerm) params) {
							if (Funct.class.isInstance(t)) {
								Funct funct = (Funct) t;
								if (funct.size() > 1) {
									context.failed("Unexpected Param in send(...): " + funct);
									return false;
								}
								
								if (funct.functor().equals("protocol")) {
									message.protocol = ((Primitive<?>) funct.termAt(0)).value().toString();
								} else if (funct.functor().equals("conversationId")) {
									message.conversationId = ((Primitive<?>) funct.termAt(0)).value().toString();
								} else {
									context.failed("Unexpected Param in send(...): " + funct);
									return false;
								}
							}
						}
						
					}
					message.performative = ((Performative) performative.accept(visitor)).value();
					message.content = ContentCodec.getInstance().encode((Formula) content.accept(visitor));
					
	//				ACREAPI api = context.agent().getAPI(ACREAPI.class);
	//				if ( api != null ) {
	//					ACREMessage m = new ACREMessage();
	//					m.setPerformative(perf);
	//					m.setReceiver(new ACREAgentIdentifier( r ) );
	//					m.setSender(api.getAcreIdentifier());
	//					m.setContent(c.toString());
	//					m.setLanguage("ASTRA");
	//					api.getConversationManager().processMessage(m);
	//				}
	
					if (!MessageService.send(message)) {
						context.failed("Failed to send message");
					}
				} catch (Throwable th) {
					context.failed("Unexpected Error", th);
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return Send.this;
			}
			
			public String toString() {
				return "send(" + performative + "," + name + "," + content + ")";
			}
		};
	}
}
