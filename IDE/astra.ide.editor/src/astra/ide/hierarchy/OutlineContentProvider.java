package astra.ide.hierarchy;


import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import astra.ast.core.IElement;
import astra.ast.statement.BlockStatement;

public class OutlineContentProvider implements ITreeContentProvider, IResourceChangeListener {
	public final static String ACTION = "Action"; 
	public final static String SENSOR = "Sensor"; 
	public Object[] EMPTY_ARRAY = new Object[0];
	
	private Viewer viewer;
	
	public OutlineContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
    }

    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//		root = (Root) newInput;
		this.viewer= viewer;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Root) {
			Root rt = (Root) parentElement;
			if (rt.getUnit() != null) {
				if (rt.getUnit().packageElement().packageName() == null) {
					System.out.println("package name is null is null: " + rt.getUnit());
					System.exit(0); 
				}
				if (!rt.getUnit().packageElement().packageName().equals("")) {
					return new Object[] {rt.getUnit().packageElement(), rt.getUnit()};
				}
				return new Object[] {rt.getUnit()};
			}
			return EMPTY_ARRAY;
		} else if (parentElement instanceof IElement) {
			IElement[] elements = ((IElement) parentElement).getElements();
			if (elements.length == 1 && elements[0] instanceof BlockStatement) {
				return elements[0].getElements();
			}
			return elements;
		}
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		IElement unit = (IElement) element;

		if (unit.getParent() == null)
			return null;

		return unit.getParent();
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		        viewer.refresh();
		        ((TreeViewer) viewer).expandAll();
		    }
		});	
	}
}
