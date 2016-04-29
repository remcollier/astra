package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class BasicAcreEvent extends AbstractElement implements IEvent {
	ITerm type;
	ITerm cid;
	
	public BasicAcreEvent(ITerm type, ITerm cid, Token start, Token end, String source) {
		super(start, end, source);
		
		this.type = type;
		this.cid = cid;
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
		return "@acre(" + type + "," + cid + ")";
	}

	@Override
	public String toSignature() {
		return "acre:basic";
	}
}
