package astra.ast.formula;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class PredicateFormula extends AbstractElement implements IFormula {
	String predicate;
	List<ITerm> terms;
	
	public PredicateFormula(String predicate, List<ITerm> terms, Token start, Token end, String source) {
		super(start, end, source);
		this.predicate = predicate;
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
		if (predicate.equals("true") || predicate.equals("false")) return predicate;
		String out = predicate + "(";
		if (terms.size() > 0) out += " ";
		for (int i=0; i < terms.size(); i++) {
			if (i > 0) out += ", ";
			out += terms.get(i).toString();
		}
		if (terms.size() > 0) out += " ";
		return out + ")";
	}

	public String predicate() {
		return predicate;
	}
	
	public String toSignature() {
		StringBuffer buf = new StringBuffer();
		buf.append("formula:" + predicate);
		for (int i=0; i < terms.size(); i++) {
			buf.append(":" + Token.toTypeString(terms.get(i).type().type()));
		}
		return buf.toString();
	}

}
