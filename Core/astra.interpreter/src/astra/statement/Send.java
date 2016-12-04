package astra.statement;

import java.util.HashMap;

import astra.core.Intention;
import astra.formula.Formula;
import astra.formula.ModuleFormula;
import astra.messaging.AstraMessage;
import astra.messaging.MessageService;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.reasoner.util.ContentCodec;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.term.FormulaTerm;
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
					
					if (params != null) {
						Object parameters = params.accept(visitor);
						if (ListTerm.class.isInstance(parameters)) {
	//						System.out.println("params: " + parameters);
							for (Term t : (ListTerm) parameters) {
								if (Funct.class.isInstance(t)) {
									Funct funct = (Funct) ((Funct) t).accept(visitor);
									if (funct.size() > 1) {
										context.failed("Unexpected Param in send(...): " + funct);
										return false;
									}
									
									if (funct.functor().equals("protocol")) {
										message.protocol = ((Primitive<?>) funct.termAt(0)).value().toString();
									} else if (funct.functor().equals("conversation_id")) {
										message.conversationId = ((Primitive<?>) funct.termAt(0)).value().toString();
									} else {
										context.failed("Unexpected Param in send(...): " + funct);
										return false;
									}
								}
							}
						}
					}
					message.performative = ((Performative) performative.accept(visitor)).value();
					
					// Process the content...
					Formula ctnt = null;
					Object cnt = content.accept(visitor);
					if (cnt instanceof FormulaTerm) {
						ctnt = ((FormulaTerm) cnt).value();
					} else if (cnt instanceof ModuleFormula) {
						// This is normally executed by the resolution algorithm, but that is
						// not used here. This replaces content defined as a module formula
						// with the actual content.
						ctnt = ((ModuleFormula) cnt).adaptor().invoke(new BindingsEvaluateVisitor(new HashMap<Integer, Term>(), context.agent), ((ModuleFormula) cnt).predicate());
					} else if (cnt instanceof Formula) {
						ctnt = (Formula) cnt;
					} else {
						context.failed("Invalid Formula type: " + cnt.getClass().getCanonicalName());
						return false;
					}
//					System.out.println("content: " + ctnt);
					message.content = ContentCodec.getInstance().encode(ctnt);
					
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
