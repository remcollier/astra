package astra.ast.formula;



public class MethodSignature {
	private String name;
	private MethodType[] types;
	private String returnType;
	private int type;
	private String signature;
	boolean symbol;
	
	public MethodSignature(PredicateFormula formula, int type) {
		this.type = type;
		name = formula.predicate();
		types = new MethodType[formula.termCount()];
		for (int i=0; i < formula.termCount(); i++) {
			types[i] = new MethodType(formula.terms().get(i));
		}
	}

	public MethodSignature(PredicateFormula formula, int type, boolean symbol) {
		this(formula, type);
		this.symbol = symbol;
	}
	
	public String name() {
		return name;
	}
	
	public int type() {
		return type;
	}
	
	public MethodType[] types() {
		return types;
	}
	
	public String toString() {
		String out = name + "(";
		boolean first = true;
		for (MethodType type : types) {
			if (first) first=false; else out += ",";
			out += type;
		}
		return out + ") : " + (returnType == null ? "undefined" : returnType);
	}

	public int termCount() {
		return types.length;
	}

	public MethodType type(int i) {
		return types[i];
	}

	public void returnType(String retType) {
		this.returnType = retType;
	}

	public String returnType() {
		return returnType;
	}

	public void signature(String signature) {
		this.signature = signature;
	}

	public String signature() {
		return signature;
	}
	
	public boolean symbol() {
		return symbol;
	}
}
