package astra.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import astra.core.ActionParam;
import astra.core.Module;
import astra.core.ModuleException;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.reasoner.Unifier;
import astra.reasoner.util.BindingsEvaluateVisitor;
import astra.term.Funct;
import astra.term.ListTerm;
import astra.term.Primitive;
import astra.term.Term;

/**
 * This API provides support for manipulating ASTRA Lists.
 * 
 * @author Rem Collier
 *
 */
public class Prelude extends Module {
	@SuppressWarnings({ "rawtypes" })
	private Comparator comparator = new Comparator() {
		@SuppressWarnings("unchecked")
		@Override
		public int compare(Object arg0, Object arg1) {
			if (arg0 instanceof Comparable && arg1 instanceof Comparable) {
				return ((Comparable) arg0).compareTo(arg1);
			}
			if (arg0 instanceof Primitive && arg1 instanceof Primitive) {
				return compare(((Primitive) arg0).value(), ((Primitive) arg1).value());
			}
			throw new UnsupportedOperationException();
		}
	};

	@SuppressWarnings("unchecked")
	@TERM
	public String headAsString(ListTerm list) {
		return ((Primitive<String>) list.get(0)).value();
	}

	@SuppressWarnings("unchecked")
	@TERM
	public int headAsInt(ListTerm list) {
		return ((Primitive<Integer>) list.get(0)).value();
	}

	@SuppressWarnings("unchecked")
	@TERM
	public long headAsLong(ListTerm list) {
		return ((Primitive<Long>) list.get(0)).value();
	}

	@SuppressWarnings("unchecked")
	@TERM
	public float headAsFloat(ListTerm list) {
		return ((Primitive<Float>) list.get(0)).value();
	}

	@SuppressWarnings("unchecked")
	@TERM
	public double headAsDouble(ListTerm list) {
		return ((Primitive<Double>) list.get(0)).value();
	}

	@TERM
	public ListTerm tail(ListTerm list) {
		return list.subList(1, list.size());
	}

