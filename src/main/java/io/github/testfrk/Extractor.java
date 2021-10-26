/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all methods with a specified annotation.
 * e.g, RequestMapping
 */
public class Extractor {


	/**
	 * @param clses          see Scanner.scan
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Method>> extract(Set<Class<?>> clses) {
		try {
			return extract(clses, (Class<? extends Annotation>) 
					Class.forName(Constants.DEFAULT_REQUESTMAPPING), null);
		} catch (ClassNotFoundException e) {
		}
		return new HashMap<>();
	}
	
	/**
	 * @param clses          see Scanner.scan
	 * @param labels         annotation should have this labels, e.g, {method, RequestMethod.POST}
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Map<String, Object> labels) {
		try {
			return extract(clses, (Class<? extends Annotation>) 
					Class.forName(Constants.DEFAULT_REQUESTMAPPING), labels);
		} catch (ClassNotFoundException e) {
		}
		return new HashMap<>();
	}
	
	/**
	 * @param clses          see Scanner.scan
	 * @param anno           e.g, RequestMapping or null 
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 */
	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno) {
		return extract(clses, anno, null);
	}
	
	/**
	 * @param clses          see Scanner.scan
	 * @param anno           e.g, RequestMapping or null 
	 * @param labels         annotation should have this labels, e.g, {method, RequestMethod.POST}
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 */
	public static Map<String, List<Method>> extract(Set<Class<?>> clses, 
			Class<? extends Annotation> anno, Map<String, Object> labels) {
		
		
		Map<String, List<Method>> map = new HashMap<>();
		
		// no class
		if (clses == null) {
			throw new NullPointerException("clses cannot be null");
		}
		
		// for each class
		for (Class<?> c : clses) {
			String key = c.getName();
			// ignore this class because of be analysed 
			if (map.containsKey(key)) {
				continue;
			}
			
			// put to mapper
			map.put(key, extractValues(anno, c, labels));
		}
		
		return map;
	}

	private static List<Method> extractValues(Class<? extends Annotation> anno, Class<?> c, Map<String, Object> labels) {
		
		List<Method> values = new ArrayList<>();
		
		// for each method
		for (Method m : c.getDeclaredMethods()) {
			// just focus on the method has a specified annotation and labels
			if (filterViaAnnotation(m, anno, labels) != null) {
				values.add(m);
			}
		}
		return values;
	}
	
	// annotation and labels can be null
	private static Method filterViaAnnotation(Method m, Class<? extends Annotation> anno, Map<String, Object> labels) {
		if (anno == null) {
			return m;
		}
		
		// get annotation
		Annotation r = m.getAnnotation(anno);
		return filterViaLabels(r, labels) == null ? null : m;
	}
	
	private static Annotation filterViaLabels(Annotation anno, Map<String, Object> labels) {
		if (labels != null && labels != null && labels.size() != 0) {
			
			if (labels.size() > 1) {
				throw new UnsupportedOperationException("TODO. support mutiple labels simultaneously later.");
			}
			
			for (String key : labels.keySet()) {
				try {
					Object value = Utils.getValue(anno, key);
					return value == labels.get(key) ? anno : null;
				} catch (NullPointerException e ) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		
		return anno;
	}
	
}
