package astra.ide.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import astra.ide.Activator;
import astra.ide.builder.ASTRABuilder;
import astra.ide.builder.ASTRANature;

public class NewProjectWizard extends Wizard implements INewWizard {
	private ASTRAWizardNewProjectCreationPage namePage;
	
	@Override
	public boolean performFinish() {
		try {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) {
					createProject(monitor != null ? monitor : new NullProgressMonitor());
		        }
		    };
		    getContainer().run(false,true,op);
		} catch(InvocationTargetException x) {
			reportError(x);
			return false;
		} catch(InterruptedException x) {
			reportError(x);
			return false;
		}

		return true; 
	}
	
	@SuppressWarnings("restriction")
	private void createProject(IProgressMonitor monitor) {
		monitor.beginTask("Creating New ASTRA Project", 50);
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			
			monitor.subTask("Creating Directories");
			IProject project = root.getProject(namePage.getProjectName());
			IProjectDescription description = workspace.newProjectDescription(project.getName());
			if (!Platform.getLocation().equals(namePage.getLocationPath()))
				description.setLocation(namePage.getLocationPath());
			
			// Add in the Java and AFAPL2 Natures
			description.setNatureIds(new String[] { JavaCore.NATURE_ID, ASTRANature.NATURE_ID });
			
			// Add the AFAPL2 Builder
			ICommand astraBuilderCommand = description.newCommand();
			ICommand javaBuilderCommand = description.newCommand();
			astraBuilderCommand.setBuilderName(ASTRABuilder.BUILDER_ID);
			javaBuilderCommand.setBuilderName(JavaCore.BUILDER_ID);
			description.setBuildSpec(new ICommand[] { javaBuilderCommand, astraBuilderCommand });

			// Create the project
			project.create(description, monitor);
			monitor.worked(10);
			
			// Set up some properties
			project.open(monitor);
			monitor.worked(10);
			
			// Create the basic directories
			IPath projectPath = project.getFullPath();
			IPath srcPath = projectPath.append("src");
			IPath binPath = projectPath.append("bin");
			
			IFolder srcFolder = root.getFolder(srcPath);
			IFolder binFolder = root.getFolder(binPath);
			createFolderHelper(srcFolder, monitor);
			createFolderHelper(binFolder, monitor);
			monitor.worked(10);
			
			IJavaProject javaProject = JavaCore.create(project);
			Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
			entries.add(JavaRuntime.getDefaultJREContainerEntry());
			entries.add(JavaCore.newSourceEntry(srcPath));

			entries.add(JavaCore.newLibraryEntry(getLibrary("astra.jar"), null, null));
			entries.add(JavaCore.newLibraryEntry(getLibrary("mas-acre.jar"), null, null));
			entries.add(JavaCore.newLibraryEntry(getLibrary("cartago.jar"), null, null));
			entries.add(JavaCore.newLibraryEntry(getLibrary("json-simple-1.1.1.jar"), null, null));
			if (this.namePage.environment().equals("EIS")) {
				entries.add(JavaCore.newLibraryEntry(getLibrary("eis-"+namePage.eisVersion()+".jar"), null, null));
			}
//			File file = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().append("astra").toOSString());
//			for (File child : file.listFiles()) {
//				if (child.isFile() && child.getName().endsWith(".jar")) {
//					entries.add(JavaCore.newLibraryEntry(new Path(child.getAbsolutePath()), null, null));
//				}
//			}
			javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
		} catch (CoreException x) {
			reportError(x);
		} finally {
			monitor.done();
		}
	}

	private IPath getLibrary(String string) {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().append("astra").append(string);
	}

	private void createFolderHelper(IFolder folder, IProgressMonitor monitor)
			throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder && (!((IFolder) parent).exists()))
				createFolderHelper((IFolder) parent, monitor);
			folder.create(false, true, monitor);
		}
	}

	private void reportError(Exception x) {
		ErrorDialog.openError(
				getShell(), "ASTRA New Project Wizard",
				"An Exception occurred when running the project", makeStatus(x));
	}

	private IStatus makeStatus(Exception x) {
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							IStatus.ERROR, x.getMessage(), x);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setNeedsProgressMonitor(true);
	}

	public void addPages() {
		super.addPages();
		namePage = new ASTRAWizardNewProjectCreationPage();
		namePage.setTitle("New ASTRA Project");
		namePage.setDescription("This wizard helps you to create a new ASTRA project");
		namePage.setImageDescriptor(ImageDescriptor.createFromFile(getClass(), "icons/afProject.gif"));
		addPage(namePage);
//		configurationPage = new EnvironmentConfigurationPage();
//		addPage(configurationPage);
	}
}
