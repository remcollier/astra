package astra.debugger;

import java.lang.reflect.Method;

public class StartCommand implements DebuggerCommand {
	@Override
	public String execute(DebuggerWorker worker, String[] arguments) {
		try {
			Method method = Class.forName(worker.getMainClass()).getMethod("main", String[].class);
			String[] params = new String[] {};
			method.invoke(null, (Object) params);
		} catch (Exception e) {
			e.printStackTrace();
			return FAIL;
		}
		return OK;
	}

}
