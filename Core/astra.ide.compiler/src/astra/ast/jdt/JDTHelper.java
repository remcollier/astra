package astra.ast.jdt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import astra.ast.core.ASTRAClassElement;
import astra.ast.core.AbstractHelper;
import astra.ast.core.BuildContext;
import astra.ast.core.IJavaHelper;
import astra.ast.core.ImportElement;
import astra.ast.core.ParseException;
import astra.ast.element.PackageElement;
import astra.ast.formula.MethodSignature;
import astra.ast.formula.MethodType;


public class JDTHelper extends AbstractHelper {
	public static Map<Integer, String> ALTERNATE = new HashMap<Integer, String>();
	public static Map<Integer, String> ANNOTATIONS = new HashMap<Integer, String>();
	private static Map<String, String> PRIMITIVE_MAP = new HashMap<String, String>();
	private static Set<String> PRIMITIVES = new HashSet<String>();

	static {
		ANNOTATIONS.put(ACTION, "ACTION");
		ANNOTATIONS.put(TERM, "TERM");
		ANNOTATIONS.put(FORMULA, "FORMULA");
		ANNOTATIONS.put(SENSOR, "SENSOR");
		ANNOTATIONS.put(EVENT, "EVENT");

		ALTERNATE.put(ACTION, "astra.core.Module.ACTION");
		ALTERNATE.put(TERM, "astra.core.Module.TERM");
		ALTERNATE.put(FORMULA, "astra.core.Module.FORMULA");
		ALTERNATE.put(SENSOR, "astra.core.Module.SENSOR");
		ALTERNATE.put(EVENT, "astra.core.Module.EVENT");

		PRIMITIVE_MAP.put("int", "java.lang.Integer");
		PRIMITIVE_MAP.put("long", "java.lang.Long");
		PRIMITIVE_MAP.put("float", "java.lang.Float");
		PRIMITIVE_MAP.put("double", "java.lang.Double");
		PRIMITIVE_MAP.put("char", "java.lang.Character");
		PRIMITIVE_MAP.put("boolean", "java.lang.Boolean");
		PRIMITIVE_MAP.put("Funct", "astra.term.Funct");

		PRIMITIVES.add("B");
		PRIMITIVES.add("C");
		PRIMITIVES.add("D");
		PRIMITIVES.add("F");
		PRIMITIVES.add("I");
		PRIMITIVES.add("J");
		PRIMITIVES.add("S");
		PRIMITIVES.add("V");
		PRIMITIVES.add("Z");
	}
	
	IProject project;
	IJavaProject javaProject;
	PackageElement packageElement;
	ImportElement[] imports;
	BuildContext context = new BuildContext();
	
	public JDTHelper(IProject project) {
		this.project = project;
		javaProject = JavaCore.create(project);
	}
	
	public BuildContext getBuildContext() {
		return context;
	}
	
	@Override
	public String resolveModule(String className) {
		org.eclipse.jdt.core.IType type = getType(className);
		return type == null ? null : type.getFullyQualifiedName();
	}
	

	private boolean isSignature(String clazz) {
		return (clazz.startsWith("[") || PRIMITIVES.contains(clazz) || 
				((clazz.startsWith("L") || clazz.startsWith("Q") || clazz.startsWith("T")) && clazz.endsWith(";")));
	}
	
	public String resolveClass(String clazz) {
		if (isSignature(clazz)) {
			return resolveClass(Signature.toString(clazz));
		}
		
		int gstart = clazz.indexOf("<");
		if (gstart >= 0) {
			int gend = clazz.lastIndexOf(">");
			String inner = clazz.substring(gstart+1, gend);
			clazz = clazz.substring(0, clazz.indexOf("<"));
			return clazz.endsWith("ActionParam") ? resolveClass(inner) : resolveClass(clazz);
		}

		String cls = PRIMITIVE_MAP.get(clazz);
		if (cls != null) {
//			System.out.println("\treturning: " + cls);
			return cls;
		}
		return clazz;
	}
	
