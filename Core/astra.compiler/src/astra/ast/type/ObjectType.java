package astra.ast.type;

import astra.ast.core.IElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IType;
import astra.ast.core.ParseException;

public class ObjectType implements IType {
	int type;
	String clazz;
	IElement parent;
	
	public ObjectType(int type, String clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	@Override
	public int type() {
		return type;
	}
	
	public String getClazz() {
		return clazz;
	}

	@Override
	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean equals(Object object) {
		if (object instanceof ObjectType) {
			return clazz.equals(((ObjectType) object).clazz);
		}
		return false;
	}

	@Override
	public IElement[] getElements() {
		return new IElement[0];
	}

	@Override
	public IElement getParent() {
		return parent;
	}

	@Override
	public IElement setParent(IElement parent) {
		this.parent =parent;
		return this;
	}
	
	public int getBeginLine() {
		return parent.getBeginLine();
	}

	@Override
	public int getBeginColumn() {
		return parent.getBeginColumn();
	}

	@Override
	public int charStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int charEnd() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		return "object<" + clazz + ">";
	}
}
