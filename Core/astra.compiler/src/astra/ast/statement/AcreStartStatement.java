package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class AcreStartStatement extends AbstractElement implements IStatement {
	ITerm protocol;
	ITerm receiver;
	ITerm performative;
	PredicateFormula content;
	ITerm cid;
	
	public AcreStartStatement(ITerm protocol, ITerm receiver, ITerm performative, PredicateFormula content, 
			ITerm cid, Token start, Token end, String source) {
		super(start, end, source);
		this.protocol = protocol;
		this.receiver = receiver;
		this.performative = performative;
		this.content = content;
		this.cid = cid;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm protocol() {
		return protocol;
	}
	
	public ITerm receiver() {
		return receiver;
	}
	
	public ITerm performative() {
		return performative;
	}
	
	public PredicateFormula content() {
		return content;
	}
	
	public ITerm cid() {
		return cid;
	}
	
	public String toString() {
		return "acre_start(" + protocol.toString() + "," + receiver.toString() + "," + performative.toString() + "," +
				content.toString() + "," + cid.toString() + ")";
	}
}
