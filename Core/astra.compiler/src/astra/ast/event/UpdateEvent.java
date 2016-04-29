package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class UpdateEvent extends AbstractElement implements IEvent {
	String type;
	IFormula content;
	
	public UpdateEvent(String type, IFormula content, Token start, Token end, String source) {
		super(start, end, source);
		this.type = type;
		this.content = content;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IFormula content() {
		return content;
	}

	public String type() {
		return type;
	}
	
	public String toString() {
		return type + content;
	}
	
	public String toSignature() {
		return "update:"+type+":" + content.toSignature();
	}
}
