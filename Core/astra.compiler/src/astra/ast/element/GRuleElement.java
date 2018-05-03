package astra.ast.element;

import java.util.List;

import astra.ast.core.IElementVisitor;
import astra.ast.core.IEvent;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;


public class GRuleElement extends RuleElement {
	IFormula dropCondition;
	List<RuleElement> rules;
	
	public GRuleElement(IEvent event, IFormula context, IFormula dropCondition, IStatement statement, List<RuleElement> rules, Token start, Token end, String source) {
		super(event, context, statement, start, end, source);
		this.dropCondition = dropCondition;
		this.rules = rules;
	}

	public IEvent event() {
		return event;
	}

	public IFormula context() {
		return context;
	}

	public IFormula dropCondition() {
		return dropCondition;
	}

	public IStatement statement() {
		return statement;
	}
	
	public List<RuleElement> rules() {
		return rules;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}
	
	public String toString() {
		return "rule " + event + " : " + context + " " + statement;
	}
}