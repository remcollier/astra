package astra.eis;

import astra.core.AbstractTask;
import astra.formula.Predicate;

public abstract class EISTask extends AbstractTask {
	protected Predicate op;
	
	public EISTask(Predicate op) {
		super("[EIS] " + op);
		this.op = op;
	}
}
