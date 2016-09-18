package astra.lang;

import java.util.Scanner;

import astra.core.ActionParam;
import astra.core.Module;
import astra.formula.Formula;
import astra.term.Funct;
import astra.term.ListTerm;
/**
 * This API provides support for I/O via the Java console. It does this via
 * java.lang.System.in and java.lang.System.out respectively.
 * 
 * @author Rem Collier
 *
 */
public class Console extends Module {
	Scanner scanner = new Scanner(java.lang.System.in);

	/**
	 * Action to print a string followed by a new line.
	 * 
	 * @param string the string to be output
	 * @return
	 */
	@ACTION
	public boolean println(String string) {
		java.lang.System.out.println("[" + agent.name() +"]" + string);
		return true;
	}

	/**
	 * Action to print a string followed by a new line.
	 * 
	 * @param string the string to be output
	 * @return
	 */
	@ACTION
	public boolean println(Funct funct) {
		java.lang.System.out.println("[" + agent.name() +"]" + funct);
		return true;
	}

	/**
	 * Action to print a formula followed by a new line.
	 * 
	 * @param string the formula to be output
	 * @return
	 */
	@ACTION
	public boolean println(Formula formula) {
		java.lang.System.out.println("[" + agent.name() +"]" + formula);
		return true;
	}

	/**
	 * Action to print a int followed by a new line.
	 * 
	 * @param integer the int to be output
	 * @return
	 */
	@ACTION
	public boolean println(Integer integer) {
		java.lang.System.out.println("[" + agent.name() +"]" + integer.toString());
		return true;
	}
	
	/**
	 * Action to print a long followed by a new line.
	 * 
	 * @param value the long to be output
	 * @return
	 */
	@ACTION
	public boolean println(Long value) {
		java.lang.System.out.println("[" + agent.name() +"]" + value.toString());
		return true;
	}
	
	/**
	 * Action to print a float followed by a new line.
	 * 
	 * @param value the float to be output
	 * @return
	 */
	@ACTION
	public boolean println(Float value) {
		java.lang.System.out.println("[" + agent.name() +"]" + value.toString());
		return true;
	}
	
	/**
	 * Action to print a double followed by a new line.
	 * 
	 * @param value the double to be output
	 * @return
	 */
	@ACTION
	public boolean println(Double value) {
		java.lang.System.out.println("[" + agent.name() +"]" + value.toString());
		return true;
	}
	
	/**
	 * Action to print a char followed by a new line.
	 * 
	 * @param value the char to be output
	 * @return
	 */
	@ACTION
	public boolean println(Character value) {
		java.lang.System.out.println("[" + agent.name() +"]" + value.toString());
		return true;
	}
	
	/**
	 * Action to print a boolean followed by a new line.
	 * 
	 * @param bool the boolean to be output
	 * @return
	 */
	@ACTION
	public boolean println(Boolean bool) {
		java.lang.System.out.println("[" + agent.name() +"]" + bool.toString());
		return true;
	}
	
	/**
	 * Action to print an ASTRA list followed by a new line.
	 * 
	 * @param list the ASTRA list to be output
	 * @return
	 */
	@ACTION
	public boolean println(ListTerm list) {
		java.lang.System.out.println("[" + agent.name() +"]" + list.toString());
		return true;
	}
	
	/**
	 * Action to print an object followed by a new line.
	 * 
	 * @param object the object to be output
	 * @return
	 */
	@ACTION
	public boolean println(Object obj) {
		return println("[" + agent.name() +"]" + obj.toString());
	}

	/**
	 * Action to print a long (no new line).
	 * 
	 * @param value the long to be output
	 * @return
	 */
	@ACTION
	public boolean print(Long value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a float (no new line).
	 * 
	 * @param value the float to be output
	 * @return
	 */
	@ACTION
	public boolean print(Float value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a double (no new line).
	 * 
	 * @param value the double to be output
	 * @return
	 */
	@ACTION
	public boolean print(Double value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a integer (no new line).
	 * 
	 * @param value the integer to be output
	 * @return
	 */
	@ACTION
	public boolean print(Integer value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a string (no new line).
	 * 
	 * @param value the string to be output
	 * @return
	 */
	@ACTION
	public boolean print(String value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a boolean (no new line).
	 * 
	 * @param value the boolean to be output
	 * @return
	 */
	@ACTION
	public boolean print(Boolean value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a char (no new line).
	 * 
	 * @param value the char to be output
	 * @return
	 */
	@ACTION
	public boolean print(Character value) {
		java.lang.System.out.print(value);
		return true;
	}

	/**
	 * Action to print a formula (no new line).
	 * 
	 * @param formula the formula to be output
	 * @return
	 */
	@ACTION
	public boolean print(Formula formula) {
		java.lang.System.out.print(formula);
		return true;
	}


	/**
	 * Action to print an object (no new line).
	 * 
	 * @param object the object to be output
	 * @return
	 */
	@ACTION
	public boolean print(Object obj) {
		return print(obj.toString());
	}

	/**
	 * Action to print the canonical class name of an object.
	 *  
	 * @param obj the object.
	 * @return
	 */
	@ACTION
	public boolean printClassName(Object obj) {
		return println(obj.getClass().getCanonicalName());
	}

	/**
	 * Action to read an integer value from the console.
	 * 
	 * @param value a container for holding the value.
	 * @return
	 */
	@ACTION
	public boolean readInt(ActionParam<Integer> value) {
		value.set(scanner.nextInt());
		return true;
	}

	/**
	 * Action to read an long value from the console.
	 * 
	 * @param value a container for holding the value.
	 * @return
	 */
	@ACTION
	public boolean readLong(ActionParam<Long> value) {
		value.set(scanner.nextLong());
		return true;
	}

	/**
	 * Action to read an float value from the console.
	 * 
	 * @param value a container for holding the value.
	 * @return
	 */
	@ACTION
	public boolean readFloat(ActionParam<Float> value) {
		value.set(scanner.nextFloat());
		return true;
	}

	/**
	 * Action to read an double value from the console.
	 * 
	 * @param value a container for holding the value.
	 * @return
	 */
	@ACTION
	public boolean readDouble(ActionParam<Double> value) {
		value.set(scanner.nextDouble());
		return true;
	}
}
