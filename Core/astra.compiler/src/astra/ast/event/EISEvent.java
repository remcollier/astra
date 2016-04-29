package astra.ast.event;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class EISEvent extends AbstractElement implements IEvent {
	public static final int ENVIRONMENT 	= 0;
	public static final int ADDITION 		= 1;
	public static final int RETRACTION	 	= 2;

	int type;
	ITerm id;
	ITerm entity;
	IFormula content;
	
	public EISEvent(int type, ITerm id, ITerm entity, IFormula content, Token start, Token end, String source) {
		super(start, end, source);
		
		this.type = type;
		this.id = id;
		this.entity = entity;
		this.content = content;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm id() {
		return id;
	}

	public int type() {
		return type;
	}
	
	public ITerm entity() {
		return entity;
	}
	
	public IFormula content() {
		return content;
	}
	
	public String toString() {
		String t = "+";
		if (type == ENVIRONMENT) {
			t = "";
		} else if (type == RETRACTION) {
			t = "-";
		}
		return t + "@eis( " + id + ", " + (entity == null ? "":entity + ", ") + content + " )";
	}

	@Override
	public String toSignature() {
		return "eis:" + content.toSignature();
	}
}
