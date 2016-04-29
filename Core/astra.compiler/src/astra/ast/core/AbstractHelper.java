package astra.ast.core;

import java.util.HashMap;
import java.util.Map;

import astra.ast.formula.MethodSignature;
import astra.ast.type.BasicType;
import astra.ast.type.ObjectType;

public abstract class AbstractHelper implements IJavaHelper {
	static Map<String, Integer> typeMappings = new HashMap<String, Integer>();
	
	static {
		typeMappings.put("boolean", Token.BOOLEAN);
		typeMappings.put("char", Token.CHARACTER);
		typeMappings.put("int", Token.INTEGER);
		typeMappings.put("long", Token.LONG);
		typeMappings.put("float", Token.FLOAT);
		typeMappings.put("double", Token.DOUBLE);
		typeMappings.put("string", Token.STRING);
		typeMappings.put("list", Token.LIST);
		typeMappings.put("formula", Token.FORMULA);
		typeMappings.put("funct", Token.FUNCT);
	}
	
	@Override
	public IType getType(String moduleClass, MethodSignature signature) {
		Integer t = typeMappings.get(signature.returnType());
		return (t == null) ? new ObjectType(Token.OBJECT, signature.returnType()) : new BasicType(t);
	}
}
