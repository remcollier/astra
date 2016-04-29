package astra.ast.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import astra.ast.core.IType;

public class VariableTypeStack {
	Stack<Map<String, IType>> tables = new Stack<Map<String, IType>>();
	
	public VariableTypeStack() {
		addScope();
	}
	
	public void addVariable(String variable, IType type) {
		tables.peek().put(variable, type);
	}
	
	public boolean exists(String variable) {
		return getType(variable) != null;
	}
	
	public IType getType(String variable) {
		int i = tables.size()-1;
		while (i >= 0) {
			Map<String, IType> table = tables.get(i--);
			IType type = table.get(variable);
			if (type != null) return type;
		}
		return null;
	}
	
	public void addScope() {
		tables.push(new HashMap<String, IType>());
	}

	public void removeScope() {
		tables.pop();
	}
	
	public String toString() {
		String out = "";
		int i = tables.size()-1;
		while (i >= 0) {
			Map<String, IType> table = tables.get(i--);
			out += (i+1) + "." + table + "\n";
		}
		return out;
	}

	public void dump() {
		System.out.println(toString());
	}
}
