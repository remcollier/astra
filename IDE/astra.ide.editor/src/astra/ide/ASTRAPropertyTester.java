package astra.ide;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.part.FileEditorInput;

public class ASTRAPropertyTester extends PropertyTester {
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
	
		if (property.equals("isFile")) {
			if (receiver instanceof FileEditorInput) {
				return ((FileEditorInput) receiver).getFile().getName().endsWith(".astra");
			} else if (receiver instanceof IResource) {
				return ((IResource) receiver).getName().endsWith(".astra");
			}
			return false;
		}
		return false;
	}

}
