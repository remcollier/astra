import astra.core.ActionParam;
import astra.core.Module;

public class MyModule extends Module {
	@ACTION
	public boolean get(ActionParam<Long> value) {
		value.set(5l);
		return true;
	}
	
	@TERM
	public String answer() {
		return "happy";
	}
}
