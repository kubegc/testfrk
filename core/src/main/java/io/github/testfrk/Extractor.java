/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.github.testfrk.utils.AnnoUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 * 
 * find all methods with a specified annotation.
 * e.g, RequestMapping
 */
public class Extractor {

	public final static Logger m_logger = Logger.getLogger(Extractor.class.getName());
	
	/**
	 * @param clses         待分析的类集合，见Scanner.scan
	 * @return 类名和方法名集合, 比如{io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception  异常
	 */
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> clses) throws Exception {
		return extract(clses, Constants.DEFAULT_ALL);
	}
	
	/**
	 * @param clses          待分析的类集合，见Scanner.scan
	 * @param labels         annotation should have this labels, e.g, {method, RequestMethod.POST}
	 * @return 类名和方法名集合, 比如{io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception  异常
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> clses, Map<String, Object> labels) throws Exception {
		try {
			return extract(clses, (Class<? extends Annotation>) 
					Class.forName(Constants.DEFAULT_REQUESTMAPPING), labels);
		} catch (ClassNotFoundException e) {
			m_logger.warning("找不到任何类");
		}
		return new HashMap<>();
	}
	
	/**
	 * @param clses          待分析的类集合，见Scanner.scan
	 * @param anno           e.g, RequestMapping or null 
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception 
	 */
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno) throws Exception {
		return extract(clses, anno, new HashMap<>());
	}
	
	/**
	 * @param clses          see Scanner.scan
	 * @param anno           e.g, RequestMapping or null 
	 * @param labels         annotation should have this labels, e.g, {method, RequestMethod.POST}
	 * @return classname-methods mapping, e.g, {io.github.testfrk.springboot.TestServer=[], io.github.testfrk.springboot.controllers.UserController=[public java.lang.Object io.github.testfrk.springboot.controllers.UserController.echoHello2(java.lang.String,int,java.lang.String)]}
	 * @throws Exception 
	 */
	public static Map<String, List<MethodAndType>> extract(Set<Class<?>> clses, 
			Class<? extends Annotation> anno, Map<String, Object> labels) throws Exception {
		
		
		Map<String, List<MethodAndType>> map = new HashMap<>();
		
		// no class
		if (clses == null) {
			throw new NullPointerException("parameter clses cannot be null, see Scanner.scan");
		}
		
		// for each class
		for (Class<?> c : clses) {

			// classname
			String cn = c.getName();
			// ignore this class because of be analyzed 
			if (map.containsKey(cn)) {
				continue;
			}
			
			// classname-methods mapping
			map.put(cn, extractValues(anno, c, labels == null ? new HashMap<>() : labels));
		}
		
		return map;
	}

	/**
	 * @param anno          annotations   
	 * @param c             class
	 * @param labels        labels
	 * @return the methods with a specified annotation and labels
	 * @throws Exception 
	 */
	private static List<MethodAndType> extractValues(Class<? extends Annotation> anno, Class<?> c, Map<String, Object> labels) throws Exception {
		
		List<MethodAndType> values = new ArrayList<>();
		
		// for each method
		for (Method m : c.getDeclaredMethods()) {
			// just focus on the method has a specified annotation and labels
			if (filterViaAnnotation(m, anno, labels) != null) {
				Object value = AnnoUtil.getValue(
						m.getAnnotation(anno), labels.keySet()
						.toArray(new String[] {})[0]);
				values.add(new MethodAndType(value.toString(), m));
			}
		}
		return values;
	}
	
	/**
	 * @param m            method
	 * @param anno         annotation
	 * @param labels       labels
	 * @return  method
	 */
	// annotation and labels can be null
	private static Method filterViaAnnotation(Method m, Class<? extends Annotation> anno, Map<String, Object> labels) {
		
		if (anno == null) {
			return m;
		}
		
		// get annotation
		Annotation r = m.getAnnotation(anno);
		return filterViaLabels(r, labels) == null ? null : m;
	}
	
	
	/**
	 * @param anno         annotation
	 * @param labels       labels
	 * @return 
	 */
	private static Annotation filterViaLabels(Annotation anno, Map<String, Object> labels) {
		
		for (String func : labels.keySet()) {
			try {
				Object value = AnnoUtil.getValue(anno, func);
				if (labels.get(func).equals("ALL") || value == labels.get(func)) {
					return anno;
				}
			} catch (Exception e) {
				
			}
		}
		
		return null;
	}
	
	/**
	 * @author wuheng@iscas.ac.cn
	 * @since  0.6
	 *
	 */
	public static class MethodAndType {
		
		/**
		 * Get/Post/Put/Delete
		 */
		protected final String type;
		
		/**
		 * 方法名
		 */
		protected final Method method;

		public MethodAndType(String type, Method method) {
			super();
			this.type = type;
			this.method = method;
		}

		public String getType() {
			return type;
		}

		public Method getMethod() {
			return method;
		}

		@Override
		public String toString() {
			return type + ":" + method;
		}

	}
}
