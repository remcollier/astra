package astra.term;

import java.io.Serializable;

import astra.type.Type;
import astra.util.LogicVisitor;

public interface Term extends Serializable {

	public static final Term[] EMPTY_ARRAY = {};
	public static final Term NULL_TERM = null;
	
	public Type type();

	public Object accept(LogicVisitor visitor);

	public boolean matches(Term right);

	public String signature();


}
