package astra.ast.term;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class ListTerm extends AbstractElement implements ITerm {
	List<ITerm> list;
	
	public ListTerm(List<ITerm> list, Token start, Token end, String source) {
		super(start, end, source);
		this.list = list;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public IType type() {
		return new BasicType(Token.LIST);
	}

	public String toString() {
		String out = "[ ";
		boolean first = true;
		for (ITerm term : list) {
			if (first) first = false; else out += ", ";
			out += term.toString();
		}
		return out + " ]";
	}

	public List<ITerm> terms() {
		return list;
	}
}
