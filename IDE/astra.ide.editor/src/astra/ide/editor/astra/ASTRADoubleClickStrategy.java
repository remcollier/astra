package astra.ide.editor.astra;

import org.eclipse.jface.text.*;

public class ASTRADoubleClickStrategy implements ITextDoubleClickStrategy {
	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;

		if (pos < 0)
			return;

		try {
			ITypedRegion reg = part.getDocument().getPartition(pos);
			part.setSelectedRange(reg.getOffset(), reg.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}