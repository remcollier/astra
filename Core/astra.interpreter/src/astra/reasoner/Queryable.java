package astra.reasoner;

import java.util.List;

import astra.formula.Formula;

/**
 * A Source for {@link Reasoner} implementations.  This interface defines a single method that
 * should return formulae matching the given formula.
 * 
 * @author rem
 */
public interface Queryable {
	public List<Formula> getMatchingFormulae(Formula predicate);
}
