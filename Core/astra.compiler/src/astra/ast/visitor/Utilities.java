package astra.ast.visitor;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.ParseException;

public class Utilities {
	public static boolean validatePackageAndClassName(ASTRAClassElement element, String source) 
			throws ParseException {
		String pkg = ""; 
		String nme = source; 
		
		int index = source.lastIndexOf('/');
		if (index > -1) {
			if (index > 1) {
				pkg = source.substring(0, index).replace('/', '.');
				nme = source.substring(index+1);
			} else {
				pkg = "";
				nme = source;
			}
		}

		if (nme.lastIndexOf('.') > -1) {
			if (nme.substring(nme.lastIndexOf('.')).equalsIgnoreCase(".astra"))
				nme = nme.substring(0, nme.lastIndexOf('.'));
			else {
				pkg = nme.substring(0, nme.lastIndexOf('.'));
				nme = nme.substring(nme.lastIndexOf('.')+1);
			}
		}
		
		if (!pkg.equals(element.packageElement().packageName())) {
			throw new ParseException("Package name does not match location: expected: " + 
					(pkg.equals("") ? "DEFAULT PACKAGE":pkg) + " but got: " + 
					element.packageElement().packageName(), element); 
		}
		if (!nme.equals(element.getClassDeclaration().name())) {
			throw new ParseException("ASTRA class name: " + 
						element.getClassDeclaration().name() + 
						" does not match file name: " + nme, element.getClassDeclaration()); 
		}
		return true;
	}
}
