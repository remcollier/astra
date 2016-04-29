package astra.ast.type;

import astra.ast.core.IElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class BasicType implements IType {
	int type;
	private IElement parent;
	
	public BasicType(int type) {
		this.type = type;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	@Override
	public int type() {
		return type;
	}

	@Override
	public String getSource() {
		return null;
	}
	
	public boolean equals(Object object) {
		if (object instanceof BasicType) {
			return type == ((BasicType) object).type;
		}
		return false;
	}
	
	public String toString() {
		return Token.toTypeString(type);
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
}
