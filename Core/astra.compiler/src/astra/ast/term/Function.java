package astra.ast.term;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.type.BasicType;

public class Function extends AbstractElement implements ITerm {
	String function;
	List<ITerm> terms;
	
	public Function(String function, List<ITerm> terms, Token start, Token end, String source) {
		super(start, end, source);
		this.function = function;
		this.terms = terms;
	}

	public int termCount() {
		return terms.size();
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public List<ITerm> terms() {
		return terms;
	}
	
	public String toString() {
		if (function.equals("true") || function.equals("false")) return function;
		String out = function + "( ";
		for (int i=0; i < terms.size(); i++) {
			if (i > 0) out += ", ";
			out += terms.get(i).toString();
		}
		return out + " )";
	}

	public String functor() {
		return function;
	}

	public String toSignature() {
		String out = function + "(";
		for (int i=0; i < terms.size(); i++) {
			if (i > 0) out += ",";
			out += Token.toTypeString(terms.get(i).type().type());
		}
		return out + ")";
	}

	@Override
	public IType type() {
		return new BasicType(Token.FUNCT);
	}
}
