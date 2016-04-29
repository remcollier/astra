package astra.ide.hierarchy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import astra.ast.core.IElement;
import astra.ast.jdt.ASTRAProject;
import astra.ide.editor.astra.ASTRAEditor;

public class ASTRAContentOutlinePage extends ContentOutlinePage {
	Object root;
	ASTRAEditor editor;
	IEditorInput editorInput;
	
	public ASTRAContentOutlinePage(ASTRAEditor editor) {
		this.editor = editor;
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().setContentProvider(new OutlineContentProvider());
		getTreeViewer().setLabelProvider(new OutlineLabelProvider());
		getTreeViewer().addSelectionChangedListener(this);
		getTreeViewer().setInput(root);
		getTreeViewer().expandAll();
		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			   public void selectionChanged(SelectionChangedEvent event) {
			       // if the selection is empty clear the label
			       if(event.getSelection().isEmpty()) {
			           return;
			       }
			       if(event.getSelection() instanceof IStructuredSelection) {
			           IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			           if (selection.getFirstElement() instanceof IElement) {
			        	   IElement unit = (IElement) selection.getFirstElement();
				           IDocument document = editor.getDocumentProvider().getDocument(editorInput);
				           try {
							   IRegion region = document.getLineInformation(unit.getBeginLine()-1);
					           editor.setHighlightRange(region.getOffset(), region.getLength(), true);
					           editor.setFocus();
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			           }
			       }
			   }
			});
		}

	public void setInput(IEditorInput editorInput) {
		this.editorInput = editorInput;
		IFile file = ((IFileEditorInput) editorInput).getFile();
		if (file.exists()) {
			root = new Root(file);
		}
	}
}
