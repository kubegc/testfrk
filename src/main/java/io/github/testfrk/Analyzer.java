/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.30
 */
public class Analyzer {

	public Analyzer(String pkgName) throws Exception {
		Recorder.record(Extractor.extract(
				Scanner.scan(pkgName)));
	}
	
	public JsonNode analyse() {
		ArrayNode list = new ObjectMapper().createArrayNode();
		
		for (String url : RuleBase.urlToMethod.keySet()) {
			try {
				Method ana =  Analyzer.class.getMethod(
						"analyse" + RuleBase.urlToReqType.get(url), 
						String.class, Method.class);
				ana.invoke(null, url, RuleBase.urlToMethod.get(url));
			} catch (Exception e) {
				System.out.println("unsupport void static " + "analyse" + RuleBase.urlToReqType.get(url) 
							+ "(String url, Method m)");
			} 
		}
		
		return list;
	}
	
	public static JsonNode analysePOST(String url, Method m) {
		System.out.println("---" + url);
		ObjectNode node = new ObjectMapper().createObjectNode();
		if (m.getParameterCount() == 0) {
			node.set(url, new ObjectMapper().createArrayNode());
		}
		
		return null;
	}

	private static void print(Annotation[] as) {
		for (Annotation a : as) {
			System.out.println(a);
		}
	}
}
