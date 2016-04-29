package astra.ast.statement;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class SendStatement extends AbstractElement implements IStatement {
	ITerm performative, sender, params;
	IFormula content;
	
	public SendStatement(ITerm performative, ITerm sender, IFormula content, ITerm params, Token start, Token end, String source) {
		super(start, end, source);
		this.performative = performative;
		this.sender = sender;
		this.content = content;
		this.params = params;
	}

	public IFormula content() {
		return content;
	}

	public ITerm performative() {
		return performative;
	}

	public ITerm sender() {
		return sender;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String toString() {
		return "send(" + performative + ","  + sender + "," + content + ")";
	}

	public ITerm params() {
		return params;
	}
}
