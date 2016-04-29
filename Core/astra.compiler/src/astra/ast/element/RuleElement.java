package astra.ast.element;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;


public class RuleElement extends AbstractElement {
	IEvent event;
	IFormula context;
	IStatement statement;
	
	public RuleElement(IEvent event, IFormula context, IStatement statement, Token start, Token end, String source) {
		super(start, end, source);
		this.event = event;
		this.context = context;
		this.statement = statement;
	}

	public IEvent event() {
		return event;
	}

	public IFormula context() {
		return context;
	}

	public IStatement statement() {
		return statement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}
	
	public String toString() {
		return "rule " + event + " : " + context + " " + statement;
	}
}