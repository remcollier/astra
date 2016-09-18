package astra.statement;

public abstract class DefaultModuleCallAdaptor implements ModuleCallAdaptor {
	@Override
	public boolean suppressNotification() {
		return false;
	}

}
