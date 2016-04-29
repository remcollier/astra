package astra.ast.core;


public class ParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3638552174772398541L;
	int line;
	int column;
	int length;
	IElement element;
	Token start;
	Token end;
	
	public ParseException(String msg, int beginLine, int beginColumn, int length) {
		super(msg + " on line: " + beginLine + " column: " + beginColumn);
		this.line = beginLine;
		this.column = beginColumn;
		this.length = length;
	}
	
	public ParseException(String msg, Throwable e, int beginLine, int beginColumn, int length) {
		super(msg + " on line: " + beginLine + " column: " + beginColumn, e);
		this.line = beginLine;
		this.column = beginColumn;
		this.length = length;
	}

	public ParseException(String msg, Throwable e, IElement element) {
		super(msg + " on line: " + element.getBeginLine() + " column: " + element.getBeginColumn(), e);
		this.element = element;
	}

	public ParseException(String msg, IElement element) {
		super(msg + " on line: " + element.getBeginLine() + " column: " + element.getBeginColumn());
		this.element = element;
	}

	public ParseException(String msg, Token start, Token end) {
		super(msg + " on line: " + start.beginLine + " column: " + start.beginColumn);
		this.start = start;
		this.end = end;
	}

	public ParseException(String msg, Token tok) {
		this(msg, tok, tok);
	}

	public int line() {
		if (element != null) {
			return element.getBeginLine();
		}
		if (end != null) {
			return end.beginLine;
		}
		return line;
	}
	
	public int column() {
		if (element != null) {
			return element.getBeginColumn();
		}
		if (end != null) {
			return end.beginColumn;
		}
		return column;
	}

	public int charStart() {
		if (element != null) {
			return element.charStart();
		}
		if (start != null) {
			return start.charStart;
		}
		return 0;
	}
	
	public int charEnd() {
		if (element != null) {
			return element.charEnd()-1;
		}
		if (end != null) {
			return end.charEnd;
		}
		return 0;
	}
	
	public int length(int tabSize) {
		if (element != null) {
			int count = 0;
			String source = element.getSource();
			for (int i = 0; i < source.length(); i++) {
				if (source.charAt(i) == '\t') {
					count += tabSize;
				} else {
					count++;
				}
			}
			return count;
		}
		if (end != null) {
			return end.token.length();
		}
		return length;
	}
}
