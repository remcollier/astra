package astra.term;

import astra.formula.Formula;
import astra.type.Type;
import astra.util.LogicVisitor;

public class QueryTerm implements Term {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3531850681574434943L;
	Formula formula;
	
	public QueryTerm(Formula formula) {
		this.formula = formula;
	}

	public Boolean value() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  QueryTerm.value  <<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return null;
	}

	@Override
	public Type type() {
		return Type.BOOLEAN;
	}
	
	public Formula formula() {
		return formula;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean matches(Term term) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> QueryTerm.matches <<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return (term instanceof Variable);
	}

	@Override
	public String signature() {
		return "qt";
	}
	
	public String toString() {
		return "query(" + formula.toString() + ")";
	}

}
