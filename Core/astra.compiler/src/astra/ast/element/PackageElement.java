package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class PackageElement extends AbstractElement {
	String pkg;
	
	public PackageElement(String pkg, Token start, Token end, String source) {
		super(start, end, source);
		this.pkg = pkg;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String packageName() {
		return pkg;
	}
	
	public String toString() {
		return "package " + pkg;
	}
}
