package astra.ide.editor.astra;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class ASTRAScanner extends BufferedRuleBasedScanner {
	public ASTRAScanner(ColorManager manager) {
		setRules(new IRule[] {
			new WhitespaceRule(new ASTRAWhitespaceDetector())
		});
	}
}
