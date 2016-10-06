package astra.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import astra.ast.reflection.ReflectionHelper;

public class ASTRACompiler {
	IJavaHelper helper;
	ASTRAClassHierarchy hierarchy;
	
	public static void compile(String source) {
		ASTRACompiler.newInstance().run_compiler(source);
	}
	
	public static ASTRACompiler newInstance() {
		return newInstance("src/");
	}
	
	public static ASTRACompiler newInstance(String target) {
		return new ASTRACompiler(target);
	}
	
	private ASTRACompiler(String target) {
		helper = new ReflectionHelper(target);
		hierarchy = new ASTRAClassHierarchy(helper);
	}
	
	public void run_compiler(String cls) {
		Map<String, List<ParseException>> errors = new HashMap<String, List<ParseException>>();
		hierarchy.compile(cls, errors);
		if (!errors.isEmpty()) {
			for(Entry<String, List<ParseException>> entry : errors.entrySet()) {
				System.out.println("File: " + entry.getKey());
				for(ParseException e : entry.getValue()) {
					e.printStackTrace();
				}
			}
		}
	}
}
