/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet.
 * I do not known why. 
 * 
 * Do not modify.
 */
public class Recorder {

	/**
	 * @param map        see Extractor.extract
	 * @throws ClassNotFoundException 
	 */
	public static void record(Map<String, List<Method>> map) throws ClassNotFoundException {
		record(map, Constants.DEFAULT_REQUESTMAPPING, Constants.DEFAULT_URL);
	}
	
	
	/**
	 * @param map         see Extractor.extract
	 * @param annoClass   annotation's class name
	 * @param annoUrlTag  annotation's url method
	 * @throws ClassNotFoundException
	 */
	public static void record(Map<String, List<Method>> map, String annoClass, String annoUrlTag) throws ClassNotFoundException {
		
		if (map == null || map.size() == 0 || annoClass == null || annoUrlTag == null) {
			throw new NullPointerException("map, or annoClass, or annoUrlTag cannot be null");
		}
		
		for (String key : map.keySet()) {
			Class<?> cls = Class.forName(key);
			@SuppressWarnings("unchecked")
			Annotation a = cls.getAnnotation(
					(Class<? extends Annotation>) 
					Class.forName(annoClass));
			if (a != null) {
				
			}
			
			List<String> urls = new ArrayList<>();
		}
	}

}
