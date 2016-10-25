

import astra.compiler.ASTRACompiler;

public class CompileFIPALibrary {

	public static void main(String[] args) {
//		ASTRACompiler.compile("astra.fipa.FIPAProtocol");
		ASTRACompiler.compile("astra.fipa.FIPARequestProtocol");
		ASTRACompiler.compile("astra.fipa.Request");

	}

}
