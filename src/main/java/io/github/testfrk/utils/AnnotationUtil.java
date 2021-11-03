/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * Get value
 */
public class AnnotationUtil {

	public static Object getValue(Annotation anno, String func) throws Exception {

		Method m = anno.annotationType().getMethod(func);
		Object value = m.invoke(anno);
		if (value.getClass().isArray()) {
			Object[] v = (Object[]) value;
			return v[0];
		} 
		return value;
	}
	
	public static void assertNotNull(String url, Parameter p, Annotation a, Class<?> ac) {
		if (a == null) {
			throw new RuntimeException("the parameter " + p.getName() + " in " + url 
					+ " missing annotation " + ac.getName());
		}
	}

}
