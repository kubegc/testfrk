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
 */
public class Extractor {


	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno) {
		return extract(clses, anno, null);
	}
	
	public static Map<String, List<Method>> extract(Set<Class<?>> clses, Class<? extends Annotation> anno, Map<String, Object> labels) {
		Map<String, List<Method>> map = new HashMap<>();
		
		if (clses == null) {
			throw new NullPointerException("clses cannot be null");
		}
		
		for (Class<?> c : clses) {
			
			String key = c.getName();
			if (map.containsKey(key)) {
				continue;
			}
			
			map.put(key, extractValues(anno, c, labels));
		}
		
		return map;
	}

	private static List<Method> extractValues(Class<? extends Annotation> anno, Class<?> c, Map<String, Object> labels) {
		
		List<Method> values = new ArrayList<>();
		
		for (Method m : c.getDeclaredMethods()) {
			if (filterViaAnnotation(m, anno, labels) != null) {
				values.add(m);
			}
		}
		return values;
	}
	
	private static Method filterViaAnnotation(Method m, Class<? extends Annotation> anno, Map<String, Object> labels) {
		if (anno == null) {
			return m;
		}
		
		Annotation r = m.getAnnotation(anno);
		return filterViaLabels(r, labels) == null ? null : m;
	}
	
	private static Annotation filterViaLabels(Annotation anno, Map<String, Object> labels) {
		if (labels != null && labels != null && labels.size() != 0) {
			for (String key : labels.keySet()) {
				try {
					Class<? extends Annotation> annotationType = anno.annotationType();
					Method m = annotationType.getMethod(key);
					Object value = m.invoke(anno);
					if (value.getClass().isArray()) {
						Object[] v = (Object[]) value;
						return v[0] == labels.get(key) ? anno : null;
					} else {
						return value == labels.get(key) ? anno : null;
					}
				} catch (NullPointerException e ) {
				} catch (NoSuchMethodException e) {
				} catch (SecurityException e) {
				} catch (IllegalAccessException e) {
				} catch (IllegalArgumentException e) {
				} catch (InvocationTargetException e) {
				}
				return null;
			}
		}
		
		return anno;
	}
	
}
