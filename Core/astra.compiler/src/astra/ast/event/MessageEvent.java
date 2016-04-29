package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class MessageEvent extends AbstractElement implements IEvent {
	ITerm speechact;
	ITerm sender;
	IFormula content;
	ITerm params;
	
	public MessageEvent(ITerm speechact, ITerm sender, IFormula content, ITerm params, Token start, Token end, String source) {
		super(start, end, source);
		
		this.speechact = speechact;
		this.sender = sender;
		this.content = content;
		this.params = params;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm speechact() {
		return speechact;
	}
	
	public ITerm sender() {
		return sender;
	}
	
	public IFormula content() {
		return content;
	}
	
	public String toString() {
		return "@message(" + speechact + "," + sender + "," + content + ")";
	}
	
	public String toSignature() {
		return "message:"+speechact+":" + content.toSignature();
	}
	
	public ITerm params() {
		return params;
	}
}
