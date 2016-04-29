package astra.ast.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableStack {
	Stack<Map<String, String>> tables = new Stack<Map<String, String>>();
	
	public VariableStack() {
		addScope();
	}
	
	public void addVariable(String variable, String type) {
		tables.peek().put(variable, type);
	}
	
	public boolean exists(String variable) {
//		System.out.println("looking for: " + variable);
		int i = tables.size()-1;
		while (i >= 0) {
			Map<String, String> table = tables.get(i--);
			if (table.containsKey(variable)) {
//				System.out.println("found: " + variable);
				return true;
			}
		}
//		System.out.println("not found: " + variable);
		return false;
	}
	
	public void addScope() {
		tables.push(new HashMap<String, String>());
	}

	public void removeScope() {
		tables.pop();
	}
}
