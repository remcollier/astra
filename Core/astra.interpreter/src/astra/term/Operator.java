package astra.term;

import astra.type.Type;
import astra.util.LogicVisitor;

public class Operator implements Term {
	public static final char PLUS							= '+'; 
	public static final char MINUS 							= '-'; 
	public static final char MULTIPLY						= '*'; 
	public static final char DIVIDE							= '/'; 
	public static final char MODULO							= '%';
	
	private char op;
	private Term left, right;
	private Type type;
	
	protected Operator(char op, Type type, Term left, Term right) {
		this.op = op;
		this.type = type;
		this.left = left;
		this.right =right;
	}
	
	public static Operator newOperator(char op, Term left, Term right) {
		if (left.type().equals(right.type())) {
			return new Operator(op, left.type(), left, right);
		}
		
		return new Operator(op, Type.getMostGeneralType(left.type(),right.type()), left, right);
	}
	
	public char op() {
		return op;
	}
	
	public Term left() {
		return left;
	}
	
	public Term right() {
		return right;
	}
	
	@Override
	public Type type() {
		return type;
	}

	@Override
	public Object accept(LogicVisitor visitor) {
		return visitor.visit(this);
	}

	public String toString() {
		return toString("");
	}
	
	public String toString(String indent) {
		return left.toString() + op + right.toString();
	}

	@Override
	public boolean matches(Term term) {
		return type.equals(term.type());
	}
	
	public boolean equals(Object object) {
		if (object instanceof Operator) {
			Operator op = (Operator) object;
			return op.left.equals(left) && op.right.equals(right);
		}
		return false;
	}

	@Override
	public String signature() {
		// TODO Auto-generated method stub
		return null;
	}
}
