package astra.reasoner.util;

import astra.formula.Formula;
import astra.term.Term;

public interface LogicVisitor {
	public Object visit(Formula formula);
	public Object visit(Term term);
}
