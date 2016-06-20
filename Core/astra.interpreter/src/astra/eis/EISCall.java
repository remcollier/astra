package astra.eis;

import astra.core.AbstractTask;
import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.statement.AbstractStatement;
import astra.statement.Statement;
import astra.statement.StatementHandler;
import astra.term.Primitive;
import astra.term.Term;

public class EISCall extends AbstractStatement {
	private Term id;
	private Term entity;
	private Predicate predicate;
	
	public EISCall(String clazz, int[] data, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.predicate = predicate;
	}

	public EISCall(String clazz, int[] data, Term entity, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.entity = entity;
		this.predicate = predicate;
	}

	public EISCall(String clazz, int[] data, Term id, Term entity, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.id = id;
		this.entity = entity;
		this.predicate = predicate;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {

			private AbstractTask task;
			private int state = 0;

			@Override
			public boolean execute(final Intention context) {
				switch (state ) {
				case 0:
					task= new AbstractTask() {
						@SuppressWarnings("unchecked")
						public void doTask() {
							ContextEvaluateVisitor visitor = new ContextEvaluateVisitor(context);
							EISAgent agt = null;
							if (id == null) {
								agt = context.getEISAgent(null);
							} else {
								agt = context.getEISAgent(((Primitive<String>) id.accept(visitor)).value());
							}
							String ename = null;
							if (entity == null) {
								ename = agt.defaultEntity();
							} else {
								ename = ((Primitive<String>) entity .accept(visitor)).value();
							}
							try {
								agt.invoke(ename, (Predicate) predicate.accept(visitor));
								context.notifyDone(null);
							} catch (Throwable th) {
								context.notifyDone("Failed EIS Action", th);
							}
						}

						@Override
						public Object source() {
							return null;
						}
					};
					context.suspend();
					context.schedule(task);
					state = 1;
					return true;
				case 1:
					task = null;
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return EISCall.this;
			}
			
			public String toString() {
				return "EIS." + predicate;
			}
			
		};
	}

}
