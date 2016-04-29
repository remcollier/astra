package astra.tr;

import java.util.LinkedList;
import java.util.Map;

import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;
import astra.util.BindingsEvaluateVisitor;
import cartago.ArtifactId;
import cartago.Op;



public class CartagoAction extends AbstractAction {
	private Term artifactId;
	private Predicate action;
	
	public CartagoAction(String clazz, int[] data, Term artifactId, Predicate action) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.artifactId = artifactId;
		this.action = action;
	}

	public CartagoAction(String clazz, int[] data, Predicate action) {
		setLocation(clazz, data[0], data[1], data[2], data[3]);
		this.action = action;
	}

	@Override
	public ActionHandler getStatementHandler() {
		return new ActionHandler() {
			private int state = 0;

			@Override
			public boolean execute(final TRContext context, final Map<Integer, Term> bindings) {
				Predicate activity = (Predicate) action.accept(new BindingsEvaluateVisitor(bindings,context.agent));
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
					if (artifactId != null) {
						Term obj = (Term) artifactId.accept(new BindingsEvaluateVisitor(bindings,context.agent));
						if (obj instanceof Primitive) {
							Object o = ((Primitive<?>) obj).value();
							if (o instanceof ArtifactId) {
								ArtifactId artId = (ArtifactId) o;
								context.getCartagoAPI().getSession().doAction(artId, operation, null, -1);
							} else if (o instanceof String) {
								context.getCartagoAPI().getSession().doAction(o.toString(), operation, null, -1);
							} else {
								System.err.println("Could not handle artifact id type: " + obj.getClass().getName());
								return false;
							}
						} else {
							System.err.println("Could not handle artifact id type: " + obj.getClass().getName());
							return false;
						}
					} else {
						context.getCartagoAPI().getSession().doAction(operation, null, -1);
					}
				} catch (Throwable th) {
					th.printStackTrace();
					return false;
				}
				return true;
			}
			
			public String toString() {
				return "state: " + state;
			}
		};
	}
}
