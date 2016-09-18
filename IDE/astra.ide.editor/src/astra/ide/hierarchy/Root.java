package astra.ide.hierarchy;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ParseException;
import astra.ast.jdt.ASTRAProject;

public class Root extends PlatformObject {
	IFile file = null;
	
	public Root() {
	}
	
	public Root(IFile file) {
		this.file = file;
	}
	
	public ASTRAClassElement getUnit() {
		try {
			return ASTRAProject.getProject(file.getProject()).getASTRAClassElement(file);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void setUnit(ASTRAClassElement unit) {
//		this.unit = unit;
//	}
}
