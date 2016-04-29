package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ModuleElement extends AbstractElement {
	String clazz;
	String name;
	String qualifiedName;
	
	public ModuleElement(String clazz, String name, Token start, Token end, String source) {
		super(start, end, source);
		this.clazz = clazz;
		this.name = name;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String name() {
		return name;
	}

	public String className() {
		return clazz;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public String qualifiedName() {
		return qualifiedName;
	}
	
	public String toString() {
		return "module " + clazz + " " + name;
	}
}
