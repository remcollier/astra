package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.visitor.Utilities;

public class Brackets extends AbstractElement implements ITerm {
	IType type;
	ITerm contents;
	
	public Brackets(ITerm contents, Token start, Token end, String source) {
		super(start, end, source);
		
		this.contents = contents;
		this.type = contents.type();
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm contents() {
		return contents;
	}

	@Override
	public IType type() {
		return type;
	}
	
	public String toString() {
		return "(" + contents.toString() + ")";
	}

	public void updateType() {
		type = contents.type();
	}
}
