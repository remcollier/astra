package astra.ide.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
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
public class MultiCommentCommand extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public MultiCommentCommand() {
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

		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		IProject project = input.getFile().getProject();

		ISelection sel = editor.getSelectionProvider().getSelection();
		if (sel instanceof TextSelection) {
			final TextSelection textSel = (TextSelection) sel;
			String newText = "/*" + textSel.getText() + "*/";
			try {
				doc.replace(textSel.getOffset(), textSel.getLength(), newText);
			} catch (BadLocationException e) {
				throw new ExecutionException("Error Commenting Text", e);
			}
		}

		page.saveEditor(editor, false);
		return null;
	}

}
