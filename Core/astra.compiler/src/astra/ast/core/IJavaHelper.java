package astra.ast.core;

import java.util.List;

import astra.ast.element.PackageElement;
import astra.ast.formula.MethodSignature;
import astra.ast.reflection.ReflectionHelper;

public interface IJavaHelper {
	public static final int ACTION = 0;
	public static final int TERM = 1;
	public static final int FORMULA = 2;
	public static final int SENSOR = 3;
	public static final int EVENT = 4;
	
	public String resolveModule(String className);

	public void setup(PackageElement packageElement, ImportElement[] importElements);

	public IType getType(String module, MethodSignature signature);

	public String getFullClassName(String className);

	public ASTRAClassElement loadAST(String clazz) throws ParseException;
	public boolean validate(String moduleClass, MethodSignature signature);

	public List<String> getSensors(String name);

	public IJavaHelper spawn();

	public BuildContext getBuildContext();

	public long lastModified(String clazz);
}
