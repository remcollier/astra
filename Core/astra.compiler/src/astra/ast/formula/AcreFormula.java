package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AcreFormula extends AbstractElement implements IFormula {
	private ITerm cid;
	private ITerm index;
	private ITerm type;
	private ITerm performative;
	private IFormula content;
	
	public AcreFormula(ITerm cid, ITerm index, ITerm type, ITerm performative, IFormula content, Token start, Token end, String source) {
		super(start, end, source);
		this.cid = cid;
		this.index = index;
		this.type = type;
		this.performative = performative;
		this.content = content;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm cid() {
		return cid;
	}

	public ITerm index() {
		return index;
	}

	public ITerm type() {
		return type;
	}

	public ITerm performative() {
		return performative;
	}

	public IFormula content() {
		return content;
	}
	
	public String toString() {
		return "acre_message( " + cid + ", " + index + ", " + type + ", " + performative + ", " + content + " )";
	}
	
	public String toSignature() {
		return "";
	}
}
