package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.IType;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class DeclarationStatement extends AbstractElement implements IStatement {
	ITerm term;
	IType type;
	String variable;
	
	public DeclarationStatement(IType type, String variable, ITerm term, Token start, Token end, String source) {
		super(start, end, source);
		this.variable = variable;
		this.term = term;
		this.type = type;
	}

	public DeclarationStatement(IType type, String variable, Token start, Token end, String source) {
		super(start, end, source);
		this.variable = variable;
		this.type = type;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String variable() {
		return variable;
	}

	public ITerm term() {
		return term;
	}
	
	public IType type() {
		return type;
	}
	
	public String toString() {
		return type + " " + variable + (term == null ? "":" = " + term);
	}
}
