package astra.ast.core;

public class ImportElement extends AbstractElement {
	String name;
	
	public ImportElement(String name, Token start, Token end, String source) {
		super(start, end, source);
		this.name = name;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data)
			throws ParseException {
		return visitor.visit(this, data);
	}

	public String name() {
		return name;
	}

}
