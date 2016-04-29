package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class CartagoEvent extends AbstractElement implements IEvent {
	ITerm type;
	ITerm evt;
	IFormula content;
	
	public CartagoEvent(ITerm speechact, ITerm sender, IFormula content, Token start, Token end, String source) {
		super(start, end, source);
		
		this.type = speechact;
		this.evt = sender;
		this.content = content;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm type() {
		return type;
	}
	
	public ITerm evt() {
		return evt;
	}
	
	public IFormula content() {
		return content;
	}
	
	public String toString() {
		return "@cartago(" + type + (evt != null ? ","+evt:"") + "," + content + ")";
	}

	@Override
	public String toSignature() {
		return "cartago:" + content.toSignature();
	}
}
