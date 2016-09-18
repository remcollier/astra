package astra.ast.reflection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.AbstractHelper;
import astra.ast.core.BuildContext;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ITerm;
import astra.ast.core.ImportElement;
import astra.ast.core.NoSuchASTRAClassException;
import astra.ast.core.ParseException;
import astra.ast.core.Token;
import astra.ast.element.PackageElement;
import astra.ast.formula.MethodSignature;
import astra.ast.formula.MethodType;
import astra.ast.formula.PredicateFormula;
import astra.ast.term.Variable;
import astra.ast.type.ObjectType;
import astra.core.ActionParam;
import astra.core.Module.EVENT;

public class ReflectionHelper extends AbstractHelper {
	private static Map<Integer, String> annotations = new HashMap<Integer, String>();
	
	static {
		annotations.put(ACTION, "astra.core.Module.ACTION");
		annotations.put(TERM, "astra.core.Module.TERM");
		annotations.put(FORMULA, "astra.core.Module.FORMULA");
		annotations.put(SENSOR, "astra.core.Module.SENSOR");
		annotations.put(EVENT, "astra.core.Module.EVENT");
	}

	PackageElement packageElement;
	ImportElement[] imports;
	BuildContext context = new BuildContext();

	public BuildContext getBuildContext() {
		return context;
	}

	@Override
	public String resolveModule(String className) {
		Class<?> clazz = resolveClass(className);
		return clazz == null ? null : clazz.getCanonicalName();
	}

	public Class<?> resolveClass(String clazz) {
		try {
			return Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			try {
				// System.out.println("trying: " +
				// packageElement.packageName()+"."+clazz);
				if (packageElement != null)
					return Class.forName(packageElement.packageName() + "." + clazz);
			} catch (ClassNotFoundException e0) {
			}

			for (ImportElement imp : imports) {
				String im = imp.name();
				if (im.endsWith(clazz)) {
					try {
						return Class.forName(im);
					} catch (ClassNotFoundException e1) {
					}
				}
				if (im.endsWith("*")) {
					try {
						return Class.forName(im.substring(0, im.length() - 1) + clazz);
					} catch (ClassNotFoundException e1) {
					}
				}
			}
			try {
				return Class.forName("astra.lang." + clazz);
			} catch (ClassNotFoundException e2) {
			}
		}

		return null;
	}

	@Override
	public void setup(PackageElement packageElement, ImportElement[] imports) {
		this.packageElement = packageElement;
		this.imports = imports;
	}

	@Override
	public String getFullClassName(String className) {
		return resolveClass(className).getCanonicalName();
	}

	@Override
	public ASTRAClassElement loadAST(String clazz) throws ParseException {
		InputStream in = getClass().getResourceAsStream("/" + clazz.replace(".", "/") + ".astra");
		if (in == null) {
			throw new NoSuchASTRAClassException("No such ASTRA class: " + clazz);
		}
		return new ASTRAClassElement(clazz,in);
	}

	private Method getMatchingMethod(String moduleClass, MethodSignature signature) {
		Class<?> cls = resolveClass(moduleClass);

		while (cls.getSuperclass() != null) {
			for (Method mthd : cls.getMethods()) {
				if (mthd.getName().equals(signature.name())
						&& signature.termCount() == (mthd.getParameterTypes().length-(signature.symbol() ? 1:0))) {
					if (signature.type() == -1) {
						return validate(signature, mthd) ? mthd:null;
					} else {
						for (Annotation ann : mthd.getAnnotations()) {
							if (ann.annotationType().getCanonicalName().equals(annotations.get(signature.type()))) {
								if (signature.type() == IJavaHelper.EVENT) {
									if (ann instanceof EVENT) {
										String[] params = ((EVENT) ann).types();
										int i = 0;
										boolean match = true;
										while (match && i < params.length) {
											match = params[i].equals(signature.type(i).type());
											i++;
										}
										if (match) {
											signature.signature(((EVENT) ann).signature());
											String retType = mthd.getReturnType().getCanonicalName();
											String t = MethodType.resolveType(retType);
											if (t == null)
												signature.returnType(retType);
											else
												signature.returnType(t);
											return mthd;
										}
									}
								} else {
									if (validate(signature, mthd)) {
										return mthd;
									}
								}
							}
						}
					}
				}
			}
			cls = cls.getSuperclass();
		}
		return null;
	}

	@Override
	public boolean validate(String moduleClass, MethodSignature signature) {
		return getMatchingMethod(moduleClass, signature) != null;
	}

