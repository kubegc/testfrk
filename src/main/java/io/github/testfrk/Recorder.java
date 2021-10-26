/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
 */
public class Recorder {

	/**
	 * @param map        see Extractor.extract
	 * @throws Exception 
	 */
	public static void record(Map<String, List<Method>> map) throws Exception {
		record(map, Constants.POST_REQUEST_TYPE, Constants.DEFAULT_REQUESTMAPPING, Constants.DEFAULT_URL);
	}
	
	/**
	 * @param map        see Extractor.extract
	 * @throws Exception 
	 */
	public static void record(Map<String, List<Method>> map, String reqType) throws Exception {
		record(map, reqType, Constants.DEFAULT_REQUESTMAPPING, Constants.DEFAULT_URL);
	}
	
	
	/**
	 * @param map         see Extractor.extract
	 * @param annoClass   annotation's class name
	 * @param annoUrlTag  annotation's url method
	 * @throws Exception
	 */
	public static void record(Map<String, List<Method>> map, String reqType, String annoClass, String annoUrlTag) throws Exception {
		
		if (map == null || map.size() == 0 || annoClass == null || annoUrlTag == null) {
			throw new NullPointerException("map, or annoClass, or annoUrlTag cannot be null");
		}
		
		for (String c : map.keySet()) {
			String prefix = getPrefix(annoClass, annoUrlTag, c);
			List<String> urls = new ArrayList<>();
			for (Method m : map.get(c)) {
				String postfix = getPostfix(annoClass, annoUrlTag, m);
				String url = toUrl(prefix, postfix);
				urls.add(url);
				RuleBase.urlToMethod.put(url, m);
				RuleBase.urlToReqType.put(url, reqType);
			}
			RuleBase.nameToUrls.put(c, urls);
		}
	}


	private static String getPostfix(String annoClass, String annoUrlTag, Method m)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		@SuppressWarnings("unchecked")
		Annotation ma = m.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ma == null) ? "" :(String) 
				Utils.getValue(ma, annoUrlTag);
	}


	private static String getPrefix(String annoClass, String annoUrlTag, String c)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?> cls = Class.forName(c);
		@SuppressWarnings("unchecked")
		Annotation ca = cls.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ca == null) ? "" : (String) 
				Utils.getValue(ca, annoUrlTag);
	}
	
	private static String toUrl(String prefix, String postfix) {
		String url = prefix + postfix;
		return url;
	}
	
}
