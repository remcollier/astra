package astra.tr;

import java.util.Map;

import astra.core.AbstractTask;
import astra.eis.EISAgent;
import astra.formula.Predicate;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.term.Primitive;
import astra.term.Term;
import eis.exceptions.ActException;
import eis.exceptions.NoEnvironmentException;



public class EISAction extends AbstractAction {
	private Term id;
	private Term entity;
	private Predicate predicate;
	ActionHandler handler;
	
	public EISAction(String clazz, int[] data, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.predicate = predicate;
	}
	
	public EISAction(String clazz, int[] data, Term id, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.id = id;
		this.predicate = predicate;
	}

	public EISAction(String clazz, int[] data, Term id, Term entity, Predicate predicate) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.id = id;
		this.entity = entity;
		this.predicate = predicate;
	}
	
	@Override
	public ActionHandler getStatementHandler() {
		if (handler == null) {
			handler = new ActionHandler() {
				private AbstractTask task;
				private int state = 0;
	
				@Override
				public boolean execute(final TRContext context, final Map<Integer, Term> bindings) {
					switch (state) {
					case 0:
						task= new AbstractTask() {
							@SuppressWarnings("unchecked")
							public void doTask() {
								BindingsEvaluateVisitor visitor = new BindingsEvaluateVisitor(bindings, context.agent);
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
									ename = ((Primitive<String>) entity.accept(visitor)).value();
								}
								try {
									agt.invoke(ename, (Predicate) predicate.accept(visitor));
								} catch (ActException e) {
									e.printStackTrace();
								} catch (NoEnvironmentException e) {
									e.printStackTrace();
								}
								state = 2;
							}
	
							@Override
							public Object source() {
								return null;
							}
						};
						context.schedule(task);
						state = 1;

						return true;
					case 1:
						return true;
					case 2:
						task = null;
						state = 0;
					}
					return false;
				}
				
				public String toString() {
					return "state: " + state;
				}
			};
//		} else {
//			System.out.println("using same handler: " + handler.toString());
		}
		return handler;
	}
}
