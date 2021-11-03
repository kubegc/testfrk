/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
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

import io.github.testfrk.values.AbstractValueGenerator;
import io.github.testfrk.values.DefaultValueGenerator;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.30
 */
public class Analyzer {

	/**
	 * value
	 */
	protected final AbstractValueGenerator valueUtil;
	
	/**
	 * pkgName
	 */
	protected final String pkgName;

	/**
	 * @param pkgName     package
	 * @throws Exception
	 */
	public Analyzer(String pkgName) throws Exception {
		this(new DefaultValueGenerator(), pkgName);
	}
	
	/**
	 * @param valueGenerator   value
	 * @param pkgName
	 * @throws Exception
	 */
	public Analyzer(AbstractValueGenerator valueGenerator, String pkgName) throws Exception {
		this.valueUtil = valueGenerator;
		this.pkgName   = pkgName;
		Recorder.record(Extractor.extract(
				Scanner.scan(pkgName)));
	}
	
	/**
	 * analyse requested data
	 * 
	 * @return   data set
	 */
	public JsonNode analyse() {
		
		ObjectNode url2data = new ObjectMapper().createObjectNode();
		
		// for each url, analyse its all possible valid and invalid data
		for (String url : RuleBase.urlToMethod.keySet()) {
			try {
				// the value is all possible valid and invalid data
				url2data.set(url, analyseData(url, RuleBase.urlToMethod.get(url)));
			} catch (Exception re) { 
				System.out.println("stop analysing " + url);
				re.printStackTrace();
			} 
		}
		
		return url2data;
	}
	
	public ArrayNode analyseData(String url, Method m) throws Exception {
		
		// no data
		if (m.getParameterCount() == 0) {
			return new ObjectMapper().createArrayNode();
		}
		
		ObjectNode dataStruct = dataStruct(url, m);
		return dataContent(url, dataStruct);
	}

	private ObjectNode dataStruct(String url, Method m) throws Exception {
		
		ObjectNode dataStruct = new ObjectMapper().createObjectNode();
		
		for (int i = 0 ; i < m.getParameterCount(); i++) {
			
			Parameter p = m.getParameters()[i];
			Type t = m.getGenericParameterTypes()[i];
			// 如果是Java基本数据类型（String, Integer等）, 必须有RequestParam标签
			if (JavaUtil.isPrimitive(t.getTypeName())) {
				// 每个参数都必须带Validated
				Validated v = p.getAnnotation(Validated.class);
				assertNotNull(url, p, v, Validated.class);
				
				RequestParam rp = p.getAnnotation(RequestParam.class);
				assertNotNull(url, p, rp, RequestParam.class);
				String name = rp.value() == null || rp.value().length() == 0 
											? p.getName() : rp.value(); 
				dataStruct.set(name, valueUtil.getPrimitiveValues(m.getName(), p));
			} 
			// 这些情况暂时不处理
			else if (JavaUtil.isList(t.getTypeName()) 
					|| JavaUtil.isSet(t.getTypeName())
					|| JavaUtil.isMap(t.getTypeName())) {
				throw new RuntimeException("Unsupport parameters types: list, Set and Map");
			} 
			// 如果是Java对象，必须包含RequestBody标签
			else {
				// 每个参数都必须带Validated
				Validated v = p.getAnnotation(Validated.class);
				assertNotNull(url, p, v, Validated.class);
				
				RequestBody rb = p.getAnnotation(RequestBody.class);
				assertNotNull(url, p, rb, RequestBody.class);
				dataStruct = valueUtil.getObjectValues(DefaultValueGenerator
						.getClassName(t.getTypeName()), v.value());
			}
		}
		return dataStruct;
	}
	
	/**
	 * @param url
	 * @param dataStruct
	 * @return
	 */
	public static ArrayNode dataContent(String url, ObjectNode dataStruct) {
		ArrayNode dataset = new ObjectMapper().createArrayNode();
		// a testcase, all parameters have right values
		dataset.add(rightParameterValueData(url, dataStruct));
		// N parameters generate N testcases, each testcase has a invalid value 
		dataset.addAll(wrongParameterValueData(url, dataStruct));
		return dataset;
	}

	/**
	 * @param url               url
	 * @param dataStruct        dataStruct           
	 * @param dataset           dataset
	 * @return   object node
	 */
	private static ObjectNode rightParameterValueData(String url, ObjectNode dataStruct) {
		ObjectNode rightCase = new ObjectMapper().createObjectNode();
		
		ObjectNode rightVal  = new ObjectMapper().createObjectNode();
		
		Iterator<String> iter = dataStruct.fieldNames();
		while(iter.hasNext()) {
			String key = iter.next();
			ArrayNode array = (ArrayNode) dataStruct.get(key);
			rightVal.set(key, array.get(0));
		}
		
		rightCase.set(RuleBase.urlToReqType.get(url).toLowerCase() + "_" 
						+ url.substring(url.lastIndexOf("/") + 1) + "_valid_all", rightVal);
		
		return rightCase;
	}
	
	private static ArrayNode wrongParameterValueData(String url, ObjectNode dataStruct) {
		ArrayNode wrongCaseList = new ObjectMapper().createArrayNode();
		for (int i = 0; i < dataStruct.size(); i++) {
			ObjectNode wrongCase = new ObjectMapper().createObjectNode();
			ObjectNode wrongVal  = new ObjectMapper().createObjectNode();
			Iterator<String> iter = dataStruct.fieldNames();
			
			int wrongValuePos = 0;
			
			String postfix = "";
			
			while(iter.hasNext()) {
				String key = iter.next();
				if (wrongValuePos == i) {
					postfix = key;
					wrongVal.set(key, dataStruct.get(key).get(1));
				} else {
					wrongVal.set(key, dataStruct.get(key).get(0));
				}
				++wrongValuePos;
			}
			
			wrongCase.set(RuleBase.urlToReqType.get(url).toLowerCase() 
							+ "_" + url.substring(url.lastIndexOf("/") + 1) 
							+ "_invalid_" + postfix, wrongVal);
			wrongCaseList.add(wrongCase);
		}
		return wrongCaseList;
	}

	public static void assertNotNull(String url, Parameter p, Annotation a, Class<?> ac) {
		if (a == null) {
			throw new RuntimeException("the parameter " + p.getName() + " in " + url 
					+ " missing annotation " + ac.getName());
		}
	}
	
	public String getPkgName() {
		return pkgName;
	}
}
