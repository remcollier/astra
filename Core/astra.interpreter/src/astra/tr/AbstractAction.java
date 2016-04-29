package astra.tr;

public abstract class AbstractAction implements Action {
	int beginLine;
	int beginColumn;
	int endLine;
	int endColumn;
	String clazz;
	boolean linkedToSource = false;
	
	public void setLocation(String clazz, int beginLine, int beginColumn, int endLine, int endColumn) {
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.clazz = clazz;
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
