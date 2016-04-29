package astra.ast.term;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class ListSplitterTerm extends AbstractElement implements ITerm {
	ITerm head;
	ITerm tail;
	
	public ListSplitterTerm(ITerm head, ITerm tail, Token start, Token end, String source) {
		super(start, end, source);
		this.head = head;
		this.tail = tail;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm head() {
		return head;
	}

	public ITerm tail() {
		return tail;
	}

	@Override
	public IType type() {
		return tail.type();
	}
	
	@Override
	public String toString() {
		return "[" + head + "|" + tail + "]";
	}
}
