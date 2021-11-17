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
 * @since  0.6
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
		
		for (String classname : map.keySet()) {
			List<String> urls = new ArrayList<>();
			for (MethodAndType m : map.get(classname)) {
				String url = toUrl(
						getUrlPart(annoClass, annoUrlTag, classname), 
						getUrlPart(annoClass, annoUrlTag, m.getMethod()));
				
				urls.add(url);
				RuleBase.urlToMethod.put(url, m.getMethod());
				RuleBase.urlToReqType.put(url, m.getType());
			}
			RuleBase.nameToUrls.put(classname, urls);
		}
	}


	private static String getUrlPart(String annoClass, String annoUrlTag, Method m)
			throws Exception {
		@SuppressWarnings("unchecked")
		Annotation ma = m.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ma == null) ? "" :(String) 
				AnnoUtil.getValue(ma, annoUrlTag);
	}


	private static String getUrlPart(String annoClass, String annoUrlTag, String c)
			throws Exception {
		Class<?> cls = Class.forName(c);
		@SuppressWarnings("unchecked")
		Annotation ca = cls.getAnnotation(
				(Class<? extends Annotation>) 
				Class.forName(annoClass));
		return (ca == null) ? "" : (String) 
				AnnoUtil.getValue(ca, annoUrlTag);
	}
	
	private static String toUrl(String urlPrefix, String urlPostfix) {
		return urlPrefix == null ? urlPostfix : urlPrefix + urlPostfix;
	}
	
}
