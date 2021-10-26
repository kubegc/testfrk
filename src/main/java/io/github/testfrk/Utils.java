/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 *        find all classes with a specified annotation. Note that the core
 *        algorithm comes from Internet. I do not known why.
 * 
 *        Do not modify.
 */
public class Utils {

	public static Object getValue(Annotation anno, String key) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Method m = anno.annotationType().getMethod(key);
		Object value = m.invoke(anno);
		if (value.getClass().isArray()) {
			Object[] v = (Object[]) value;
			return v[0];
		} 
		return value;
	}

}
