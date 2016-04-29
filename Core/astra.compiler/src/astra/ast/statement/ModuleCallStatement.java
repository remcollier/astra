package astra.ast.statement;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IStatement;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.formula.PredicateFormula;

public class ModuleCallStatement extends AbstractElement implements IStatement {
	PredicateFormula method;
	String module;
	
	public ModuleCallStatement(String module, PredicateFormula method, Token start, Token end, String source) {
		super(start, end, source);
		this.module = module;
		this.method = method;
	}

	public PredicateFormula method() {
		return method;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public String module() {
		return module;
	}
	
	public String toString() {
		return module + "." + method;
	}
}
