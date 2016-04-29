package astra.ast.core;

public abstract class AbstractElement implements IElement {
	public Token start;
	public Token end;
	String source;
	public IElement parent;

	public AbstractElement(Token start, Token end, String source) {
		this.start = start;
		this.end = end;
		this.source = source;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public IElement getParent() {
		return parent;
	}

	@Override
	public IElement setParent(IElement parent) {
		this.parent = parent;
		return this;
	}

	@Override
	public IElement[] getElements() {
		return new IElement[0];
	}

	@Override
	public int getBeginLine() {
		if (start == null) return 0;
		return start.beginLine;
	}

	@Override
	public int getBeginColumn() {
		if (start == null) return 0;
		return start.beginColumn;
	}
	
	public int charStart() {
		if (start == null) return 0;
		return start.charStart;
	}
	
	public int charEnd() {
		if (end == null) return 0;
		return end.charEnd;
	}

	public int getEndLine() {
		if (end == null) return 0;
		return end.endLine;
	}
	public int getEndColumn() {
		if (end == null) return 0;
		return end.endColumn;
	}
}
