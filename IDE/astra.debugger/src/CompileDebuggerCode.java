import astra.compiler.ASTRACompiler;

public class CompileDebuggerCode {

	public static void main(String[] args) {
//		ASTRACompiler.compile("astra.gui.Test2");
		ASTRACompiler compiler = ASTRACompiler.newInstance();
		compiler.run_compiler("astra.debugger.Debugger");
		compiler.run_compiler("astra.debugger.Test");
	}

}
