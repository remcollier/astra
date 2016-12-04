package astra.ast.statement;

import java.util.List;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class MaintainBlockStatement extends AbstractElement implements IStatement {
	List<IStatement> statements;
	List<IStatement> recover;
	IFormula formula;
	
	public MaintainBlockStatement(IFormula formula, List<IStatement> statements, List<IStatement> recover, Token start, Token end, String source) {
		super(start, end, source);
		this.formula = formula;
		this.statements = statements;
		this.recover = recover;
	}

	public IStatement[] statements() {
		return statements.toArray(new IStatement[] {});
	}
	
	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String toString() {
		String out = "maintain (" + formula + ") { ";
		for (IStatement statement : statements) {
			out += statement + "; ";
		}
		if (!recover.isEmpty()) {
			out += "}  recover { ";
			for (IStatement statement : recover) {
				out += statement + "; ";
			}
		}
		return out + "}";
	}

	public IFormula formula() {
		return formula;
	}
}