	public IType getType(String clazz) {
		clazz = resolveClass(clazz);
		
		IType type = null;
		
		try {
			type = javaProject.findType(clazz);

			if (type == null && packageElement != null && !packageElement.packageName().equals("")) {
				type = javaProject.findType(packageElement.packageName()+"."+clazz);
			}
			
			if (type == null) {
				for (ImportElement imp : imports) {
					String im = imp.name();
					if (im.endsWith(clazz)) {
						return javaProject.findType(im);
					}
					if (im.endsWith("*")) {
						type = javaProject.findType(im.substring(0, im.length()-1) + clazz);
						if (type != null) return type;
					}
				}
				type = javaProject.findType("astra.lang." + clazz);
			}

			if (type == null) {
				// Try the default java.lang package...
				type = javaProject.findType("java.lang."+clazz);
			}

			return type;
		} catch (JavaModelException e) {
			e.printStackTrace();
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
		return getType(className).getFullyQualifiedName();
	}

	@Override
	public ASTRAClassElement loadAST(String clazz) throws ParseException {
		String filename = clazz.replace('.', File.separatorChar)+".astra";
		try {
			IFile file = project.getFile("/src/" + filename);
			if (!file.exists()) {
				for(IClasspathEntry entry : javaProject.getRawClasspath()) {
					IPath path = entry.getPath();
					if ("jar".equalsIgnoreCase(path.getFileExtension())) {
						JarFile jf = null;
						try {
							File osFile = path.toFile();
							if (osFile.exists()) {
								jf = new JarFile(path.toString());
							} else {
								file = project.getWorkspace().getRoot().getFile(path);
								if (file.exists()) {
									jf = new JarFile(file.getLocation().toOSString());
								} else {
									return null;
								}
							}
							JarEntry je = jf.getJarEntry(filename.replace(File.separatorChar, '/'));
							if (je != null) return new ASTRAClassElement(clazz,jf.getInputStream(je));
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
				return null;
			}
			
			return new ASTRAClassElement(clazz,file.getContents());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean validate(String moduleClass, MethodSignature signature) {
		org.eclipse.jdt.core.IType cls = getType(moduleClass);
		
//		System.out.println("signature: " + signature.name() + " / " + signature.termCount());
		try {
			while (cls != null) {
//				System.out.println("cls methods : " + cls.getMethods().length);
				for (IMethod mthd : cls.getMethods()) {
//					System.out.println("method: " + mthd.getElementName() + " / " + mthd.getParameterTypes().length);
					if (mthd.getElementName().equals(signature.name()) && signature.termCount() == mthd.getParameterTypes().length) {
						for (IAnnotation ann : mthd.getAnnotations()) {
							String annot = ANNOTATIONS.get(signature.type());
//							System.out.println("\tAnnotation: " + annot);
							try {
								if (ann.getElementName().equals(annot) || ann.getElementName().equals(ALTERNATE.get(signature.type()))) {
//									System.out.println("variable matching...");
									if (signature.type() == IJavaHelper.EVENT) {
										String sig = null;
										String[] params = null;
										for (IMemberValuePair mvp : ann.getMemberValuePairs()) {
											if (mvp.getMemberName().equals("signature")) {
												sig = mvp.getValue().toString();
											} else if (mvp.getMemberName().equals("types")) {
												Object[] objs = (Object[]) mvp.getValue();
												params = new String[objs.length];
												for (int i=0; i < objs.length; i++) {
													params[i] = objs[i].toString();
												}
											}
										}

										int i = 0;
										boolean match = true;
										while (match && i < params.length) {
//											System.out.println("param: " + params[i]);
//											System.out.println("sig: " + signature.type(i).type());
											match = params[i].equals(signature.type(i).type());
											i++;
										}
//										System.out.println("\tmatch: " + match);
										if (match) {
											signature.signature(sig);
											return true;
										}
									} else {
										ILocalVariable[] params = mthd.getParameters();
										int i = 0;
										boolean match = true;
										while (match && i < params.length) {
											match = matchType(params[i].getTypeSignature(), signature.type(i));
	//										System.out.println("\t\tmatched: " + i + " / " + match + " / " + params[i].getTypeSignature() + " = "  +signature.type(i));
											i++;
										}
										
										if (match) {
	//										System.out.println("\tmatch: " + mthd.getReturnType());
											String retType = null;
											if (mthd.getReturnType().startsWith("[")) {
												retType = Signature.toString(mthd.getReturnType());
											} else {
												String cl = mthd.getReturnType();
												if (isSignature(cl)) {
													cl = Signature.toString(cl);
												}
												IType type = getType(cl);
												if (type == null) {
													retType = cl;
												} else {
													retType = type.getFullyQualifiedName();
												}
											}
											
											String t = MethodType.resolveType(retType);
	//										System.out.println("retType: " + retType);
	//										System.out.println("ret: " + t);
											if (t == null) 
												signature.returnType(retType);
											else
												signature.returnType(t);
											return true;
	//									} else {
	//										System.out.println("\tno match...");
										}
									}
								}
							} catch (Throwable th) {
								th.printStackTrace();
							}
						}
					}
				}
				if (cls.getSuperclassName() == null) {
					return false;
				}
				
				cls = getType(cls.getSuperclassName());
			}
//			System.out.println("finished and failed...");
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}


	private boolean matchType(String cls, MethodType methodType) {
//		System.out.println("cls: " + cls);
		if (methodType.variable()) {
//			System.out.println("variable: " +methodType);
			if (cls.startsWith("QActionParam") || cls.startsWith("Lastra.core.ActionParam")) {
				// strip out the inner type and check it against the ASTRA type
				if (cls.indexOf('<') > -1) {
					String inner = cls.substring(cls.indexOf('<')+1, cls.lastIndexOf('>'));
					boolean result = matchType(inner, methodType);
					if (result) {
						methodType.actionParam(true);
					}
					return result;
				}
				
				// default must be true because it could not find the inner class...
				return true;
			}
		}
		
		IType type = getType(cls);

//		System.out.println("type: " +type);
		if (type == null) {
			cls = resolveClass(cls);
			if (methodType.type().equals("list") && cls.startsWith("ListTerm")) {
				methodType.primitiveType("astra.term.ListTerm");
				return true;
			} else if (methodType.type().endsWith(cls)) {
				type = getType(methodType.type());
				methodType.primitiveType(cls);
			}
		}
		
//		if (type == null) return false;
		
		// handle primitives
		if (methodType.isPrimitive()) {
			String t = type.getFullyQualifiedName();
			if (PRIMITIVE_MAP.containsKey(t)) t = PRIMITIVE_MAP.get(t);
			return methodType.validatePrimitive(t);
		}
		
		// validate raw types...
		try {
			ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
			boolean result = hierarchy.contains(getType(methodType.type()));
			if (result) methodType.primitiveType(methodType.type());
			return result;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<String> getSensors(String name) {
		List<String> list = new LinkedList<String>();
		
		IType cls = getType(name);
		try {
			while (cls != null) {
				for (IMethod mthd : cls.getMethods()) {
					if (mthd.getParameterTypes().length == 0) {
						for (IAnnotation ann : mthd.getAnnotations()) {
							String annot = ANNOTATIONS.get(SENSOR);
							if (ann.getElementName().equals(annot) || ann.getElementName().equals(ALTERNATE.get(SENSOR))) {
								list.add(mthd.getElementName());
							}
						}
					}
				}
				if (cls.getSuperclassName() == null) {
					cls = null;
				} else {
					cls = getType(cls.getSuperclassName());
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return list;
	}

	@Override
	public IJavaHelper spawn() {
		JDTHelper helper = new JDTHelper(project);
		helper.setup(packageElement, imports);
		return helper;
	}

	@Override
	public long lastModified(String clazz) {
		return 0;
	}
}
