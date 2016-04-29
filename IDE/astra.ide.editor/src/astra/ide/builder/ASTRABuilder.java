package astra.ide.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.xml.sax.helpers.DefaultHandler;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ParseException;
import astra.ast.jdt.ASTRAProject;

public class ASTRABuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = "astra.ide.builder";
	private static final String MARKER_TYPE = "astra.ide.problem";

	class ASTRADeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkASTRA(resource);
				break;
			case IResourceDelta.REMOVED:
				deleteGeneratedCode(resource);
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkASTRA(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class ASTRAResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkASTRA(resource);
			return true;
		}
	}

	class ASTRAErrorHandler extends DefaultHandler {
		private IResource resource;

		public ASTRAErrorHandler(IResource resource) {
			this.resource = resource;
		}

		public void error(ParseException ex) {
			try {
				IMarker marker = resource.createMarker(MARKER_TYPE);
				marker.setAttribute(IMarker.MESSAGE, ex.getMessage());
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				if (ex.line() == -1) {
					marker.setAttribute(IMarker.LINE_NUMBER, 1);
				} else {
					marker.setAttribute(IMarker.LINE_NUMBER, ex.line());
				}
				if (ex.charStart() > 0) {
					marker.setAttribute(IMarker.CHAR_START, ex.charStart());
					marker.setAttribute(IMarker.CHAR_END, ex.charEnd());
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	protected void deleteGeneratedCode(IResource resource) throws CoreException {
		if (resource instanceof IFolder) {
			for (IResource res : ((IFolder) resource).members()) {
				deleteGeneratedCode(res);
			}
			return;
		}
		
		IFile file = (IFile) resource;
		
		try {
			ASTRAProject project = ASTRAProject.getProject(file.getProject());
			project.deleteFile(file);
		} catch (CoreException e2) {
			e2.printStackTrace();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}


		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException e1) {
		}
		
		if (!file.getProjectRelativePath().toString().startsWith("src")) {
			return;
		}
		
		String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
		IResource parent = file.getParent();
		while (!parent.getName().equals("src")) {
			filename = parent.getName() + "/" + filename;
			parent = parent.getParent();
		}
		IPath outputPath = parent.getParent().getProjectRelativePath().append("gen/" + filename + ".java");
		IFile file2 = file.getProject().getFile(outputPath);
		file2.delete(true, new NullProgressMonitor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkASTRA(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".astra")) {
			IFile file = (IFile) resource;
			if (!file.getProjectRelativePath().toString().startsWith("src")) {
				return;
			}

			System.out.println("file: " + file.getName());
			ASTRAProject project = null;
			try {
				project = ASTRAProject.getProject(file.getProject());
			} catch (CoreException e2) {
				e2.printStackTrace();
				return;
			}
			
			for (IFile f : project.getDependencies(file)) {
				try {
					f.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
				} catch (CoreException e1) {
					e1.printStackTrace();
					System.out.println();
				}
				
				ASTRAErrorHandler reporter = new ASTRAErrorHandler(f);
				try {
					project.invalidateFile(f);
					ASTRAClassElement element = project.getASTRAClassElement(f);
					if (element != null) {
						for (ParseException e : element.getErrorList()) {
							reporter.error(e);
						}
					}
				} catch (ParseException e) {
					reporter.error(e);
				} catch (Throwable e) {
					reporter.error(new ParseException("Unexpected Parser Termination: " + e.getMessage(), e, 0, 0, 0));
				}
			}
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new ASTRAResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		delta.accept(new ASTRADeltaVisitor());
	}
}
