package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ForAllStatement extends AbstractElement implements IStatement {
	ITerm variable;
	ITerm list;
	IStatement statement;
	
	public ForAllStatement(ITerm variable, ITerm list, IStatement statement, Token start, Token end, String source) {
		super(start, end, source);
		this.variable = variable;
		this.list = list;
		this.statement = statement;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public ITerm variable() {
		return variable;
	}
	
	public ITerm list() {
		return list;
	}
	
	public IStatement statement() {
		return statement;
	}
	
	public String toString() {
		return "forall ( " + variable + " : " + list + " )";
	}
}
