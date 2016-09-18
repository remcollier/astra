package astra.ast.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ADTTokenizer {
	private int tabSpace = 4;
	private StringBuffer tokenBuffer = new StringBuffer();
	private StringBuffer contentBuffer = new StringBuffer();
	private List<String> lines = new ArrayList<String>();
	private List<Token> tokens = new LinkedList<Token>();
	private InputStream in;
	int line = 1;
	int column = 0;
	

	int beginLine = 1;
	int beginColumn = 0;
	int count = 0;
	private Token lastToken;
	private Stack<Character> back = new Stack<Character>();
	
	private static List<String> speechActParts = new LinkedList<String>();
	static {
		speechActParts.add("accept");
		speechActParts.add("inform");
		speechActParts.add("not");
		speechActParts.add("query");
		speechActParts.add("reject");
		speechActParts.add("request");
	}

	public static void main(String[] args) throws ParseException {
		String input = "test2(\"hello\", \"\\\"text\\\"\")";
		ADTTokenizer tokenizer = new ADTTokenizer(new ByteArrayInputStream(input.getBytes()));

		List<Token> list = new ArrayList<Token>();
		Token token = tokenizer.nextToken();
		while (token != Token.EOF_TOKEN) {
			list.add(token);
//			System.out.println("'" + token.token + "' / type: " + token.type + " (" + token.beginColumn +":" + token.beginLine + ", " + token.endColumn + ":" + token.endLine + ")");
//			System.out.println("\t'" + tokenizer.getSource(token, token) + "'");
			token = tokenizer.nextToken();
		}
		ASTRAParser parser = new ASTRAParser(tokenizer);
		System.out.println(parser.createFormula(list));
	}
	
	public ADTTokenizer(InputStream in) {
		this.in = in;
		lines.add("IGNORE");
		lines.add("");
	}

	public void setTabSpace(int tabSpace) {
		this.tabSpace = tabSpace;
	}
	
	public Token nextToken() throws ParseException {
		return lastToken = generateNextToken();
	}
	
	private Token generateNextToken() throws ParseException {
		// remove any tokens in the token buffer first before parsing more tokens
		if (!tokens.isEmpty()) {
			return tokens.remove(0);
		}
		
		// find the next token(s)
		Character ch;
		boolean quit = false;
		while (!quit && (ch = readCharacter()) != null) {
			switch (ch) {
			case ' ':
				endOfToken();
				column++;
				beginLine = line;
				beginColumn = column;
				quit = !tokens.isEmpty();
				break;
			case '\t':
				endOfToken();
				column+=tabSpace;
				beginLine = line;
				beginColumn = column;
				quit = !tokens.isEmpty();
				break;
			case '/':
				column++;
				StringBuffer buf = new StringBuffer();
				buf.append((char)ch);
				char ch2 = readCharacter();
				buf.append(ch2);
				column++;
				switch (ch2) {
				case '/':
					// Single Line Comment
					while ((ch2 = readCharacter()) != '\n') {
						buf.append(ch2);
						column++;
					}
					line++;
					column=0;
					break;
				case '*':
					// Multi Line Comment
					boolean inComment = true;
					while (inComment) {
						do {
							ch2 = readCharacter();
							buf.append(ch2);
							column++;
						} while (ch2 != '*');

						ch2 = readCharacter();
						buf.append(ch2);
						switch (ch2) {
						case '/':
							inComment = false;
							column++;
							beginLine = line;
							beginColumn = column;
						case '\r':
							break;
						case '\n':
							column=0;
							line++;
							break;
						default:
							column++;
						}
					}
					break;
				default:
					// Treat this a a divide (/)
					column -= 2;
					endOfToken();
					column++;
					addCharacterToken((char) ch);
					beginLine = line;
					beginColumn = column;
					column++;
					tokenBuffer.append(ch2);
					quit = !tokens.isEmpty();
					break;
				}
				break;
			case '\"':
				if (tokenBuffer.length() > 0) throw new ParseException("Unexpected start of string", line, column, 1);
			case '\'':
				if (tokenBuffer.length() > 0) throw new ParseException("Unexpected start of character", line, column, 1);
				tokenBuffer.append((char) ch);
				column++;
				do {
					ch2 = readCharacter();
					tokenBuffer.append(ch2);
					column++;
					
					if (ch2 == '\\') {
						char ch3 = readCharacter();
						tokenBuffer.append(ch3);
						column++;
					}
				}
				while (ch2 != ch);
				break;
			case '\n':
//				System.out.println("new line: " + line);
				column = 0;
				line++;
				beginLine = line;
				beginColumn = column;
				break;
			case '-':
				if (speechActParts.contains(tokenBuffer.toString())) {
					column++;
					tokenBuffer.append((char) ch);
					break;
				}
			case '+':
			case '%':
			case '*':
			case '.':
			case ',':
			case '!':
			case '[':
			case ']':
			case ':':
			case '=':
			case '(':
			case ')':
			case '{':
			case '}':
			case ';':
			case '~':
			case '&':
			case '|':
			case '>':
			case '<':
			case '$':
				endOfToken();
				beginLine = line;
				beginColumn = column;
				column++;
				addCharacterToken((char) ch);
				beginLine = line;
				beginColumn = column;
				quit = !tokens.isEmpty();
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				column++;
				tokenBuffer.append((char) ch);
				if (tokenBuffer.length() == 1) {
					try {
						if (in.available() > 0) {
							ch2 = readCharacter();
							while (ch2 >= '0' && ch2 <='9' && in.available() > 0) {
								tokenBuffer.append(ch2);
								column++;
								ch2 = readCharacter();
							}
							if (ch2 >= '0' && ch2 <= '9') {
								tokenBuffer.append(ch2);
								column++;
							} else  if (ch2 == '.') {
								tokenBuffer.append(ch2);
								column++;
								ch2 = readCharacter();
								while (ch2 >= '0' && ch2 <='9' && in.available() > 0) {
									tokenBuffer.append(ch2);
									column++;
									ch2 = readCharacter();
								}
								if (ch2 >= '0' && ch2 <= '9') {
									tokenBuffer.append(ch2);
									column++;
								}
							}
							
							if (ch2 == 'f' || ch2 == 'l') {
								tokenBuffer.append(ch2);
								endOfToken();
							} else {
								column -= 1;
								endOfToken();
								if (in.available() > 0) back(ch2);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				column++;
				tokenBuffer.append((char) ch);
			}
			
			try {
				quit = tokens.isEmpty() && in.available() == 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (tokenBuffer.length() > 0) {
			endOfToken();
		}
		if (tokens.isEmpty()) return Token.EOF_TOKEN;
//		throw new ParseException("Unexected end of file", line, 0, 0);
//		System.out.println("generated: " + tokens.get(0).token);
		return tokens.remove(0);
	}
	
	private void back(Character ch) {
		back.push(ch);
		count--;
	}
	
	private Character readCharacter() {
		try {
			int ch = -1;
			if (!back.isEmpty()) {
				ch = (int) back.pop().charValue();
			} else {
				ch = in.read();
			}
			count++;
			if (ch > -1) {
				contentBuffer.append((char) ch);
				lines.set(lines.size()-1,contentBuffer.toString());
				if (((char) ch) == '\n') {
					contentBuffer = new StringBuffer();
					lines.add(contentBuffer.toString());
				}
				return (char) ch;
			}
		} catch (IOException e) {
			// This should not happen
		}
		return null;
	}

	int startOfToken = 0;
	
	private void endOfToken() throws ParseException {
		String tok = tokenBuffer.toString().trim();
		if (!tok.isEmpty()) tokens.add(new Token(tok, beginLine, beginColumn, line, column, startOfToken, count));
		tokenBuffer = new StringBuffer();
		startOfToken = count;
	}

	private void addCharacterToken(char ch) throws ParseException {
		tokens.add(new Token("" + ch, beginLine, beginColumn, line, column, count-1, count));
		tokenBuffer = new StringBuffer();
	}

	public String getContents() {
		return contentBuffer.toString();
	}

	public String getSource(Token tok, Token tok2) {
//		System.out.println("'" + tok.token + "' / type: " + tok.type + " (" + tok.beginColumn + ", " + tok.endColumn + ")");
//		System.out.println("'" + tok2.token + "' / type: " + tok2.type + " (" + tok2.beginColumn + ", " + tok2.endColumn + ")");
//		System.out.println(tok.token + " -> " + tok2.token);
		String out = "";
		for (int i=tok.beginLine; i <= tok2.endLine; i++) {
			int start = 0;
			int end = lines.get(i).length();
			if (i == tok.beginLine && tok.beginColumn <= end) {
				start = tok.beginColumn;
			}
			if (i == tok2.endLine) {
				if (tok2.endColumn < end) end = tok2.endColumn;
				
			}
//			System.out.println("start: " + start + " / end: " + end);
			out += lines.get(i).substring(start, end);
		}
		return out;
	}

	public Token getLastToken() {
		return lastToken;
	}

	public void back(Token token) {
		tokens.add(0, token);
	}
	public Token peek() throws ParseException {
		Token tok = nextToken();
		back(tok);
		return tok;
	}
}
