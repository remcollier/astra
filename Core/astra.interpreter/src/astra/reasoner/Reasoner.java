package astra.reasoner;

import java.util.List;
import java.util.Map;

import astra.formula.Formula;
import astra.term.Term;

/**
 * Core Interface for logical reasoning systems that may be attached to an agent. Sources
 * contain logic databases that are queried by the reasoner. All sources must implement the
 * {@link Queryable} interface.
 * 
 * @author rem
 *
 */
public interface Reasoner {
	public void addSource(Queryable source);
	public List<Map<Integer, Term>> queryAll(Formula formula);
	public List<Map<Integer, Term>> query(Formula formula);
	public List<Map<Integer, Term>> query(Formula formula, Map<Integer, Term> bindings);
	public Reasoner copy();
}
