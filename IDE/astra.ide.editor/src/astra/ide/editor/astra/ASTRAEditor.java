package astra.ide.editor.astra;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import astra.ide.hierarchy.ASTRAContentOutlinePage;

public class ASTRAEditor extends TextEditor {
	private ColorManager colorManager;
	private ASTRAContentOutlinePage fOutlinePage;
	private ProjectionSupport projectionSupport;
	private ProjectionAnnotationModel annotationModel;
	private Annotation[] oldAnnotations;

	public ASTRAEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new ASTRAConfiguration(colorManager, this));
		setDocumentProvider(new ASTRADocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();

		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();

		// turn projection mode on
		viewer.doOperation(ProjectionViewer.TOGGLE);

		annotationModel = viewer.getProjectionAnnotationModel();
	}

	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	public void updateFoldingStructure(ArrayList<Position> positions) {
		Annotation[] annotations = new Annotation[positions.size()];

		// this will hold the new annotations along
		// with their corresponding positions
		HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();

		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();

			newAnnotations.put(annotation, positions.get(i));

			annotations[i] = annotation;
		}

		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);

		oldAnnotations = annotations;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage = new ASTRAContentOutlinePage(this);
				if (getEditorInput() != null)
					fOutlinePage.setInput(getEditorInput());
			}
			return fOutlinePage;
		}
		return super.getAdapter(required);
	}
}