	/**
	 * Action that adds any object to a list
	 * 
	 * @param list
	 *            the list that is to be modified
	 * @param object
	 *            the object that is to be added to the list
	 * @return
	 */
	@ACTION
	public boolean add(ListTerm list, Object object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Integer object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Long object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Float object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Double object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Character object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, String object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	@ACTION
	public boolean add(ListTerm list, Boolean object) {
		list.add(Primitive.newPrimitive(object));
		return true;
	}

	/**
	 * Action that adds any object to a list at a given index.
	 * 
	 * @param list
	 *            the list that is to be modified
	 * @param index
	 *            the index at which the object is to be added
	 * @param object
	 *            the object that is to be added to the list
	 * @return
	 */
	@ACTION
	public boolean addAt(ListTerm list, int index, Object object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Integer object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Long object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Float object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Double object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Character object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, String object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	public boolean addAt(ListTerm list, int index, Boolean object) {
		list.add(index, Primitive.newPrimitive(object));
		return true;
	}

	/**
	 * Action that removes an object from a list at a given index.
	 * 
	 * @param list
	 *            the list to be modified
	 * @param index
	 *            the index of the object to be removed
	 * @return
	 */
	@TERM
	public boolean remove(ListTerm list, int index) {
		list.remove(index);
		return true;
	}

	/**
	 * Term that returns the size of the list.
	 * 
	 * @param list
	 *            the list whose size is needed.
	 * @return
	 */
	@TERM
	public int size(ListTerm list) {
		return list.size();
	}

	/**
	 * Formula that returns true if the list is empty, false otherwise
	 * 
	 * @param list
	 *            the list whose state is to be checked
	 * @return
	 */
	@FORMULA
	public Formula isEmpty(ListTerm list) {
		return list.isEmpty() ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, String value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, int value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, long value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, float value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, double value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@FORMULA
	public Formula contains(ListTerm list, char value) {
		return list.contains(Primitive.newPrimitive(value)) ? Predicate.TRUE : Predicate.FALSE;
	}

	@TERM
	public String stringValueFor(ListTerm list, String funct) {
		return stringValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public String stringValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<String>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public int intValueFor(ListTerm list, String funct) {
		return intValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public int intValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Integer>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public long longValueFor(ListTerm list, String funct) {
		return longValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public long longValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Long>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public char charValueFor(ListTerm list, String funct) {
		return charValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public char charValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Character>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public boolean booleanValueFor(ListTerm list, String funct) {
		return booleanValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public boolean booleanValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Boolean>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public Object objectValueFor(ListTerm list, String funct) {
		return objectValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public Object objectValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Object>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public float floatValueFor(ListTerm list, String funct) {
		return floatValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public float floatValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Float>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	@TERM
	public double doubleValueFor(ListTerm list, String funct) {
		return doubleValueFor(list, funct, 0);
	}
	@SuppressWarnings("unchecked")
	@TERM
	public double doubleValueFor(ListTerm list, String functor, int index) {
		for (Term term : list.terms()) {
			if (term instanceof Funct) {
				Funct funct = (Funct) term;
				if (funct.functor().equals(functor)) {
					return ((Primitive<Double>) funct.termAt(index)).value();
				}
			}
		}
		throw new RuntimeException("No funct with functor: " + functor + " in list: " + list);
	}
	
	/**
	 * Term that returns the value at the given index cast as an int.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public int valueAsInt(ListTerm list, int index) {
		return (Integer) ((Primitive<?>) list.get(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an long.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public long valueAsLong(ListTerm list, int index) {
		return (Long) ((Primitive<?>) list.get(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an long.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public Funct valueAsFunct(ListTerm list, int index) {
		return (Funct) list.get(index);
	}

	/**
	 * Term that returns the value at the given index cast as an double.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public double valueAsDouble(ListTerm list, int index) {
		return (Double) ((Primitive<?>) list.get(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as an float.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public float valueAsFloat(ListTerm list, int index) {
		return (Float) ((Primitive<?>) list.get(index)).value();
	}

	/**
	 * Term that returns the value at the given index cast as a list.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public ListTerm valueAsList(ListTerm list, int index) {
		return (ListTerm) list.get(index);
	}

	/**
	 * Term that returns the value at the given index cast as an string.
	 * 
	 * @param list
	 *            the list containing the value
	 * @param index
	 *            the index of the value in the list
	 * @return
	 */
	@TERM
	public String valueAsString(ListTerm list, int index) {
		return ((Primitive<?>) list.get(index)).value().toString();
	}

	/**
	 * Term that sorts the list in ascending order. This method assumes that the
	 * values stored in the list are comparable.
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@ACTION
	public boolean sort_asc(ListTerm list) {
		Collections.sort(list, comparator);
		return true;
	}

	/**
	 * Term that sorts the list in descending order. This method assumes that
	 * the values stored in the list are comparable.
	 * 
	 * @param list
	 *            the list to be sorted
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@ACTION
	public boolean sort_desc(ListTerm list) {
		Collections.sort(list, comparator);
		Collections.reverse(list);
		return true;
	}

	/**
	 * Term that swaps the value at index i with the value at index j in the
	 * list.
	 * 
	 * @param list
	 *            the list to be modified
	 * @param i
	 *            the index of the first value
	 * @param j
	 *            the index of the second value
	 * @return
	 */
	@ACTION
	public boolean swap(ListTerm list, int i, int j) {
		if (i < 0 || i >= list.size())
			throw new ModuleException("First index is out of bounds: " + i);
		if (j < 0 || j >= list.size())
			throw new ModuleException("Second index is out of bounds: " + i);
		Collections.swap(list, i, j);
		return true;
	}

	/**
	 * Term that reverses the order of the values stored in the list.
	 * 
	 * @param list
	 *            the source list
	 * @return
	 */
	@ACTION
	public boolean reverse(ListTerm list) {
		Collections.reverse(list);
		return true;
	}

	/**
	 * Term that converts the ASTRA list to a standard list of Java objects.
	 * This assumes that the ASTRA list contains only primitives...
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TERM
	public List fromASTRAList(ListTerm list) {
		List nl = new ArrayList();
		for (Term value : list) {
			nl.add(((Primitive) value).value());
		}
		return nl;
	}

	/**
	 * Term that converts any Java list (anything that implements the
	 * {@link java.util.List} interface) into an ASTRA list. Here the contents
	 * of the original list are assumed to be primitives or objects and are
	 * converted to ASTRA Primitives.
	 * 
	 * @param list
	 *            the list to be converted
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@TERM
	public ListTerm toASTRAList(List list) {
		ListTerm nl = new ListTerm();
		for (Object value : list) {
			nl.add(Primitive.newPrimitive(value));
		}
		return nl;
	}

	/**
	 * Term that converts a list to an object array.
	 * 
	 * @param list
	 * @return
	 */
	@TERM
	public Object[] toObjectArray(ListTerm list) {
		return list.toArray();
	}
}
