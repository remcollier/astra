package astra.ast.core;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ParserTests {
	public static void main(String[] args) throws ParseException {
//		ASTRAParser parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("3 >= 5 & likes(5) & is(system.name(), [\"rem\", 2], \"happy\")".getBytes())));
//		List<Token> list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("stream: " + list);
//		System.out.println("predicate: " + parser.createFormula(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("EIS(\"rem\")->is(\"alive\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("predicate: " + parser.createFormula(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("CARTAGO(\"rem\")->is(\"alive\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("predicate: " + parser.createFormula(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("acre_message(string A, string Y, int Z, string P, content(\"q\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("predicate: " + parser.createFormula(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("console.print(\"rem\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("predicate: " + parser.createFormula(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("TEst::!print(\"rem\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("predicate: " + parser.createFormula(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("+@eis(string Y, string X, print(\"rem\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("@eis(string Y, print(\"rem\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("@cartago(string Y, print(\"rem\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("@cartago(string Y, string Y, print(\"rem\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("@message(inform, string X, print(\"rem\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("+print(\"rem\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("event: " + parser.createEvent(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("int X".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("int X = 50".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("{ int X = 50; int Y; }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("send(inform, \"main\", state(\"alive\"))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("synchronized (tok) { int X = 50; int Y; }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("query( isa(string X, string Y) & isa(Y, string Z) )".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("if ( isa(string X, string Y) & isa(Y, string Z) ) {} else { int X; }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("while ( isa(string X, string Y) & isa(Y, string Z) ) { if (has(Z)) { int X; } }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("while ( isa(string X, string Y) & isa(Y, string Z) ) { if (has(Z)) { int X; } int P; }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("try { console.println(\"help\"); } recover { console.println(\"phew\"); }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("test.Scope::myplan(X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("!goal(X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("!!goal(X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//		
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("test.Scope::!goal(X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("foreach(isa(string X, string Y)) { console.println(\"X=\"+X); }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("forall(string X : L) { console.println(\"X=\"+X); }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("EIS(\"rem\").println(\"X=\"+X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("EIS.println(\"X=\"+X)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("acre_start(N, Y, P, state(5), int Y)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("acre_advance(N, Y, state(5))".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("CARTAGO.focus(\"test\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream("CARTAGO(\"my\").focus(\"test\")".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("statement: " + parser.createStatement(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream(
//				"+!init() { console.println(\"hello world\"); }".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("rule: " + parser.createRule(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream(
//				"isa(string X, string Z) :- isa(X, string Y) & isa(Y, Z)".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("inference: " + parser.createInference(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream(
//				"astra.lang.Console console".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("module: " + parser.createModule(list));
//
//		parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream(
//				"test.pack".getBytes())));
//		list = parser.readTo(Token.SEMI_COLON);
//		System.out.println("module: " + parser.createPackage(list));

		ASTRAParser parser = new ASTRAParser(new ADTTokenizer(new ByteArrayInputStream(
				"int X = math.intValue(system.name()) + 1".getBytes())));
		System.out.println("module: " + parser.createTerm(parser.readTo(Token.SEMI_COLON)));
	}

}
