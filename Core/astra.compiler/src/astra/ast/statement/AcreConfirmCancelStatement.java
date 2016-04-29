package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AcreConfirmCancelStatement extends AbstractElement implements IStatement {
	ITerm cid;
	
	public AcreConfirmCancelStatement(ITerm cid, Token start, Token end, String source) {
		super(start, end, source);
		this.cid = cid;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm cid() {
		return cid;
	}
	
	public String toString() {
		return "acre_confirm_cancel(" + cid.toString() + ")";
	}
}
