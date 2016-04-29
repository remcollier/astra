package astra.ast.tr;

import astra.ast.core.AbstractElement;
import astra.ast.core.IAction;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class EISAction extends AbstractElement implements IAction {
	ITerm id;
	ITerm entity;
	PredicateFormula call;
	
	public EISAction(ITerm id, ITerm entity, PredicateFormula call, Token start, Token end, String source) {
		super(start, end, source);
		this.id = id;
		this.entity = entity;
		this.call = call;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula call() {
		return call;
	}
	
	public ITerm id() {
		return id;
	}
	
	public ITerm entity() {
		return entity;
	}
	
	public String toString() {
		return "EIS" + (id == null ? "":"( " + id + (entity == null ? "":", " + entity) + " )") + "."+call.toString();
	}
}
