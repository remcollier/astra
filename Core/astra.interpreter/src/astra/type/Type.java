package astra.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import astra.reasoner.util.StringMapper;
import astra.term.FormulaTerm;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.NullTerm;
import astra.term.Primitive;
import astra.term.Term;

public class Type implements Serializable {
	private static StringMapper mapper = new StringMapper();
	
	public static final Type STRING = new Type("string");
	public static final Type CHAR = new Type("char");
	public static final Type INTEGER = new Type("integer");
	public static final Type LONG = new Type("long");
	public static final Type FLOAT = new Type("float");
	public static final Type DOUBLE = new Type("double");
	public static final Type BOOLEAN = new Type("boolean");
	public static final Type PERFORMATIVE = new Type("performative");
	public static final Type OBJECT = new Type("object");
	public static final Type LIST = new Type("list");
	public static final Type FORMULA = new Type("formula");

	public static final Term NO_VALUE = new NullTerm();

	public static final Type FUNCTION = new Type("function");

	static List<Type> types = new ArrayList<Type>();

	static {
		types.add(BOOLEAN);
		types.add(CHAR);
		types.add(INTEGER);
		types.add(LONG);
		types.add(FLOAT);
		types.add(DOUBLE);
		types.add(STRING);
		types.add(FUNCTION);
	}

	private int id;
	
	public Type(String type) {
		id = mapper.toId(type);
	}
	
	public boolean equals(Type type) {
		return id == type.id;
	}
	
	public static Type getType(Object obj) {
		if (obj instanceof Character) return Type.CHAR;
		if (obj instanceof String) return Type.STRING;
		if (obj instanceof Integer) return Type.INTEGER;
		if (obj instanceof Long) return Type.LONG;
		if (obj instanceof Float) return Type.FLOAT;
		if (obj instanceof Double) return Type.DOUBLE;
		if (obj instanceof Boolean) return Type.BOOLEAN;
		if (obj instanceof ListTerm) return Type.LIST;
		if (obj instanceof Funct) return Type.FUNCTION;
		if (obj instanceof FormulaTerm) return Type.FORMULA;
		return new ObjectType(obj.getClass());
	}

	public static Term defaultValue(Type type) {
		if (type.equals(STRING)) return Primitive.newPrimitive("");
		if (type.equals(INTEGER)) return Primitive.newPrimitive(0);
		if (type.equals(LONG)) return Primitive.newPrimitive(0l);
		if (type.equals(FLOAT)) return Primitive.newPrimitive(0.0f);
		if (type.equals(DOUBLE)) return Primitive.newPrimitive(0.0);
		if (type.equals(CHAR)) return Primitive.newPrimitive(' ');
		if (type.equals(BOOLEAN)) return Primitive.newPrimitive(false);
		System.out.println("Type has no default: " + type.toString());
		return null;
	}
	
	public String toString() {
		return mapper.fromId(id);
	}

	public static boolean isNumeric(Type type) {
		return type.equals(INTEGER) || type.equals(LONG) || type.equals(FLOAT) || type.equals(DOUBLE);
	}

	@SuppressWarnings("unchecked")
	public static int integerValue(Term term) {
		if (term.type().equals(INTEGER)) return ((Primitive<Integer>) term).value();
		throw new UnsupportedTypeCastException("Attempted to convert: " + term + " of type: " + term.type() + " to an integer");
	}

	@SuppressWarnings("unchecked")
	public static long longValue(Term term) {
		if (term.type().equals(INTEGER)) return ((Primitive<Integer>) term).value();
		if (term.type().equals(LONG)) return ((Primitive<Long>) term).value();
		throw new UnsupportedTypeCastException("Attempted to convert: " + term + " of type: " + term.type() + " to a long");
	}

	@SuppressWarnings("unchecked")
	public static float floatValue(Term term) {
		if (term.type().equals(INTEGER)) return ((Primitive<Integer>) term).value();
		if (term.type().equals(LONG)) return ((Primitive<Long>) term).value();
		if (term.type().equals(FLOAT)) return ((Primitive<Float>) term).value();
		throw new UnsupportedTypeCastException("Attempted to convert: " + term + " of type: " + term.type() + " to a long");
	}

	@SuppressWarnings("unchecked")
	public static double doubleValue(Term term) {
		if (term.type().equals(INTEGER)) return ((Primitive<Integer>) term).value();
		if (term.type().equals(LONG)) return ((Primitive<Long>) term).value();
		if (term.type().equals(FLOAT)) return ((Primitive<Float>) term).value();
		if (term.type().equals(DOUBLE)) return ((Primitive<Double>) term).value();
		throw new UnsupportedTypeCastException("Attempted to convert: " + term + " of type: " + term.type() + " to a long");
	}

	@SuppressWarnings("unchecked")
	public static String stringValue(Term term) {
		if (term instanceof NullTerm) return "null";
		if (term.type().equals(BOOLEAN)) return ((Primitive<Boolean>) term).value().toString();
		if (term.type().equals(INTEGER)) return ((Primitive<Integer>) term).value().toString();
		if (term.type().equals(LONG)) return ((Primitive<Long>) term).value().toString();
		if (term.type().equals(FLOAT)) return ((Primitive<Float>) term).value().toString();
		if (term.type().equals(DOUBLE)) return ((Primitive<Double>) term).value().toString();
		if (term.type().equals(CHAR)) return ((Primitive<Character>) term).value().toString();
		if (term.type().equals(STRING)) return ((Primitive<String>) term).value();
		if (term.type() instanceof ObjectType) return ((Primitive<?>) term).value().toString();
		if (term.type().equals(FORMULA)) return ((FormulaTerm) term).value().toString();
		if (term.type().equals(FUNCTION)) return ((Funct) term).toString();
		if (term.type().equals(LIST)) return ((astra.term.ListTerm) term).toString();
		throw new UnsupportedTypeCastException("Attempted to convert: " + term + " of type: " + term.type() + " to a long");
	}
	
	public static Type getMostGeneralType(Type type, Type type2) {
		return (types.indexOf(type) < types.indexOf(type2)) ? type2 : type;
	}

	public static boolean isNumeric(Term il) {
		return isNumeric(il.type());
	}
}
