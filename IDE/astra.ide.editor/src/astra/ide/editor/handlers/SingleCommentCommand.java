package astra.ide.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
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
public class SingleCommentCommand extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SingleCommentCommand() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		ITextEditor editor = (ITextEditor) page.getActiveEditor();
		if (editor == null)
			return null;

		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		if (doc == null)
			return null;

		ISelection sel = editor.getSelectionProvider().getSelection();
		if (sel instanceof TextSelection) {
			final TextSelection textSel = (TextSelection) sel;

			// Assume we are going to remove comments from the selected lines
			boolean add_comment = false;

			// Get start and end of the selected region
			int startLine = textSel.getStartLine();
			int endLine = textSel.getEndLine();

			IRegion[] regions = new IRegion[1+endLine - startLine];
			String[] lines = new String[1+endLine - startLine];

			try {
				System.out.println("TEXT:\n" + doc.get(textSel.getOffset(), textSel.getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

			// Extract the lines to be (un)commented and check that all the lines are
			// commented (if not, add_comment is set to true and the lines will be commented
			// out instead of uncommented).
			int i = startLine;
			while (i <= endLine) {
				try {
					regions[i-startLine] = doc.getLineInformationOfOffset(doc.getLineOffset(i));
					lines[i-startLine] = doc.get(regions[i-startLine].getOffset(), regions[i-startLine].getLength());
					System.out.println("LIne: " + lines[i-startLine]);
					if (lines[i-startLine].startsWith("//")) add_comment = true;
				} catch (BadLocationException e) {
					throw new ExecutionException("Error Commenting Text", e);
				}

				i++;
			}

			// Now perform the update
			int offset = -1;
			int length = -1;
			try {
				offset = doc.getLineOffset(startLine);
				length = doc.getLineOffset(endLine+1) - offset;
			} catch (BadLocationException e) {
				throw new ExecutionException("Error Commenting Text", e);
			}
			String newText = "";
			for (int j=0 ; j < lines.length; j++) {
				try {
					newText += (add_comment ? lines[j].substring(2):"//" + lines[j])+doc.getLineDelimiter(j+startLine);
				} catch (BadLocationException e) {
					throw new ExecutionException("Error Commenting Text", e);
				}
			}

			System.out.println("new text: " + newText);
			
			try {
				doc.replace(offset, length, newText);
				editor.getSelectionProvider().setSelection(new TextSelection(offset, newText.length()));
			} catch (BadLocationException e) {
				throw new ExecutionException("Error Commenting Text", e);
			}
		}

		page.saveEditor(editor, false);
		return null;
	}

}
