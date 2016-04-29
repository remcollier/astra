package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class AssignmentStatement extends AbstractElement implements IStatement {
	ITerm term;
	String variable;
	IType type;
	
	public AssignmentStatement(String variable, ITerm term, Token start, Token end, String source) {
		super(start, end, source);
		this.variable = variable;
		this.term = term;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String variable() {
		return variable;
	}
	
	public IType type() {
		return type;
	}
	
	public ITerm term() {
		return term;
	}

	public void setType(IType type) {
		this.type = type;
	}
}
