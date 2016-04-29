package astra.ast.core;

public class ClassDeclarationElement extends AbstractElement {
	String name;
	String[] parents;
	boolean _abstract;
	boolean _final;
	
	public ClassDeclarationElement(String name, String[] parents, boolean _abstract, boolean _final, Token start, Token end, String source) {
		super(start, end, source);
		this.name = name;
		this.parents = parents;
		this._abstract = _abstract;
		this._final = _final;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String name() {
		return name;
	}

	public String[] parents() {
		return parents;
	}
	
	public boolean isAbstract() {
		return _abstract;
	}

	public boolean isFinal() {
		return _final;
	}
}
