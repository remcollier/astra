package astra.ast.formula;

import astra.ast.core.AbstractElement;
import astra.ast.core.IElementVisitor;
import astra.ast.core.IFormula;
import astra.ast.core.ParseException;
import astra.ast.core.Token;

public class ModuleFormula extends AbstractElement implements IFormula {
	PredicateFormula formula;
	String module;
	
	public ModuleFormula(String module, PredicateFormula formula, Token start, Token end, String source) {
		super(start, end, source);
		this.module = module;
		this.formula = formula;
	}

	@Override
	public Object accept(IElementVisitor visitor, Object data) throws ParseException {
		return visitor.visit(this, data);
	}

	public PredicateFormula method() {
		return formula;
	}
	
	public String module() {
		return module;
	}
	
	public String toString() {
		return module + "." + formula;
	}
	
	public String toSignature() {
		return "";
	}
}
