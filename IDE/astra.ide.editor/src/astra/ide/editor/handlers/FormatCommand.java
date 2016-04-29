package astra.ide.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class FormatCommand extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public FormatCommand() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) return null;

		IDocumentProvider dp = ((ITextEditor)editor).getDocumentProvider();
		IDocument doc = dp.getDocument(((ITextEditor)editor).getEditorInput());
		if (doc == null) return null;
		
		StringBuffer buf = new StringBuffer();
		
		int brackets = 0;
		try {
			for (int i=0; i < doc.getNumberOfLines(); i++) {
				int offset = doc.getLineOffset(i);
				int length = doc.getLineLength(i);
				String line = doc.get(offset, length).trim();
				brackets -= count('}', line);
				buf.append(tabs(brackets)).append(line);
				if (doc.getLineDelimiter(i) != null) buf.append(doc.getLineDelimiter(i));
				brackets += count('{', line);
			}
			doc.set(buf.toString());
		} catch (BadLocationException e) {
			new ExecutionException("Error Reading Lines", e);
		}

		page.saveEditor(editor, false);
		return null;
	}
	
	private int count(char ch, String line) {
		int count = 0;
		for (int i=0; i < line.length(); i++) {
			if (line.charAt(i) == ch) count++;
		}
		return count;
	}

	private String tabs(int tabs) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i < tabs; i++) {
			buf.append('\t');
		}
		return buf.toString();
	}
}
