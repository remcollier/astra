package astra.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ASTRAClassLoader {
	private static ASTRAClassLoader defaultClassLoader = new ASTRAClassLoader();
	
	public static ASTRAClassLoader getDefaultClassLoader() {
		return defaultClassLoader;
	}
	
	private List<ClassLoader> loaders = new LinkedList<ClassLoader>();
	private Map<String, ASTRAClass> classes = new HashMap<String, ASTRAClass>();
	
	{
		loaders.add(ClassLoader.getSystemClassLoader());
	}
	
	public void registerClassLoader(ClassLoader loader) {
		loaders.add(loader);
	}
	
	/**
	 * Loads the corrsponding astra class if it is not already loaded. if it is loaded, then
	 * it returns the current reference.
	 * 
	 * @param url
	 * @return
	 * @throws ASTRAClassNotFoundException
	 */
	public ASTRAClass loadClass(String url) throws ASTRAClassNotFoundException {
		ASTRAClass clazz = classes.get(url);
		if (clazz == null) {
			Class<?> c = null;
			for (ClassLoader loader : loaders) {
				c = doLoadClass(loader, url);
				if (c != null) break;
			}	
			if (c != null) {
				try {
					clazz = (ASTRAClass) c.newInstance();
				} catch (InstantiationException e) {
					throw new ASTRAClassNotFoundException("Could not load ASTRA class: " + url,e);
				} catch (IllegalAccessException e) {
					throw new ASTRAClassNotFoundException("Could not load ASTRA class: " + url,e);
				}
			} else {
				throw new ASTRAClassNotFoundException("Could not find ASTRA class: " + url);
			}
			classes.put(url, clazz);
		}
			
		return clazz;
	}
	
	public ASTRAClass loadClass(Class<ASTRAClass> cls) throws ASTRAClassNotFoundException {
		ASTRAClass clazz = classes.get(cls.getCanonicalName());
		if (clazz == null) {
			try {
				clazz = (ASTRAClass) cls.newInstance();
			} catch (InstantiationException e) {
				throw new ASTRAClassNotFoundException("Could not load ASTRA class: " + cls.getCanonicalName(),e);
			} catch (IllegalAccessException e) {
				throw new ASTRAClassNotFoundException("Could not load ASTRA class: " + cls.getCanonicalName(),e);
			}
			classes.put(cls.getCanonicalName(), clazz);
		}
			
		return clazz;
	}

	private Class<?> doLoadClass(ClassLoader loader, String url) {
		System.out.println("loading class: " + url);
		try {
			return loader.loadClass(url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
