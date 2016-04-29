package astra.ast.core;

public interface IElement {
	String getSource();
	public Object accept(IElementVisitor visitor, Object data) throws ParseException;
	public IElement[] getElements();
	public IElement getParent();
	public IElement setParent(IElement parent);
	public int getBeginLine();
	public int getBeginColumn();
	public int charStart();
	public int charEnd();
}
