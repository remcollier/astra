package astra.cartago;

import java.util.LinkedList;

import astra.core.Intention;
import astra.formula.Predicate;
import astra.reasoner.util.ContextEvaluateVisitor;
import astra.statement.AbstractStatement;
import astra.statement.Statement;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;
import astra.term.Variable;
import astra.type.Type;
import cartago.ArtifactId;
import cartago.Op;
import cartago.OpFeedbackParam;


public class CartagoDoStatement extends AbstractStatement {
	private Term artifactId;
	private Predicate action;
	
	public CartagoDoStatement(String clazz, int[] data, Term artifactId, Predicate action) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.artifactId = artifactId;
		this.action = action;
	}
	
	public CartagoDoStatement(String clazz, int[] data, Predicate action) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.action = action;
	}

	public String toString() {
		return toString("");
	}
	
	public String toString(String indent) {
		return indent + "CARTAGO(" + artifactId + ")." + action +")";
	}

	public ICartagoStatementHandler getStatementHandler() {
		return new ICartagoStatementHandler() {
			Op op;
			int state = 0;
			Predicate activity;
			
			@Override
			public boolean execute(Intention context) {
				switch (state) {
				case 0:
					activity = (Predicate) action.accept(new ContextEvaluateVisitor(context));
					// ATTEMPT CARTAGO ACTION...
					LinkedList<Object> list = context.getCartagoAPI().getArguments(activity);
					
					Op operation = null;
					if (list.isEmpty()) {
						operation = new Op(action.predicate());
					} else {
						operation = new Op(action.predicate(), list.toArray());
					}
					
//					System.out.println("[" + context.agent().name() + "] About to perform operation: " + operation);
					
					try {
						long actId = -1;
						if (artifactId != null) {
							Term obj = (Term) artifactId.accept(new ContextEvaluateVisitor(context));
							if (obj instanceof Primitive) {
								Object o = ((Primitive<?>) obj).value();
								if (o instanceof ArtifactId) {
									ArtifactId artId = (ArtifactId) o;
									actId = context.getCartagoAPI().getSession().doAction(artId, operation, null, -1);
								} else if (o instanceof String) {
									actId = context.getCartagoAPI().getSession().doAction(o.toString(), operation, null, -1);
								} else {
									context.failed("Could not handle artifact id type: " + obj.getClass().getName(), null);
									return false;
								}
							} else {
								context.failed("Could not handle artifact id type: " + obj.getClass().getName(), null);
								return false;
							}
//						if ((ctxt = params.get("workspace_id")) != null) {
//							System.out.println("wspid: " + CoreUtilities.toString(ctxt));
//							WorkspaceId wspId = (WorkspaceId) ((ObjectTerm) ctxt).getObject();
//							actId = agent.getSession().doAction(wspId, operation, null, -1);
						} else {
							actId = context.getCartagoAPI().getSession().doAction(operation, null, -1);
						}
						context.getCartagoAPI().registerOperation(actId, context);

						context.suspend();
						state++;
					} catch (Throwable e) {
//						agent.logAction(toString("") + " [NO SUCH ACTION]");
						context.failed("No Such Cartago Action: " + toString(), e);
//						e.printStackTrace();
						return false;
					}
					return true;
				case 1:
					// Create bindings for any unbound variables
					Object paramValues[] = op.getParamValues();

					for (int i=0; i<action.size(); i++) {
						Term term = action.termAt(i);
						if (term instanceof Variable) {
							Variable var = (Variable) term;
							if (paramValues[i] instanceof OpFeedbackParam<?>){
								OpFeedbackParam<?> feedbackParam = (OpFeedbackParam<?>) paramValues[i];
								if (!var.type().equals(Type.getType(feedbackParam.get()))) {
									// We have type incompatibility
									context.failed("Incompatible type for variable: " + var + " Expected: " + var.type() + " but got: " + Type.getType(feedbackParam.get()), null);
									return false;
								}
								
								if (var.type().equals(Type.LIST)) {
									if (!context.updateVariable(var, (ListTerm) feedbackParam.get())) {
										context.addVariable(var, (ListTerm) feedbackParam.get());
									}
								} else {
									if (!context.updateVariable(var, Primitive.newPrimitive(feedbackParam.get()))) {
//										System.out.println("Adding: " + var);
										context.addVariable(var, Primitive.newPrimitive(feedbackParam.get()));
									}
								}
//								if (context.getValue(var) != null) {
//									System.out.println("updating: " + var);
//									if (var.type().equals(Type.LIST)) {
//										context.updateVariable(var, (ListTerm) feedbackParam.get());
//									} else {
//										context.updateVariable(var, Primitive.newPrimitive(feedbackParam.get()));
//									}
//								} else {
//									System.out.println("adding: " + var);
//									if (var.type().equals(Type.LIST)) {
//										context.addVariable(var, (ListTerm) feedbackParam.get());
//									} else {
//										context.addVariable(var, Primitive.newPrimitive(feedbackParam.get()));
//									}
//								}
							} else if (activity.termAt(i) instanceof Variable) {
								context.failed("[" + context.name() + "] Unexpected variable in cartago operation call: " + term, null);
							}
						}
					}
					
					
//					System.out.println("[" + context.name() + "] Completing action: " + activity);
//					context.dumpVariableTables();
//					agent.logAction(toString("") + " [SUCCESS]");
				}
				return false;
			}

			@Override
			public boolean onFail(Intention context) {
				return false;
			}

			@Override
			public Statement statement() {
				return CartagoDoStatement.this;
			}

			@Override
			public void setOperation(Op op) {
				this.op = op;
			}

			public String toString() {
				if (artifactId == null) return "CARTAGO." + activity;
				return "CARTAGO("+ artifactId + ")."+activity;
			}
		};

	}

}
