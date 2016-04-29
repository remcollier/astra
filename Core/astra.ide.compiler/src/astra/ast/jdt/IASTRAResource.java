package astra.ast.jdt;

import java.io.InputStream;

import org.eclipse.core.resources.IProject;

public interface IASTRAResource {
	public InputStream getInputStream();
	public IProject getProject();
	public String getPath();
	public boolean exists();
	public String getSource();
	public void close();
}
