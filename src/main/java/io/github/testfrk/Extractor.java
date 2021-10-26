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

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all methods with a specified annotation.
 */
public class Extractor {


	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno) {
		return extract(clses, anno, null);
	}
	
	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno, Map<String, String> filters) {
		Map<String, List<Method>> map = new HashMap<>();
		if (clses == null) {
			throw new NullPointerException("clses cannot be null");
		}
		
		for (Class<?> c : clses) {
			
			String key = c.getName();
			if (map.containsKey(key)) {
				continue;
			}
			
			map.put(key, extractValues(anno, c, filters));
		}
		
		return map;
	}

	private static List<Method> extractValues(Class<? extends Annotation> anno, Class<?> c, Map<String, String> filters) {
		
		List<Method> values = new ArrayList<>();
		
		for (Method m : c.getDeclaredMethods()) {
			if (filterViaAnnotation(m, anno) != null) {
				values.add(m);
			}
		}
		return values;
	}
	
	private static Method filterViaAnnotation(Method m, Class<? extends Annotation> anno) {
		if (anno == null) {
			return m;
		}
		
		Annotation r = m.getAnnotation(anno);
		return r == null ? null : m;
	}
	
}
