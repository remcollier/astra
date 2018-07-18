package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class TailTerm extends AbstractElement implements ITerm {
	ITerm term;
	
	public TailTerm(ITerm term, Token start, Token end, String source) {
		super(start, end, source);
		this.term = term;
	}

	public ITerm term() {
		return term;
	}
	
	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String toString() {
		return term.toString();
	}

	@Override
	public IType type() {
		return new BasicType(Token.LIST);
	}
}
