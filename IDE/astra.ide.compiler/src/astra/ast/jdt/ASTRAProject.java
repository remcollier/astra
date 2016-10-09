package astra.ast.jdt;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.xml.sax.helpers.DefaultHandler;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import astra.ast.element.ModuleElement;
import astra.ast.visitor.ComponentStore;
import astra.ast.visitor.ComponentVisitor;
import astra.compiler.ASTRAClass;
import astra.compiler.ASTRAClassHierarchy;

public class ASTRAProject {
	public static final String MARKER_TYPE = "astra.ide.problem";
	private static Map<IProject, ASTRAProject> repository = new HashMap<IProject, ASTRAProject>();

	public static ASTRAProject getProject(IProject project) throws CoreException {
		if (project == null) return null;
		ASTRAProject asProject = repository.get(project);
		if (asProject == null) {
			asProject = new ASTRAProject(project);
			repository.put(project, asProject);
			asProject.loadResources();
		}
		return asProject;
	}
	
	private IProject project;
	private ASTRAClassHierarchy hierarchy;
	private IJavaHelper helper;
	
	
	private ASTRAProject(IProject project) {
		this.project = project;
		helper = new JDTHelper(project);
		hierarchy = new ASTRAClassHierarchy(helper);
	}

	public IASTRAResource getResource(String name) throws CoreException {
		IFile file = project.getFile("src/" + name);
		if (file.exists())
			return new FileResource(file);

		File projRoot = new File(project.getLocation().toOSString());

		// Trying classpath...
		IJavaProject jProject = JavaCore.create(project);
		for (IClasspathEntry entry : jProject.getRawClasspath()) {
			String ety = entry.getPath().toOSString();
			 
			if (ety.substring(ety.length() - 3).equalsIgnoreCase("jar")) {
				// Step 1: Get a reference to the classpath resource...
				File f = new File(ety);
				if (!f.exists()) {
					f = new File(projRoot.getParentFile(), entry.getPath().toOSString());
					if (!f.exists()) {
						f = new File(projRoot, entry.getPath().toOSString());
						if (!f.exists())
							return null;
					}
				}

				// Examine it for .aspeak files
				try {
					JarFile jarFile = new JarFile(f);
					Enumeration<JarEntry> e = jarFile.entries();
					while (e.hasMoreElements()) {
						JarEntry jarEntry = e.nextElement();
						if (jarEntry.toString().equals(name)) {
							return new JarResource(project, jarFile, jarEntry);
						}
					}
					jarFile.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					// Ignore this exception as it indicates that the file is
					// not in the jar...
				}
			}
		}

		for (IProject proj : project.getReferencedProjects()) {
			IASTRAResource resource = getProject(proj).getResource(name);
			if (resource != null) {
				return resource;
			}
		}

		return null;
	}
	
	public void removeASTRAClass(IFile file) throws CoreException, ParseException {
		Map<String, List<ParseException>> errors = new HashMap<String, List<ParseException>>();
		
		// Clear any existing errors for this file
//		deleteMarkers(file);
		String cls = resolveToASTRAClassName(file);
		hierarchy.deleteClass(cls, errors);
		
		displayErrors(errors);
	}
	
	private void displayErrors(Map<String, List<ParseException>> errors) {
		for (Entry<String, List<ParseException>> entry : errors.entrySet()) {
			IFile f = this.resolveToIFile(entry.getKey());
			
			// Clear any existing errors for this file
			deleteMarkers(f);
			
			// Add any new errors
			ASTRAErrorHandler reporter = new ASTRAErrorHandler(f);
			for(ParseException e : entry.getValue()) {
				reporter.error(e);
			}
		}
	}

	public ASTRAClassElement getASTRAClassElement(IFile file) throws CoreException, ParseException {
		Map<String, List<ParseException>> errors = new HashMap<String, List<ParseException>>();
		
		String cls = resolveToASTRAClassName(file);
		
		if (!hierarchy.contains(cls)) {
			hierarchy.compile(cls, errors);
		}
		
		if (!errors.isEmpty()) {
			displayErrors(errors);
			return null;
		}
		return hierarchy.getClass(cls).element();
	}

	private ComponentStore getComponentStore(String cls) throws ParseException {
		LinkedList<ASTRAClass> linearisation = hierarchy.getLinearisation(cls);
		
		ComponentStore store = new ComponentStore();
		ComponentVisitor visitor = new ComponentVisitor(helper, store);
		for (int i=linearisation.size()-1; i >= 0; i--) {
			ASTRAClass node = linearisation.get(i);
			if (!node.isLoaded()) throw new ParseException("Could not compile: " + cls + " due to error in: " + node.element().getQualifiedName(), node.element());
			
			node.element().accept(visitor, store);
		}
		
		return store;
	}

	public String getModuleClassName(String cls, String module) throws ParseException {
		ComponentStore store = getComponentStore(cls);
		ModuleElement element = store.modules.get(module);
		if (element == null) return null;
		return element.className();
	}

	private IFile resolveToIFile(String name) {
		String filename = "src/" + name.replace('.', File.separatorChar)+".astra";
		return project.getFile(filename);
	}

	private String resolveToASTRAClassName(IFile file) {
		String pkg = file.getParent().getProjectRelativePath().removeFirstSegments(1).toString();
		String filename = file.getName().substring(0, file.getName().lastIndexOf("."));
		if (pkg.equals("")) return filename;
		return pkg.replaceAll("/", ".")+"."+filename;
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

	private void loadResources() throws CoreException {
//		for (IResource resource : project.members()) {
//			resource.accept(new IResourceVisitor() {
//				@Override
//				public boolean visit(IResource resource) throws CoreException {
//					switch (resource.getType()) {
//					case IResource.FOLDER:
//						IFolder folder = (IFolder) resource;
//						for (IResource res : folder.members()) {
//							res.accept(this);
//						}
//						break;
//					case IResource.FILE:
//						IFile file = (IFile) resource;
//						if (file.getName().endsWith(".astra")) {
//							compile(file);
//						}
//					}
//					return false;
//				}
//			});
//		}
	}

	public void compile(IFile file) throws CoreException {
		deleteMarkers(file);
		Map<String, List<ParseException>> errors = new HashMap<String, List<ParseException>>();
		
		hierarchy.compile(resolveToASTRAClassName(file), errors);
		
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		displayErrors(errors);
	}

	private void deleteMarkers(IFile file) {
		// Clear any existing errors for this file
		try {
			file.deleteMarkers(ASTRAProject.MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	public void deleteGeneratedFile(IFile file) {
		IPath outputPath = file.getParent().getProjectRelativePath().
				append(file.getName().substring(0, file.getName().lastIndexOf(".")) + ".java");
		System.out.println("Deleting File: " + outputPath.toString());
		// Setup output file for Generated Java Code
		IFolder folder = project.getFolder("gen");
		IFile file2 = folder.getFile(outputPath.removeFirstSegments(1));

		if (file2.exists()) {
			try {
				file2.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Deleted File: " + outputPath.toString());
	}
}
