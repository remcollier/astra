package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ITerm;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class CartagoStatement extends AbstractElement implements IStatement {
	PredicateFormula call;
	ITerm artifact;
	
	public CartagoStatement(ITerm artifact, PredicateFormula call, Token start, Token end, String source) {
		super(start, end, source);
		this.artifact = artifact;
		this.call = call;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula call() {
		return call;
	}
	
	public ITerm artifact() {
		return artifact;
	}
	
	public String toString() {
		String out = "CARTAGO";
		if (artifact != null) {
			out += "(" + artifact + ")";
		}
		return out + "." + call.toString();
	}
}
