package astra.ast.formula;

import java.util.HashMap;
import java.util.Map;

import astra.ast.core.ITerm;
import astra.ast.core.Token;
import astra.ast.term.InlineVariableDeclaration;
import astra.ast.term.Variable;
import astra.ast.type.ObjectType;

public class MethodType {
	private static Map<String, String> types = new HashMap<String, String>();
	
	static {
		types.put("java.lang.Character", "char");
		types.put("java.lang.String", "string");
		types.put("java.lang.Integer", "int");
		types.put("int", "int");
		types.put("java.lang.Long", "long");
		types.put("long", "long");
		types.put("java.lang.Float", "float");
		types.put("float", "float");
		types.put("java.lang.Double", "double");
		types.put("double", "double");
		types.put("java.lang.Boolean", "boolean");
		types.put("boolean", "boolean");
		types.put("astra.term.ListTerm", "list");
		types.put("ListTerm", "list");
		types.put("astra.term.Funct", "funct");
		types.put("Funct", "funct");
	}
	
	public static String resolveType(String type) {
		String t = types.get(type);
		return t==null ? type:t;
	}
	
	private String type;
	private boolean variable;
	private boolean actionParam = false;
	private String primitiveType;
	
	public MethodType(ITerm term) {
		variable = term instanceof Variable || term instanceof InlineVariableDeclaration;
		String t = Token.toTypeString(term.type().type());
		if (t == null) t = ((ObjectType) term.type()).getClazz();
		type = t;		
	}
	
	public String toString() {
		return type + (variable ? " [var]":"[val]");
	}

	public boolean variable() {
		return variable;
	}
	
	public String type() {
		return type;
	}
	
	public void type(String type) {
		this.type = type;
	}
	
	public boolean isPrimitive() {
		return types.values().contains(type);
	}
	
	public boolean validatePrimitive(String cls) {
		String ans = types.get(cls);
		if (ans == null) {
			return false;
		}
		boolean result = ans.equals(type);
		if (result) {
			primitiveType = cls;
		}
		return result;
	}

	public void primitiveType(String primitiveType) {
		this.primitiveType = primitiveType;
	}
	
	public String primitiveType() {
		return primitiveType;
	}
	
	public void actionParam(boolean actionParam) {
		this.actionParam = actionParam;
	}
	
	public boolean isActionParam() {
		return actionParam;
	}

	public String toClassString() {
		return (actionParam ? "ActionParam<":"") + primitiveType + (actionParam ? ">":"");
	}
}
