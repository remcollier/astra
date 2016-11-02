package astra.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BreakpointManager {
	private static Map<Breakpoint, List<Breakpoint>> breakpoints = new HashMap<Breakpoint, List<Breakpoint>>();
	
	public static void setBreakpoint(Breakpoint breakpoint) {
		List<Breakpoint> list = breakpoints.get(breakpoint);
		if (list == null) {
			breakpoints.put(breakpoint, list=new LinkedList<Breakpoint>());
		}
		list.add(breakpoint);
		Collections.sort(list);
	}
}