	private boolean validate(MethodSignature signature, Method mthd) {
		Type[] params = mthd.getGenericParameterTypes();
		int i = 0;
		boolean match = true;
		while (match && i < params.length) {
			match = matchType(params[i], signature.type(i));
			i++;
		}

		if (match) {
			String retType = mthd.getReturnType().getCanonicalName();
			String t = MethodType.resolveType(retType);
			if (t == null)
				signature.returnType(retType);
			else
				signature.returnType(t);
			return true;
		}
		return false;
	}

	private boolean matchType(Type cls, MethodType methodType) {
		if (methodType.variable()) {
			if (cls instanceof ParameterizedType) {
				ParameterizedType t = (ParameterizedType) cls;
				if (t.getRawType().equals(ActionParam.class)) {
					boolean result = matchType(t.getActualTypeArguments()[0], methodType);
					if (result) {
						methodType.actionParam(true);
					}
					return result;
				}
			}
		}

		// handle primitives
		if (methodType.isPrimitive()) {
			return methodType.validatePrimitive(((Class<?>) cls).getCanonicalName());
		}

		// validate raw types...
		try {
			Class<?> cl = Class.forName(methodType.type());
			if (((Class<?>) cls).isAssignableFrom(cl)) {
				methodType.primitiveType(cl.getCanonicalName());
				return true;
			}
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public List<String> getSensors(String name) {
		List<String> list = new LinkedList<String>();

		Class<?> cls = resolveClass(name);
//		System.out.println("class: " + name + " / cls=" + cls);
		for (Method mthd : cls.getMethods()) {
//			System.out.println("\tmethod: " + mthd);
			for (Annotation ann : mthd.getAnnotations()) {
//				System.out.println("\t\tannotation: " + ann);
				if (mthd.getParameterTypes().length == 0 && ann.toString().startsWith("@astra.core.Module$SENSOR")) {
					
					list.add(mthd.getName());
				}
			}
		}

		return list;
	}

	@Override
	public IJavaHelper spawn() {
		return new ReflectionHelper();
	}

	public long lastModified(String clazz) {
		URL url = getClass().getResource("/" + clazz.replace(".", "/") + ".astra");
		try {
			return url.openConnection().getLastModified();
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public boolean hasAutoAction(String className) {
		return validate(className, getAutoMethodSignature()); 
	}

	private MethodSignature getAutoMethodSignature() {
		Variable V = new Variable("X",null,null,null);
		V.setType(new ObjectType(Token.OBJECT_TYPE, "astra.formula.Predicate"));
		Variable V2 = new Variable("Y",null,null,null);
		V2.setType(new ObjectType(Token.OBJECT_TYPE, "astra.core.Intention"));
		return new MethodSignature(
				new PredicateFormula("auto_action", 
						Arrays.asList((ITerm) V2, (ITerm) V),
						null, null, null
				),
				-1
		);
	}
	
	@Override
	public boolean hasAutoFormula(String className) {
		Variable V = new Variable("X",null,null,null);
		V.setType(new ObjectType(Token.OBJECT_TYPE, "astra.formula.Predicate"));
		return validate(className, 
				new MethodSignature(
						new PredicateFormula("auto_formula", 
								Arrays.asList((ITerm) V),
								null, null, null
						),
						-1
				)
		);
	}

	@Override
	public boolean getEventSymbols(String className, MethodSignature signature, String symbol) {
		Class<?> cls = resolveClass(className);

		while (cls.getSuperclass() != null) {
			for (Method mthd : cls.getMethods()) {
				if (mthd.getName().equals(signature.name())) {
					if (signature.termCount() != (mthd.getParameterTypes().length-(signature.symbol() ? 1:0))) continue;
					
					for (Annotation ann : mthd.getAnnotations()) {
						if (ann.annotationType().getCanonicalName().equals(annotations.get(signature.type()))) {
							String[] params = ((EVENT) ann).symbols();
							for (int i = 0; i < params.length; i++) {
								if (params[i].equals(symbol)) return true;
							}
						}
					}
				}
			}
			cls = cls.getSuperclass();
		}
		return false;
	}

	@Override
	public boolean suppressAutoActionNotifications(String className) {
		Method mthd = getMatchingMethod(className, getAutoMethodSignature());
		for (Annotation ann : mthd.getAnnotations()) {
			if (ann.annotationType().getCanonicalName().equals("astra.core.Module.SUPPRESS_NOTIFICATIONS")) return true;
		}
		return false;
	}
}
