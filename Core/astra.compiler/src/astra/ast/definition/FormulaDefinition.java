package astra.ast.definition;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ILanguageDefinition;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class FormulaDefinition extends AbstractElement implements ILanguageDefinition {
	private String name;
	private TypeDefinition[] types;
	
	public FormulaDefinition(String name, TypeDefinition[] types, Token start, Token end, String source) {
		super(start, end, source);

		this.name = name;
		this.types = types;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(name);
		buf.append("(");
		for (int i=0; i < types.length; i++) {
			if (i > 0) buf.append(",");
			buf.append(types[i].token);
		}
		buf.append(")");
		return buf.toString();
	}

	@Override
	public String toSignature() {
		StringBuffer buf = new StringBuffer();
		buf.append("formula:"+name);
		for (TypeDefinition type : types) {
			buf.append(":"+type.token);
		}
		return buf.toString();
	}
}
