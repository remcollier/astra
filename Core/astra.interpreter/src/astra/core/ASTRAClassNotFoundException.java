package astra.core;

public class ASTRAClassNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4254904673923562155L;

	public ASTRAClassNotFoundException(String msg) {
		super(msg);
	}

	public ASTRAClassNotFoundException(String string, Throwable e) {
		super(string, e);
	}
}
