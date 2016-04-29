package astra.event;

import astra.formula.Predicate;

public class ModuleEvent implements Event {
	public String module;
	public String signature;
	public Predicate event;
	public ModuleEventAdaptor adaptor;
	
	public ModuleEvent(String module, String signature, Predicate belief, ModuleEventAdaptor adaptor) {
		this.module = module;
		this.signature = signature;
		this.event = belief;
		this.adaptor = adaptor;
	}

	public String module() {
		return module;
	}

	public Predicate event() {
		return event;
	}
	
	public ModuleEventAdaptor adaptor() {
		return adaptor;
	}

	@Override
	public Object getSource() {
		return null;
	}
	
	public String toString() {
		return "$" + module + "." + event.toString();
	}

	@Override
	public String signature() {
		return signature;
	}
}
