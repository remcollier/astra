package astra.statement;

import astra.core.AbstractTask;
import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.VariableVisitor;



public class ModuleCall extends AbstractStatement {
	String module;
	Predicate method;
	ModuleCallAdaptor adaptor;
	
	public ModuleCall(ModuleCallAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public ModuleCall(String module, String clazz, int[] data, Predicate method, ModuleCallAdaptor moduleCallAdaptor) {
		this.setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.module = module;
		this.method = method;
		this.adaptor = moduleCallAdaptor;
	}

	public ModuleCall(String module, Predicate method, ModuleCallAdaptor moduleCallAdaptor) {
		this.module = module;
		this.method = method;
		this.adaptor = moduleCallAdaptor;
	}

	@Override
	public StatementHandler getStatementHandler() {
		return new StatementHandler() {
			int state = 0;
			AbstractTask task;
			@Override
			public boolean execute(final Intention context) {
				switch (state) {
				case 0:
					task= new AbstractTask() {
						public void doTask() {
							VariableVisitor visitor = new VariableVisitor();
							method.accept(visitor);
							context.addUnboundVariables(visitor.variables());
							context.resetActionParams();
							try {
								// THIS IS THE PROBLEM - THE ADAPTOR IS CALLED
								// FROM HERE, AND THE CONTEXT IS RESUMED.
								// NEED A SUSPEND NOTIFY METHOD TO ALLOW AUTO ACTIONS
								// TO CIRCUMVENT THIS...
								// DEFAULT SHOULD BE TO NOT SKIP NOTIFICATION...
								
								// ALTERNATIVE IS TO SUSPEND THE ACTION (BLOCK) AND 
								// WAIT TO BE NOTIFIED
								if (!adaptor.invoke(context, method)) {
									context.notifyDone("Failed Action: " + module + "." + method);
								}
								context.applyActionParams();
								if (!adaptor.suppressNotification()) context.notifyDone(null);
							} catch (Throwable th) {
								context.notifyDone("Failed Action: " + module + "." + method, th);
							}
						}

						@Override
						public Object source() {
							return null;
						}
					};
					context.suspend();
					if (context.getModule(null, module).inline()) {
						task.doTask();
					} else {
						context.schedule(task);
					}
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
			
			public String toString() {
				return method.toString();
			}
			
			@Override
			public Statement statement() {
				return ModuleCall.this;
			}
		};
	}
	
}
