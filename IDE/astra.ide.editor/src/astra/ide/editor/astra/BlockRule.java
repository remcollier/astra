package astra.ide.editor.astra;

import org.eclipse.jface.text.rules.*;

public class BlockRule extends MultiLineRule {

	public BlockRule(IToken token) {
		super("{", "}", token);
	}
}
