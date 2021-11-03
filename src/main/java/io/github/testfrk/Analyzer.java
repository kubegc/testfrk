/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Iterator;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kubesys.httpfrk.utils.JavaUtil;

import io.github.testfrk.values.AbstractValue;
import io.github.testfrk.values.DefaultValueImpl;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.30
 */
public class Analyzer {

	protected final AbstractValue valueUtil;
	
	protected final String pkgName;

	public Analyzer(String pkgName) throws Exception {
		this(new DefaultValueImpl(), pkgName);
	}
	
	public Analyzer(AbstractValue valueUtil, String pkgName) throws Exception {
		this.valueUtil = valueUtil;
		this.pkgName   = pkgName;
		Recorder.record(Extractor.extract(
				Scanner.scan(pkgName)));
	}
	
	public String getPkgName() {
		return pkgName;
	}

	/**
	 * modify by youself
	 */
	public static ArrayNode testcases(String prefix, ObjectNode node) {
		
		ArrayNode list = new ObjectMapper().createArrayNode();
		
		ObjectNode case1 = new ObjectMapper().createObjectNode();
		ObjectNode case1_content = new ObjectMapper().createObjectNode();
		Iterator<String> iter = node.fieldNames();
		while(iter.hasNext()) {
			String key = iter.next();
			ArrayNode array = (ArrayNode) node.get(key);
			case1_content.set(key, array.get(0));
		}
		case1.set(prefix + "_valid_all", case1_content);
		list.add(case1);
		
		for (int i = 0; i < node.size(); i++) {
			ObjectNode case2 = new ObjectMapper().createObjectNode();
			ObjectNode case2_content = new ObjectMapper().createObjectNode();
			Iterator<String> iter2 = node.fieldNames();
			int j = 0;
			String name = "";
			while(iter2.hasNext()) {
				String key2 = iter2.next();
				ArrayNode array2 = (ArrayNode) node.get(key2);
				if (j == i) {
					name = key2;
					case2_content.set(key2, array2.get(1));
				} else {
					case2_content.set(key2, array2.get(0));
				}
				++j;
			}
			case2.set(prefix + "_invalid_" + name, case2_content);
			list.add(case2);
		}
		 
		return list;
	}
	
	public JsonNode analyse() {
		ObjectNode node = new ObjectMapper().createObjectNode();
		
		for (String url : RuleBase.urlToMethod.keySet()) {
			try {
				Method ana =  Analyzer.class.getMethod(
						"analyse" + RuleBase.urlToReqType.get(url), 
						String.class, Method.class);
				node.set(url, (ArrayNode) ana.invoke(this, url, RuleBase.urlToMethod.get(url)));
			} catch (InvocationTargetException re) { 
				re.printStackTrace();
				System.out.println("stop analysing " + url);
			} catch (Exception e) {
				System.out.println("unsupport void static " + "analyse" + RuleBase.urlToReqType.get(url) 
							+ "(String url, Method m)");
				e.printStackTrace();
			} 
		}
		
		return node;
	}
	
	
	/**
	 * 请根据项目实施要求，进行二次改造
	 */
	public ArrayNode analysePOST(String url, Method m) throws Exception {
		if (m.getParameterCount() == 0) {
			return new ObjectMapper().createArrayNode();
		}
		
		ObjectNode params = new ObjectMapper().createObjectNode();
		for (int i = 0 ; i < m.getParameterCount(); i++) {
			Parameter p = m.getParameters()[i];
			Type t = m.getGenericParameterTypes()[i];
			// 每个参数都必须带Validated
			Validated v = p.getAnnotation(Validated.class);
			assertNotNull(url, p, v, Validated.class);
			// 如果是Java基本数据类型（String, Integer等）, 必须有RequestParam标签
			if (JavaUtil.isPrimitive(t.getTypeName())) {
				RequestParam rp = p.getAnnotation(RequestParam.class);
				assertNotNull(url, p, rp, RequestParam.class);
				String name = rp.value() == null || rp.value().length() == 0 
											? p.getName() : rp.value(); 
				params.set(name, valueUtil.getPrimitiveValues(m.getName(), p));
			} 
			// 这些情况暂时不处理
			else if (JavaUtil.isList(t.getTypeName()) 
					|| JavaUtil.isSet(t.getTypeName())
					|| JavaUtil.isMap(t.getTypeName())) {
				throw new RuntimeException("Unsupport parameters types: list, Set and Map");
			} 
			// 如果是Java对象，必须包含RequestBody标签
			else {
				RequestBody rb = p.getAnnotation(RequestBody.class);
				assertNotNull(url, p, rb, RequestBody.class);
				params = valueUtil.getObjectValues(DefaultValueImpl
						.getClassName(t.getTypeName()), v.value());
			}
		}
		
		return testcases(url.substring(url.lastIndexOf("/") + 1), params);
	}

	public static void assertNotNull(String url, Parameter p, Annotation a, Class<?> ac) {
		if (a == null) {
			throw new RuntimeException("the parameter " + p.getName() + " in " + url 
					+ " missing annotation " + ac.getName());
		}
	}
}
