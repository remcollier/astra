package astra.ide.launch;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public class ASTRAFileLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		// Get the java project...
		IJavaProject javaProject = getJavaProject(configuration);

		// Get a reference to the "run" loader...
		IVMInstall vm = verifyVMInstall(configuration);
		IVMRunner runner = vm.getVMRunner(mode);

		// Create VM config
		String[] cp = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
		String[] classpath = Arrays.copyOf(cp, cp.length+1);
//		for (int i = 0; i < cp.length; i++) {
//			System.out.println("cp: "+ cp[i]);
//		}
		
		classpath[cp.length] = javaProject.getProject().getLocation().append("bin").toOSString(); 
//		System.out.println("cp: " + javaProject.getProject().getLocation().append("bin").toOSString());

		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(getMainTypeName(configuration), classpath);
		runConfig.setWorkingDirectory(javaProject.getProject().getLocation().toOSString());

		// Launch the configuration
		runner.run(runConfig, launch, monitor);
	}
}
