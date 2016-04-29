package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class AcreAdvanceStatement extends AbstractElement implements IStatement {
	ITerm performative;
	ITerm cid;
	PredicateFormula content;
	
	public AcreAdvanceStatement(ITerm performative, ITerm cid, PredicateFormula content, 
			Token start, Token end, String source) {
		super(start, end, source);
		this.performative = performative;
		this.content = content;
		this.cid = cid;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
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
		return "acre_advance(" + performative.toString() + "," + cid.toString() + "," + content.toString() + ")";
	}
}
