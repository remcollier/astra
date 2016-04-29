package astra.ast.tr;

import astra.ast.core.AbstractElement;
import astra.ast.core.IAction;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class TRAction extends AbstractElement implements IAction {
	String type;
	PredicateFormula call;
	
	public TRAction(String type, PredicateFormula call, Token start, Token end, String source) {
		super(start, end, source);
		this.type = type;
		this.call = call;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula call() {
		return call;
	}
	
	public String type() {
		return type;
	}
	
	public String toString() {
		return call.toString();
	}
}
