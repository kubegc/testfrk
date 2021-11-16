/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.4
 * 
 * Get value
 */
public class AnnoUtil {

	public static Object getValue(Annotation anno, String func) throws Exception {

		Method m = anno.annotationType().getMethod(func);
		Object value = m.invoke(anno);
		if (value.getClass().isArray()) {
			Object[] v = (Object[]) value;
			return v[0];
		} 
		return value;
	}
	
	public static void assertNotNull(Annotation a, Class<?> ac, String url, Parameter p) {
		if (a == null) {
			throw new RuntimeException("the parameter " + p.getName() + " in " + url 
					+ " missing annotation " + ac.getName());
		}
	}
	
	public static Annotation[] usedAnnotations(Annotation[] as, String func, String[] jsr303, String tag) throws Exception {
		
		List<Annotation> list = new ArrayList<>();
		for (Annotation a: valuesAnnotations(as, jsr303).values()) {
			Method m = a.annotationType().getDeclaredMethod("groups");
			Class<?>[] vs =  (Class<?>[]) m.invoke(a);
			
			// from config case
			if (vs.length == 0 && tag == null) {
				list.add(a);
			}
			
			for (Class<?> v : vs) {
				if (v.getTypeName().equals(tag)) {
					list.add(a);
				}
			}
		}
		return list.toArray(new Annotation[] {});
		
	}
	
	public static boolean validParameter(Annotation[] as, String func, String[] jsr303, String tag) throws Exception {
	
		for (Annotation a : valuesAnnotations(as, jsr303).values()) {
			Method m = a.annotationType().getDeclaredMethod(func);
			Class<?>[] vs =  (Class<?>[]) m.invoke(a);
			
			// from config case
			if (vs.length == 0 && tag == null) {
				return true;
			}
			
			for (Class<?> v : vs) {
				if (v.getTypeName().equals(tag)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Map<String, Annotation> valuesAnnotations(Annotation[] as, String[] jsr303) {
		Map<String, Annotation> list = new HashMap<>();
		if (as == null) {
			return list;
		}
		
		for (Annotation a: as) {
			for (String c : jsr303) {
				if (a.annotationType().getTypeName().contains(c)) {
					list.put(a.annotationType().getTypeName(), a);
				}
			}
		}
		return list;
	}

}
