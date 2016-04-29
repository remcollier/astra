package astra.lang;

import astra.core.Module;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;

public class Functions extends Module {
	@TERM
	public String functor(Funct f) {
		return f.functor();
	}
	
	@TERM
	public int arity(Funct f) {
		return f.size();
	}

	/**
	 * Term that returns the value at the given index cast as an int.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public int valueAsInt(Funct f, int index) {
		return (Integer) ((Primitive<?>) f.getTerm(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an long.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public long valueAsLong(Funct f, int index) {
		return (Long) ((Primitive<?>) f.getTerm(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an long.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public Funct valueAsFunct(Funct f, int index) {
		return (Funct) f.getTerm(index);
	}

	/**
	 * Term that returns the value at the given index cast as an double.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public double valueAsDouble(Funct f, int index) {
		return (Double) ((Primitive<?>) f.getTerm(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an float.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public float valueAsFloat(Funct f, int index) {
		return (Float) ((Primitive<?>) f.getTerm(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as a list.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public ListTerm valueAsList(Funct f, int index) {
		return (ListTerm) f.getTerm(index);
	}

	/**
	 * Term that returns the value at the given index cast as an string.
	 * 
	 * @param list the list containing the value
	 * @param index the index of the value in the list
	 * @return
	 */
	@TERM
	public String valueAsString(Funct f, int index) {
		return ((Primitive<?>) f.getTerm(index)).value().toString();
	}
}
