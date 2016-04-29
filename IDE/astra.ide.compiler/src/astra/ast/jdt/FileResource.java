package astra.ast.jdt;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public final class FileResource implements IASTRAResource {
	IFile file;
	String path;
	private InputStream in;

	public FileResource(IFile file) {
		this.file = file;
		path = file.getFullPath().toString();
		
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		if (path.startsWith(file.getProject().getName())) {
			// Remove project
			path = path.substring((path.indexOf("/")+1), path.length());
		}		

		if (path.startsWith("src")) {
			path = path.substring(path.indexOf("/")+1, path.length());
		}
	}
	
	public InputStream getInputStream() {
		try {
			in = file.getContents();
			return in;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean equals(Object obj) {
		if (FileResource.class.isInstance(obj)) {
			return file.equals(((FileResource) obj).file);
		}
		return false;
	}

	@Override
	public IProject getProject() {
		return file.getProject();
	}
	
	public IFile getFile() {
		return file;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getSource() {
		return file.getProject().getFullPath().toOSString();
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
		}
	}
}
