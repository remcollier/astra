package astra.statement;


public abstract class AbstractStatement implements Statement {
	int beginLine;
	int beginColumn;
	int endLine;
	int endColumn;
	String clazz;
	boolean linkedToSource = false;
	
	public void setLocation(String clazz, int beginLine, int beginColumn, int endLine, int endColumn) {
		this.clazz = clazz;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		linkedToSource = true;
	}
	
	public int beginLine() {
		return beginLine;
	}
	
	public String getASTRAClass() {
		return clazz;
	}
	
	public boolean isLinkedToSource() {
		return linkedToSource;
	}
}
