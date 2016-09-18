package astra.lang;

import java.util.Random;

import astra.core.Module;
import astra.formula.Formula;
import astra.formula.Predicate;

/**
 * Mathematical support for ASTRA.
 * 
 * @author Rem Collier
 *
 */
public class Math extends Module {
	Random random = new Random();

	/**
	 * Formula that evaluates a boolean value.
	 * 
	 * @param value
	 * @return
	 */
	@FORMULA
	public Formula evaluate(boolean value) {
		return value ? Predicate.TRUE:Predicate.FALSE;
	}
	
	/**
	 * Term that returns the max of two values.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	@TERM
	public int max(int A, int B) {
		return java.lang.Math.max(A,B);
	}
	
	/**
	 * Term that returns the min of two values.
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	@TERM
	public int min(int A, int B) {
		return java.lang.Math.min(A,B);
	}
	
	/**
	 * Term that converts a string to an int.
	 * 
	 * @param X
	 * @return
	 */
	@TERM
	public int intValue(String X) {
		return Integer.parseInt(X);
	}

	/**
	 * Term that converts a string to a long.
	 * 
	 * @param X
	 * @return
	 */
	@TERM
	public long longValue(String X) {
		return Long.parseLong(X);
	}

	/**
	 * Term that converts a string to a float.
	 * 
	 * @param X
	 * @return
	 */
	@TERM
	public float floatValue(String X) {
		return Float.parseFloat(X);
	}
	
	/**
	 * Term that converts a string to a double.
	 * 
	 * @param X
	 * @return
	 */
	@TERM
	public double doubleValue(String X) {
		return Double.parseDouble(X);
	}
	
	/**
	 * Term that returns a randomly generated int value.
	 * 
	 * @param X
	 * @return
	 */
	@TERM
	public int randomInt() {
		return java.lang.Math.abs(random.nextInt());
	}
	
	/**
	 * Term that returns the absolute value of the given integer.
	 * 
	 * @param X
	 * @return
	 */

	@TERM
	public int abs(int i) {
		return java.lang.Math.abs(i);
	}
}
