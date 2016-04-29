package astra.lang;

import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;
import astra.term.ListTerm;
import astra.term.Primitive;
/**
 * This API provides support for manipulating strings in ASTRA.
 * 
 * @author Rem Collier
 *
 */
public class Strings extends Module {
	/**
	 * Formula that checks if the two strings are equals.
	 * 
	 * @param source one string
	 * @param target the other string
	 * @return
	 */
	@FORMULA
	public Formula equal(String source, String target) {
		return source.equals(target) ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Formula that checks if the text starts with the pattern.
	 * 
	 * @param text the text
	 * @param pattern the pattern
	 * @return
	 */
	@FORMULA
	public Formula startsWith(String text, String pattern) {
		return text.startsWith(pattern) ? Predicate.TRUE:Predicate.FALSE;
	}

	/**
	 * Formula that checks if the text ends with the pattern.
	 * 
	 * @param text the text
	 * @param pattern the pattern
	 * @return
	 */
	@FORMULA
	public Formula endsWith(String text, String pattern) {
		return text.endsWith(pattern) ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Term that returns the character at the given index in 
	 * the source string.
	 * 
	 * @param source the string
	 * @param index the index of the character in the string
	 * @return
	 */
	@TERM
	public char charAt(String source, int index) {
		return source.charAt(index);
	}
	
	/**
	 * Term that splits the string into an ASTRA list of tokens 
	 * separated by the given delimiter.
	 * 
	 * @param source the source string
	 * @param delimiter the delimiter
	 * @return
	 */
	@TERM
	public ListTerm split(String source, String delimiter) {
		ListTerm list = new ListTerm();
		for (String token : source.split(delimiter)) {
			list.add(Primitive.newPrimitive(token));
		}
		return list;
	}
}
