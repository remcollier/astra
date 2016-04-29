package astra.ast.jdt;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IProject;

public final class JarResource implements IASTRAResource {
	private JarFile jarFile;
	private JarEntry jarEntry;
	private IProject project;
	private InputStream in;
	
	public JarResource(IProject project, JarFile jarFile, JarEntry jarEntry) {
		this.project = project;
		this.jarFile = jarFile;
		this.jarEntry = jarEntry;
	}

	@Override
	public InputStream getInputStream() {
		try {
			in = jarFile.getInputStream(jarEntry);
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (getClass().isInstance(obj)) {
			return jarEntry.getName().equals(((JarResource) obj).jarEntry.getName()) && 
					jarFile.getName().equals(((JarResource) obj).jarFile.getName());
		}
		return false;
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public String getPath() {
		return jarEntry.getName();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getSource() {
		return jarFile.getName();
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
		}
	}
}
