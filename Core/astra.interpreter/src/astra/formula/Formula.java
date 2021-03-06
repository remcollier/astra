package astra.formula;

import java.io.Serializable;

import astra.reasoner.util.LogicVisitor;

public interface Formula extends Serializable {

	public Object accept(LogicVisitor visitor);

	public boolean matches(Formula formula);

}
