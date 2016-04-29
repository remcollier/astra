package astra.ast.reflection;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.AbstractHelper;
import astra.ast.core.BuildContext;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ImportElement;
import astra.ast.core.NoSuchASTRAClassException;
import astra.ast.core.ParseException;
import astra.ast.element.PackageElement;
import astra.ast.formula.MethodSignature;
import astra.ast.formula.MethodType;
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

	@Override
	public boolean validate(String moduleClass, MethodSignature signature) {
		Class<?> cls = resolveClass(moduleClass);
//		 System.out.println("signature: "+ signature.name() + " / " + signature.termCount());

		while (cls.getSuperclass() != null) {
			for (Method mthd : cls.getMethods()) {
//				 System.out.println("mthd: " + mthd.getName() + " / " + mthd.getParameterTypes().length);
				if (mthd.getName().equals(signature.name())
						&& signature.termCount() == mthd.getParameterTypes().length) {
//					 System.out.println("signature-annot: " + annotations.get(signature.type()));
					for (Annotation ann : mthd.getAnnotations()) {
//						 System.out.println("\ttype: " + ann.annotationType().getCanonicalName());
						if (ann.annotationType().getCanonicalName().equals(annotations.get(signature.type()))) {
//							 System.out.println("signature type: " + signature.type());
							if (signature.type() == IJavaHelper.EVENT) {
								if (ann instanceof EVENT) {
									String[] params = ((EVENT) ann).types();
									int i = 0;
									boolean match = true;
									while (match && i < params.length) {
//										System.out.println("param: " + params[i]);
//										System.out.println("sig: " + signature.type(i).type());
										match = params[i].equals(signature.type(i).type());
										i++;
									}
//									System.out.println("\tmatch: " + match);
									if (match) {
										signature.signature(((EVENT) ann).signature());
										String retType = mthd.getReturnType().getCanonicalName();
										String t = MethodType.resolveType(retType);
//										System.out.println("\treturn type: " + t);
										if (t == null)
											signature.returnType(retType);
										else
											signature.returnType(t);
										return true;
									}
								}
							} else {
								Type[] params = mthd.getGenericParameterTypes();
								int i = 0;
								boolean match = true;
								while (match && i < params.length) {
//									System.out.println("param: " + params[i]);
//									System.out.println("sig: " + signature.type(i));
									match = matchType(params[i], signature.type(i));
									i++;
								}
//								System.out.println("\tmatch: " + match);
								if (match) {
									String retType = mthd.getReturnType().getCanonicalName();
									String t = MethodType.resolveType(retType);
//									System.out.println("\treturn type: " + t);
									if (t == null)
										signature.returnType(retType);
									else
										signature.returnType(t);
									return true;
								}
							}
						}
					}
				}
			}
			cls = cls.getSuperclass();
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
}
