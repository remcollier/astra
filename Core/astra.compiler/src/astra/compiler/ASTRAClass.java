package astra.compiler;

import java.util.ArrayList;
import java.util.List;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;

public class ASTRAClass {
	ASTRAClassElement element;
	List<ParseException> errorList = new ArrayList<ParseException>();
	
	String name;
	
	/**
	 * Creates instances of ASTRAClass. Any error that arises loading the AST is
	 * forwarded on to the method that requested the class be instantiated.
	 * 
	 * @param cls
	 * @param helper
	 * @return
	 */
	public ASTRAClass(String className) {
		this.name = className;
	}

	public boolean load(IJavaHelper helper) {
		errorList.clear();
		
		try {
			element = helper.loadAST(name);
			errorList.addAll(element.getErrorList());
		} catch (ParseException e) {
			errorList.add(e);
		}
		return errorList.isEmpty();
	}
	
	public String name() {
		return name;
	}
	
	public ASTRAClassElement element() {
		return element;
	}
	
	public List<ParseException> errorList() {
		return errorList;
	}

	public boolean isLoaded() {
		return errorList.isEmpty();
	}
	
	public boolean sourceChanged(IJavaHelper helper) {
		return (helper.lastModified(name,".astra") > helper.lastModified(name, ".class"));
	}
	
	public String toString() {
		return "ASTRAClass."+name + " [" + errorList.isEmpty() + "]";
	}
}
