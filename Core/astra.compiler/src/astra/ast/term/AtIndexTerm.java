package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AtIndexTerm extends AbstractElement implements ITerm {
	ITerm term;
	ITerm index;
	IType type;
	
	public AtIndexTerm(ITerm term, ITerm index, IType type, Token start, Token end, String source) {
		super(start, end, source);
		this.term = term;
		this.index = index;
		this.type = type;
	}

	public ITerm term() {
		return term;
	}
	
	public ITerm index() {
		return index;
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
		return type;
	}
}
