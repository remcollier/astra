package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AdvancedAcreEvent extends AbstractElement implements IEvent {
	ITerm type;
	ITerm cid;
	ITerm state;
	ITerm length;
	
	public AdvancedAcreEvent(ITerm type, ITerm cid, ITerm state, ITerm length, Token start, Token end, String source) {
		super(start, end, source);
		
		this.type = type;
		this.cid = cid;
		this.state = state;
		this.length = length;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm type() {
		return type;
	}
	
	public ITerm cid() {
		return cid;
	}
	
	public String toString() {
		return "@acre(" + type + "," + cid + "," + state + "," + length + ")";
	}

	public ITerm state() {
		return state;
	}

	public ITerm length() {
		return length;
	}

	@Override
	public String toSignature() {
		return "acre:advanced";
	}
}
