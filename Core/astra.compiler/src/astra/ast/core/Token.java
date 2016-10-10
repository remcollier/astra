package astra.ast.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Token {
	public static final int	NONE = 0;
	public static final int PACKAGE = 1;
	public static final int IMPORT = 2;
	public static final int AGENT = 3;
	public static final int EXTENDS = 4;
	public static final int	EOF = 5;
	public static final int	ABSTRACT = 6;
	public static final int	FINAL = 7;
	
	// PRIMARY ELEMENTS
	public static final int MODULE = 10;
	public static final int INITIAL = 11;
	public static final int INFERENCE = 12;
	public static final int RULE = 13;
	public static final int PLAN = 14;
	public static final int FUNCTION = 15;
	public static final int CONSTANT = 16;
	public static final int TYPES = 17;

	// BASIC LITERALS
	public static final int BOOLEAN = 19;
	public static final int IDENTIFIER = 20;
	public static final int STRING = 21;
	public static final int CHARACTER = 22;
	public static final int INTEGER = 23;
	public static final int LONG = 24;
	public static final int FLOAT = 25;
	public static final int DOUBLE = 26;
	public static final int LIST = 27;
	public static final int FORMULA = 28;
	public static final int OBJECT = 29;
	public static final int SPEECHACT = 30;
	public static final int FUNCT = 58;
	
	// PUNCTUATION
	public static final int PERIOD = 31;
	public static final int SEMI_COLON = 32;
	public static final int LEFT_BRACE = 33;
	public static final int RIGHT_BRACE = 34;
	public static final int LEFT_BRACKET = 35;
	public static final int RIGHT_BRACKET = 36;
	public static final int BANG = 37;
	public static final int PLUS = 38;
	public static final int MINUS = 39;
	public static final int COLON = 40;
	public static final int ASSIGNMENT = 41;
	public static final int MULTIPLY = 42;
	public static final int DIVIDE = 43;
	public static final int COMMA = 44;
	public static final int TRUE = 45;
	public static final int FALSE = 46;
	public static final int NOT = 47;
	public static final int AND = 48;
	public static final int OR = 49;
	public static final int LESS_THAN = 50;
	public static final int GREATER_THAN = 51;
	public static final int MESSAGE = 52;
	public static final int LEFT_SQ_BRACKET = 55;
	public static final int RIGHT_SQ_BRACKET = 56;
	public static final int MODULO = 57;
	public static final int DOLLAR = 58;
	
	// TYPES
	public static final int STRING_TYPE = 60;
	public static final int CHARACTER_TYPE = 61;
	public static final int INTEGER_TYPE = 62;
	public static final int LONG_TYPE = 63;
	public static final int FLOAT_TYPE = 64;
	public static final int DOUBLE_TYPE = 65;
	public static final int SPEECHACT_TYPE = 66;
	public static final int LIST_TYPE = 67;
	public static final int BOOLEAN_TYPE = 68;
	public static final int OBJECT_TYPE = 69;
	public static final int FUNCT_TYPE = 86;
	
	// STATEMENTS
	public static final int SEND = 70;
	public static final int IF = 71;
	public static final int ELSE = 72;
	public static final int QUERY = 73;
	public static final int WHILE = 74;
	public static final int FOREACH = 75;
	public static final int WHEN = 76;
	public static final int WAIT = 77;
	public static final int TRY = 78;
	public static final int RECOVER = 79;
	public static final int TR_START = 81;
	public static final int TR_STOP = 82;
	public static final int SYNCHRONIZED = 84;
	public static final int FORALL = 85;
	public static final int BIND = 86;
	
	public String token;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
	public int type;
	public int charStart;
	public int charEnd;
	
	static Map<Integer, String> typeToString = new HashMap<Integer, String>();
	static Map<String, Integer> typeMap = new HashMap<String, Integer>();
	static List<Integer> typePrecedence = new LinkedList<Integer>();

	static {
		// Map Tokens to types here
		typeMap.put("package", PACKAGE);
		typeMap.put("import", IMPORT);
		typeMap.put("agent", AGENT);
		typeMap.put("extends", EXTENDS);
		typeMap.put("abstract", ABSTRACT);
		typeMap.put("final", FINAL);
		
		// Program Elements
		typeMap.put("module", MODULE);
		typeMap.put("initial", INITIAL);
		typeMap.put("rule", RULE);
		typeMap.put("plan", PLAN);
		typeMap.put("function", FUNCTION);
		typeMap.put("inference", INFERENCE);
		typeMap.put("constant", CONSTANT);
		typeMap.put("types", TYPES);
		
		// Types
		typeMap.put("int", INTEGER_TYPE);
		typeMap.put("long", LONG_TYPE);
		typeMap.put("float", FLOAT_TYPE);
		typeMap.put("double", DOUBLE_TYPE);
		typeMap.put("char", CHARACTER_TYPE);
		typeMap.put("string", STRING_TYPE);
		typeMap.put("boolean", BOOLEAN_TYPE);
		typeMap.put("speechact", SPEECHACT_TYPE);
		typeMap.put("object", OBJECT_TYPE);
		typeMap.put("list", LIST_TYPE);
		typeMap.put("formula", FORMULA);
		typeMap.put("funct", FUNCT_TYPE);
		
		// Statements
		typeMap.put("send", SEND);
		typeMap.put("if", IF);
		typeMap.put("else", ELSE);
		typeMap.put("query", QUERY);
		typeMap.put("while", WHILE);
		typeMap.put("foreach", FOREACH);
		typeMap.put("forall", FORALL);
		typeMap.put("when", WHEN);
		typeMap.put("wait", WAIT);
		typeMap.put("try", TRY);
		typeMap.put("recover", RECOVER);
		typeMap.put("start", TR_START);
		typeMap.put("stop", TR_STOP);
		typeMap.put("synchronized", SYNCHRONIZED);
		typeMap.put("bind", BIND);
		
		// Basic Punctuation
		typeMap.put(".", PERIOD);
		typeMap.put("=", ASSIGNMENT);
		typeMap.put(":", COLON);
		typeMap.put("!", BANG);
		typeMap.put("-", MINUS);
		typeMap.put("+", PLUS);
		typeMap.put("*", MULTIPLY);
		typeMap.put("/", DIVIDE);
		typeMap.put(",", COMMA);
		typeMap.put(";", SEMI_COLON);
		typeMap.put("{", LEFT_BRACE);
		typeMap.put("}", RIGHT_BRACE);
		typeMap.put("(", LEFT_BRACKET);
		typeMap.put(")", RIGHT_BRACKET);
		typeMap.put("true", BOOLEAN);
		typeMap.put("false", BOOLEAN);
		typeMap.put("~", NOT);
		typeMap.put("&", AND);
		typeMap.put("|", OR);
		typeMap.put(">", GREATER_THAN);
		typeMap.put("<", LESS_THAN);
		typeMap.put("@message", MESSAGE);
		typeMap.put("[", LEFT_SQ_BRACKET);
		typeMap.put("]", RIGHT_SQ_BRACKET);
		typeMap.put("%", MODULO);
		typeMap.put("$", DOLLAR);
		
		// Speech Acts
		typeMap.put("accept-proposal", SPEECHACT);
		typeMap.put("agree", SPEECHACT);
		typeMap.put("cancel", SPEECHACT);
		typeMap.put("cfp", SPEECHACT);
		typeMap.put("confirm", SPEECHACT);
		typeMap.put("disconfirm", SPEECHACT);
		typeMap.put("failure", SPEECHACT);
		typeMap.put("inform", SPEECHACT);
		typeMap.put("inform-if", SPEECHACT);
		typeMap.put("inform-ref", SPEECHACT);
		typeMap.put("not-understood", SPEECHACT);
		typeMap.put("propose", SPEECHACT);
		typeMap.put("query-if", SPEECHACT);
		typeMap.put("query-ref", SPEECHACT);
		typeMap.put("refuse", SPEECHACT);
		typeMap.put("reject-proposal", SPEECHACT);
		typeMap.put("request", SPEECHACT);
		typeMap.put("request-when", SPEECHACT);
		typeMap.put("request_whenever", SPEECHACT);
		typeMap.put("subscribe", SPEECHACT);

		typePrecedence.add(Token.CHARACTER);
		typePrecedence.add(Token.INTEGER);
		typePrecedence.add(Token.LONG);
		typePrecedence.add(Token.FLOAT);
		typePrecedence.add(Token.DOUBLE);
		typePrecedence.add(Token.LIST);
		typePrecedence.add(Token.SPEECHACT);
		typePrecedence.add(Token.OBJECT);
		typePrecedence.add(Token.STRING);
		
		typeToString.put(Token.INTEGER, "int");
		typeToString.put(Token.LONG, "long");
		typeToString.put(Token.FLOAT, "float");
		typeToString.put(Token.DOUBLE, "double");
		typeToString.put(Token.STRING, "string");
		typeToString.put(Token.CHARACTER, "char");
		typeToString.put(Token.FORMULA, "formula");
		typeToString.put(Token.BOOLEAN, "boolean");
		typeToString.put(Token.LIST, "list");
		typeToString.put(Token.FUNCT, "funct");
		typeToString.put(Token.SPEECHACT, "speechact");
		typeToString.put(Token.INTEGER_TYPE, "int");
		typeToString.put(Token.LONG_TYPE, "long");
		typeToString.put(Token.FLOAT_TYPE, "float");
		typeToString.put(Token.DOUBLE_TYPE, "double");
		typeToString.put(Token.STRING_TYPE, "string");
		typeToString.put(Token.CHARACTER_TYPE, "char");
		typeToString.put(Token.BOOLEAN_TYPE, "boolean");
		typeToString.put(Token.LIST_TYPE, "list");
		typeToString.put(Token.FUNCT_TYPE, "funct");
		typeToString.put(Token.SPEECHACT_TYPE, "speechact");
	}
	
	protected Token() {
		type = EOF;
	}
	
	public Token(String tok, int beginLine, int beginColumn, int endLine, int endColumn, int startChar, int endChar) throws ParseException {
		this.token = tok;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.charStart = startChar;
		this.charEnd = endChar;
		
		Integer t = typeMap.get(tok);
		if (t == null) {
			if (tok.startsWith("\"")) {
				if (!tok.endsWith("\"")) {
					throw new ParseException("String literal not terminated correctly: " + tok, this);
				}
				type = STRING;
			} else if (tok.startsWith("'")) {
				if (!tok.endsWith("\'")) {
					throw new ParseException("String literal not terminated correctly: " + tok, this);
				}
				if (tok.length() > 3) {
					throw new ParseException("Expected a single character in single quotes \', but got: " + tok, this);
				}
				type = CHARACTER;
			} else {
				try {
					Integer.parseInt(tok);
					type = INTEGER;
				} catch (NumberFormatException e1) {
				}
				
				if (type == NONE) {
					try {
						if (tok.endsWith("l")) {
							Long.parseLong(tok.substring(0,tok.length()-1));
							type = LONG;
						}
					} catch (NumberFormatException e2) {
					}
				}

				if (type == NONE) {
					try {
						if (tok.endsWith("f")) {
							Float.parseFloat(tok.substring(0,tok.length()-1));
							type = FLOAT;
						}
					} catch (NumberFormatException e3) {
					}
				}
				
				if (type == NONE) {
					try {
						Double.parseDouble(tok);
						type = DOUBLE;
					} catch (NumberFormatException e4) {
					}
				}
				
				if (type == NONE) {
					type = IDENTIFIER;
				}
			}
		} else {
			type = t;
		}
	}

	public String toString() {
		return token;
	}

	public static boolean isType(int type) {
		return (type == Token.INTEGER_TYPE) || (type == Token.LONG_TYPE) || (type == Token.FLOAT_TYPE) || 
				(type == Token.DOUBLE_TYPE) || (type == Token.STRING_TYPE) || (type == Token.CHARACTER_TYPE) ||
				(type == Token.BOOLEAN_TYPE) || (type == Token.SPEECHACT_TYPE) || (type == Token.OBJECT_TYPE) ||
				(type == Token.LIST_TYPE) || (type == Token.FUNCT_TYPE);
	}

	public static boolean isLiteral(int type) {
		return (type == Token.INTEGER) || (type == Token.LONG) || (type == Token.FLOAT) || 
				(type == Token.DOUBLE) || (type == Token.STRING) || (type == Token.CHARACTER) ||
				(type == Token.BOOLEAN) || (type == Token.SPEECHACT) || (type == Token.FUNCT);
	}

	public static int resolveType(int type) {
//		System.out.println("resolving: " + type);
		if (type == Token.INTEGER_TYPE) return Token.INTEGER;
		if (type == Token.LONG_TYPE) return Token.LONG;
		if (type == Token.FLOAT_TYPE) return Token.FLOAT;
		if (type == Token.DOUBLE_TYPE) return Token.DOUBLE;
		if (type == Token.STRING_TYPE) return Token.STRING;
		if (type == Token.CHARACTER_TYPE) return Token.CHARACTER;
		if (type == Token.BOOLEAN_TYPE) return Token.BOOLEAN;
		if (type == Token.SPEECHACT_TYPE) return Token.SPEECHACT;
		if (type == Token.OBJECT_TYPE) return Token.OBJECT;
		if (type == Token.LIST_TYPE) return Token.LIST;
		if (type == Token.FUNCT_TYPE) return Token.FUNCT;
//		System.out.println("not handled");
		return -1;
	}

	public static IType resolveTypes(IType iType, IType iType2) {
		return (typePrecedence.indexOf(iType.type()) > typePrecedence.indexOf(iType2.type())) ? iType : iType2;
	}

	public static String toTypeString(int type) {
		return typeToString.get(type);
	}

	public static int fromTypeString(String type) {
		return resolveType(typeMap.get(type));
	}
	
	public static final Token EOF_TOKEN = new Token();

	public static boolean isNumeric(int type) {
		return (type == Token.INTEGER) || (type == Token.LONG) || (type == Token.FLOAT) || 
				(type == Token.DOUBLE);
	}
}
