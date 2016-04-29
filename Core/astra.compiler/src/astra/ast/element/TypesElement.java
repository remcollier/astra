package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.ILanguageDefinition;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class TypesElement extends AbstractElement {
	String name;
	ILanguageDefinition[] definitions;
	IStatement statement;
	
	public TypesElement(String name, ILanguageDefinition[] definitions, Token start, Token end, String source) {
		super(start, end, source);
		this.name = name;
		this.definitions = definitions;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String name() {
		return name;
	}
	
	public ILanguageDefinition[] definitions() {
		return definitions;
	}
}
