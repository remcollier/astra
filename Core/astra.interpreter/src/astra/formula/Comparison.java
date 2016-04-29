package astra.formula;

import astra.term.Term;
import astra.util.LogicVisitor;


public class Comparison implements Formula {
	public static final String LESS_THAN						= "<";
	public static final String GREATER_THAN						= ">";
	public static final String LESS_THAN_OR_EQUAL				= "<=";
	public static final String GREATER_THAN_OR_EQUAL			= ">=";
	public static final String EQUAL							= "==";
	public static final String NOT_EQUAL						= "~=";
	public static final String OTHER_NOT_EQUAL					= "!=";

	private String operator;
	private Term left;
	private Term right;
	
	public Comparison(String operator, Term left, Term right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	
	public String operator() {
		return operator;
	}
	
	public Term left() {
		return left;
	}
	
	public Term right(){ 
		return right;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public String toString() {
		return left + " " + operator + " " + right;
	}

	@Override
	public boolean matches(Formula formula) {
		return (formula instanceof Comparison) & operator.equals(((Comparison) formula).operator) && 
				left.matches(((Comparison) formula).left) && right.matches(((Comparison) formula).right);  
	}
}
