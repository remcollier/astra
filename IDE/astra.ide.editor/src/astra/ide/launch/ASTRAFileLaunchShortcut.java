package astra.ide.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import astra.ide.IASTRAConstants;

public class ASTRAFileLaunchShortcut implements ILaunchShortcut {

	protected ILaunchConfiguration createConfiguration(IFile type) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, type.getName());
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
//					type.getProjectRelativePath().toOSString()
					((ICompilationUnit) JavaCore.create(type)).findPrimaryType().getFullyQualifiedName()
			);
			
			wc.setAttribute(
				 IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				 type.getProject().getName()
			);
			config = wc.doSave();
		} catch (CoreException ce) {
			ce.printStackTrace();
		}
		return config;
	}

	protected ILaunchConfiguration createConfiguration(String project, String type) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, type.substring(type.lastIndexOf('.')+1));
			wc.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					type
			);
			
			wc.setAttribute(
				 IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				 project
			);
		 
			config = wc.doSave();
		} catch (CoreException ce) {
			ce.printStackTrace();
		}
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.debug.ui.launcher.JavaLaunchShortcut#
	 * getConfigurationType()
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(IASTRAConstants.FILE_ID);
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof FileEditorInput) {
			searchAndLaunch(new Object[] { ((FileEditorInput) input).getFile() }, mode);
		}
	}

	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
		}
	}

	protected boolean searchAndLaunch(Object[] search, String mode) {
		for (Object object : search) {
			if (object instanceof IFile && ((IFile) object).getName().endsWith(".astra")) {
				IFile file = (IFile) object;
				String type = file.getProjectRelativePath().removeFirstSegments(1).removeFileExtension().toString().replace('/', '.');
				launch(file.getProject().getName(), type, mode);
				return true;
			}
		}
		return false;
	}

	protected void launch(String name, String type, String mode) {
		try {
			ILaunchConfiguration config = createConfiguration(name, type);
			if (config != null) {
				config.launch(mode, null);
			}
		} catch (CoreException e) {
		}
	}
}
