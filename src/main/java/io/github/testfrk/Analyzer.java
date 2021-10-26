/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.utils.JavaUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Analyzer {

	protected Map<String, Class<?>>     nameToClass        = new HashMap<>();
	
	protected Map<String, List<String>> nameToUrlGroup     = new HashMap<>();

	protected Map<String, Method>       urlToMethod        = new HashMap<>();
	
	protected Map<String, String>       urlToReqType       = new HashMap<>();

	protected Map<String, ObjectNode>   urlToJsonData      = new HashMap<>();

	protected final String pkgName;

	public Analyzer(String pkgName) {
		super();
		this.pkgName = pkgName;
	}

	public void start() throws Exception {
		doStart(RequestMapping.class);
	}

	@SuppressWarnings("unchecked")
	void doStart(Class<? extends Annotation> requestMappingAnnotation) throws Exception {

		// find all (controller) classes
		for (Class<?> aClass : Scanner.scan(pkgName, requestMappingAnnotation)) {
			nameToClass.put(aClass.getName(), aClass);

			// find all methods exported as HttpServices
			for (Method aMethod : getMethodsWithRequestMapping(aClass)) {

				String url = getUrl(aClass, aMethod);
				bindMutipleUrlToClass(url, aClass);
				urlToMethod.put(url, aMethod);
				urlToReqType.put(url, getRequestType(aMethod));
				
				// find all required parameter data for a specified HttpService
				ObjectNode json = extractDataFromMethod(aMethod); 
				urlToJsonData.put(url, json.size() == 0 ? null : json);
			}
		}
	}

	/*********************************************************************
	 * 
	 *  Extract Data
	 * 
	 *********************************************************************/
	
	protected ObjectNode extractDataFromMethod(Method aMethod) throws Exception {
		ObjectNode json = new ObjectMapper().createObjectNode();
		for (Parameter param : getValidParameters(aMethod.getParameters())) {
			String typeName = param.getParameterizedType().getTypeName();
			if (JavaUtil.isPrimitive(typeName)) {
				json.put(param.getName(), typeName);
			} else if (JavaUtil.isSimpleObjectType(typeName)) {
				merge(json, extractDataFromClassFields(typeName, null));
			} else if (JavaUtil.isGenericObjectType(typeName)) {
//				json.set(param.getName(), extractDataFromClassFields(
//									getExplicitClassName(typeName), 
//									getImpliedClassName(typeName)));
				merge(json,extractDataFromClassFields(
						getExplicitClassName(typeName), 
						getImpliedClassName(typeName)));
			} else {
				// I believe this condition is not exist.
				continue;
			}
		}
		return json;
	}

	
	// TODO, support nested object later 
	protected ObjectNode extractDataFromClassFields(String explicitClassName, String impliedClassName) throws Exception {
		ObjectNode json = new ObjectMapper().createObjectNode();
		for (Field f : Class.forName(explicitClassName).getDeclaredFields()) {
			String typeName = f.getGenericType().getTypeName();
			if (f.getGenericType().getTypeName().equals("T")) {
				json.set(f.getName(), extractDataFromClassFields(impliedClassName, null));
			} else {
				json.put(f.getName(), typeName);
			}
		}
		return json;
	}
	
	/*********************************************************************
	 * 
	 *  Core 
	 * 
	 *********************************************************************/

	String getUrl(Class<?> clz, Method method) {
		return getUrlPrefixFromClass(clz) + getUrlPostfixFromMethod(method);
	}
	
	String getUrlPostfixFromMethod(Method method) {
		RequestMapping reqMap = method.getAnnotation(RequestMapping.class);
		return reqMap.value()[0];
	}
	
	String getUrlPrefixFromClass(Class<?> clz) {
		RequestMapping reqMap = clz.getAnnotation(RequestMapping.class);
		return reqMap.value()[0] == null ? "" : reqMap.value()[0];
	}
	
	List<Method> getMethodsWithRequestMapping(Class<?> clz) {
		List<Method> list = new ArrayList<>();
		for (Method m : clz.getDeclaredMethods()) {
			RequestMapping reqMap = m.getAnnotation(RequestMapping.class);
			if (reqMap == null) {
				continue;
			}
			list.add(m);
		}
		return list;
	}

	List<Parameter> getValidParameters(Parameter[] pArray) {
		List<Parameter> list = new ArrayList<>();
		for (Parameter p : pArray) {
			// ignore javax.servlet.http.HttpServletResponse
			// and javax.servlet.http.HttpServletRequest
			if (p.getParameterizedType().getTypeName().startsWith("javax.servlet.http")) {
				continue;
			}
			list.add(p);
		}
		return list;
	}
	
	String getImpliedClassName(String typeName) {
		int idx = typeName.indexOf("<");
		return typeName.substring(idx + 1, typeName.length() - 1);
	}

	String getExplicitClassName(String typeName) {
		int idx = typeName.indexOf("<");
		return typeName.substring(0, idx);
	}

	void merge(JsonNode thisJson, JsonNode mergedJson) throws Exception {
		Iterator<String> it = mergedJson.fieldNames();
		while (it.hasNext()) {
			String key = it.next();
			((ObjectNode) thisJson).set(key, mergedJson.get(key));
		}
	}
	
	String getRequestType(Method m) {
		RequestMapping reqMap = m.getAnnotation(RequestMapping.class);
		return reqMap.method()[0].name();
	}
	
	void bindMutipleUrlToClass(String url, Class<?> clz) {
		List<String> list = nameToUrlGroup.get(clz.getName()) == null 
				? new ArrayList<>() : nameToUrlGroup.get(clz.getName());
		list.add(url);
		nameToUrlGroup.put(clz.getName(), list);
	}

	/*********************************************************************
	 * 
	 *  Getter 
	 * 
	 *********************************************************************/
	
	public Map<String, Class<?>> getNameToClass() {
		return nameToClass;
	}

	public Map<String, List<String>> getNameToUrlGroup() {
		return nameToUrlGroup;
	}

	public Map<String, Method> getUrlToMethod() {
		return urlToMethod;
	}

	public Map<String, String> getUrlToReqType() {
		return urlToReqType;
	}

	public Map<String, ObjectNode> getUrlToJsonData() {
		return urlToJsonData;
	}

	public String getPkgName() {
		return pkgName;
	}

	
}
