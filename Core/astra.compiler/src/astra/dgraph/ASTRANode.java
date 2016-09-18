package astra.dgraph;

import java.util.List;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;

public class ASTRANode {
	ASTRAClassElement element;
	String name;
	boolean loaded;
	long lastModified;
	boolean compiled;
	
	public ASTRANode(String name, IJavaHelper helper) throws ParseException {
		this.name = name;
		compiled = false;
		try {
			this.element = helper.loadAST(name);
			if (element == null) {
				loaded=false;
			} else {
				loaded = element.getErrorList().isEmpty();
				lastModified = helper.lastModified(name);
			}
		} catch (ParseException e) {
			loaded = false;
			throw e;
		}
	}

	public ASTRAClassElement element() {
		return element;
	}
	
	public String toString() {
		return name + "{ " + loaded + "," + compiled + " }";
	}
	
	public long lastModified() {
		return lastModified;
	}
	
	public boolean loaded() {
		return loaded;
	}
	
	public boolean reload(IJavaHelper helper, boolean parentFailure) throws ParseException {
		System.out.println("reloading: " + name);
		try {
			this.element = helper.loadAST(name);
			loaded = element.getErrorList().isEmpty() && parentFailure;
			lastModified = helper.lastModified(name);
		} catch (ParseException e) {
			loaded = false;
			throw e;
		}
		return loaded;
	}

	public void loaded(boolean loaded) {
		this.loaded = loaded;
	}

	public List<ParseException> getErrorList() {
		return element.getErrorList();
	}
}
