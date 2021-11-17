/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.testfrk.Extractor.MethodAndType;
import io.github.testfrk.utils.AnnoUtil;

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
	public static void record(Map<String, List<MethodAndType>> map) throws Exception {
		record(map, Constants.DEFAULT_REQUESTMAPPING, Constants.DEFAULT_URL);
	}
	
	/**
	 * @param map         see Extractor.extract
	 * @param annoClass   annotation's class name
	 * @param annoUrlTag  annotation's url method
	 * @throws Exception
	 */
	public static void record(Map<String, List<MethodAndType>> map, String annoClass, String annoUrlTag) throws Exception {
		
		if (map == null || map.size() == 0 || annoClass == null || annoUrlTag == null) {
			throw new NullPointerException("map, or annoClass, or annoUrlTag cannot be null");
		}
		
		for (String c : map.keySet()) {
			String prefix = getPrefix(annoClass, annoUrlTag, c);
			List<String> urls = new ArrayList<>();
			for (MethodAndType m : map.get(c)) {
				String postfix = getPostfix(annoClass, annoUrlTag, m.getMethod());
				String url = toUrl(prefix, postfix);
				urls.add(url);
				RuleBase.urlToMethod.put(url, m.getMethod());
				RuleBase.urlToReqType.put(url, m.getType());
			}
			RuleBase.nameToUrls.put(c, urls);
		}
	}


	private static String getPostfix(String annoClass, String annoUrlTag, Method m)
			throws Exception {
		@SuppressWarnings("unchecked")
		Annotation ma = m.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ma == null) ? "" :(String) 
				AnnoUtil.getRequestTypeValue(ma, annoUrlTag);
	}


	private static String getPrefix(String annoClass, String annoUrlTag, String c)
			throws Exception {
		Class<?> cls = Class.forName(c);
		@SuppressWarnings("unchecked")
		Annotation ca = cls.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ca == null) ? "" : (String) 
				AnnoUtil.getRequestTypeValue(ca, annoUrlTag);
	}
	
	private static String toUrl(String prefix, String postfix) {
		String url = prefix + postfix;
		return url;
	}
	
}
