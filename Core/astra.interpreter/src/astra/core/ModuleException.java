package astra.core;

public class ModuleException extends RuntimeException {
	public ModuleException(Exception e) {
		super(e.getMessage());
	}
	public ModuleException(String msg, Exception e) {
		super(msg, e);
	}
	public ModuleException(String msg) {
		super(msg);
	}

}
