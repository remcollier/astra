package astra.ide.editor.astra;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class ASTRAWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
