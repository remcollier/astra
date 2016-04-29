package astra.ast.jdt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.xml.sax.helpers.DefaultHandler;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ParseException;
import astra.ast.element.ModuleElement;
import astra.ast.visitor.CodeGeneratorVisitor;
import astra.ast.visitor.ComponentStore;
import astra.ast.visitor.ComponentVisitor;
import astra.ast.visitor.GoalCheckVisitor;
import astra.ast.visitor.TypeCheckVisitor;
import astra.dgraph.ASTRANode;
import astra.dgraph.DependencyManager;

public class ASTRAProject {
	private static final String MARKER_TYPE = "astra.ide.problem";
	private static Map<IProject, ASTRAProject> repository = new HashMap<IProject, ASTRAProject>();

	public static ASTRAProject getProject(IProject project) throws CoreException {
		if (project == null) return null;
		ASTRAProject asProject = repository.get(project);
		if (asProject == null) {
			asProject = new ASTRAProject(project);
			repository.put(project, asProject);
			asProject.loadResources();
//			try {
//				project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
//			} catch (CoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return asProject;
	}
	
	private IProject project;
	private DependencyManager manager;
	private IJavaHelper helper;
	
	
	private ASTRAProject(IProject project) {
		this.project = project;
		helper = new JDTHelper(project);
		manager = new DependencyManager(helper);
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
	
	public void deleteFile(IFile file) throws CoreException, ParseException {
		String cls = resolveToASTRAClassName(file);
		manager.deleteClass(cls);
	}
	
	public void invalidateFile(IFile file) throws CoreException, ParseException {
		String cls = resolveToASTRAClassName(file);
		manager.reloadClass(cls);
		compileClass(file,cls);
	}
	
	public ASTRAClassElement getASTRAClassElement(IFile file) throws CoreException, ParseException {
		String cls = resolveToASTRAClassName(file);
		
		if (!manager.isClassLoaded(cls)) {
			manager.loadClass(cls);
		} else {
			ASTRANode node = manager.getClass(cls);
			if (node.getErrorList().isEmpty()) return node.element();
		}
		
		if (manager.getClass(cls) == null) {
			return null;
		}
		
		return compileClass(file, cls);
	}

	private ASTRAClassElement compileClass(IFile file, String cls) throws CoreException, ParseException {
		IProgressMonitor monitor = new NullProgressMonitor();
		ASTRAClassElement element = manager.getClass(cls).element();
		
		if (element.getErrorList().isEmpty()) {
			ComponentStore store = getComponentStore(cls);
			
			ASTRANode node = manager.getClass(cls);
	
			CodeGeneratorVisitor cgv = new CodeGeneratorVisitor(helper,store);
			node.element().accept(new TypeCheckVisitor(), store);
			node.element().accept(new GoalCheckVisitor(), store);
			node.element().accept(cgv, null);

			// Generate the new code...
			IPath outputPath = file.getParent().getProjectRelativePath().
					append(file.getName().substring(0, file.getName().lastIndexOf(".")) + ".java");
			
			// Setup output file for Generated Java Code
			IFolder folder = project.getFolder("gen");
			if (!folder.exists()) {
				// Need to dynamically creeate the gen folder and add it as a source folder....
				folder.create(true, true, monitor);
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
				IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
				IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
				System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
				newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
				javaProject.setRawClasspath(newEntries, null);
			}
	
			IFile file2 = folder.getFile(outputPath.removeFirstSegments(1));
			Stack<IFolder> stack = new Stack<IFolder>();
			IFolder ifolder = (IFolder) file2.getParent();
	
			if (!ifolder.exists()) {
				while (!ifolder.exists()) {
					stack.push(ifolder);
					ifolder = (IFolder) ifolder.getParent();
				}
				while (!stack.isEmpty()) {
					stack.pop().create(true, true, monitor);
				}
			} else {
				try {
					file2.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			file2.create(new ByteArrayInputStream(cgv.toString().getBytes()), true, monitor);
			file2.getParent().getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		return element;
	}

	private ComponentStore getComponentStore(String cls) throws ParseException {
		LinkedList<ASTRANode> linearisation = manager.getLinearisation(cls);
		
		ComponentStore store = new ComponentStore();
		ComponentVisitor visitor = new ComponentVisitor(helper, store);
		for (int i=linearisation.size()-1; i >= 0; i--) {
			ASTRANode node = linearisation.get(i);
			System.out.println("processing: " + node);
			if (!node.loaded()) throw new ParseException("Could not compile: " + cls + " due to error in: " + node.element().getQualifiedName(), node.element());
			
			node.element().accept(visitor, store);
			System.out.println("store: " + store.events);
		}
		
		return store;
	}

	public String getModuleClassName(String cls, String module) throws ParseException {
		ComponentStore store = getComponentStore(cls);
		ModuleElement element = store.modules.get(module);
		if (element == null) return null;
		return element.className();
	}

	/**
	 * Gets subclass dependents...
	 * 
	 * @param file
	 * @return
	 */
	public List<IFile> getDependencies(IFile file) {
		List<IFile> list = new LinkedList<IFile>();
		String cls = resolveToASTRAClassName(file);
		for (ASTRANode node : manager.dependencyList(cls)) {
			list.add(resolveToIFile(node.element().getQualifiedName()));
		}
		return list;
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
		for (IResource resource : project.members()) {
			resource.accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					switch (resource.getType()) {
					case IResource.FOLDER:
						IFolder folder = (IFolder) resource;
						for (IResource res : folder.members()) {
							res.accept(this);
						}
						break;
					case IResource.FILE:
						IFile file = (IFile) resource;
						if (file.getName().endsWith(".astra")) {
							ASTRAErrorHandler reporter = new ASTRAErrorHandler(file);
							try {
								manager.loadClass(resolveToASTRAClassName(file));
							} catch (ParseException e) {
								reporter.error(e);
							}
						}
					}
					return false;
				}
			});
		}
	}
}
