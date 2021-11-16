/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;


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
public abstract class Analyzer {

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
	
	/**
	 * @param url
	 * @param m
	 * @return
	 * @throws Exception
	 */
	protected ArrayNode analyseData(String url, Method m) throws Exception {
		
		// no data
		if (m.getParameterCount() == 0) {
			return new ObjectMapper().createArrayNode();
		}
		
		// get data structure
		ObjectNode dataStruct = dataStruct(url, m);
		
		// get data value
		return dataValue(url, dataStruct);
	}

	/**
	 * @param url           url
	 * @param m             method
	 * @return              name-class mapper
	 * @throws Exception unable to analyse data
	 */
	protected ObjectNode dataStruct(String url, Method m) throws Exception {
		
		ObjectNode dataStruct = new ObjectMapper().createObjectNode();
		
		for (int i = 0 ; i < m.getParameterCount(); i++) {
			Type t = m.getGenericParameterTypes()[i];
			
			if (JavaUtil.isPrimitive(t.getTypeName())) {
				dataStruct.set(valueUtil.checkPrimitiveParameter(url, m, i), 
						valueUtil.getPrimitiveValues(m.getName(), m.getParameters()[i]));
			} else if (JavaUtil.isList(t.getTypeName()) 
					|| JavaUtil.isSet(t.getTypeName())
					|| JavaUtil.isMap(t.getTypeName())) {
				throw new RuntimeException("Unsupport parameters types: list, Set and Map");
			} else {
				dataStruct = valueUtil.getObjectValues(
						DefaultValueGenerator.getOuterClassName(t.getTypeName()), 
						DefaultValueGenerator.getInnerClassName(t.getTypeName()),
						valueUtil.checkObjectParameter(url, m, i));
			}
		}
		
		return dataStruct;
	}
	
	/**
	 * @param url
	 * @param dataStruct
	 * @return list
	 */
	protected ArrayNode dataValue(String url, ObjectNode dataStruct) {
		ArrayNode dataset = new ObjectMapper().createArrayNode();
		// a testcase, all parameters have right values
		dataset.add(rightParameterValueData(url, dataStruct));
		// N parameters generate N testcases, each testcase has a invalid value 
		dataset.addAll(wrongParameterValueData(url, dataStruct));
		// N parameters generate N testcases, each testcase has a null value
		dataset.addAll(nullParameterValueData(url, dataStruct));
		return dataset;
	}

	/**
	 * @param url               url
	 * @param dataStruct        dataStruct           
	 * @param dataset           dataset
	 * @return   node
	 */
	protected ObjectNode rightParameterValueData(String url, ObjectNode dataStruct) {
		ObjectNode rightCase = new ObjectMapper().createObjectNode();
		
		ObjectNode rightVal  = new ObjectMapper().createObjectNode();
		
		Iterator<String> iter = dataStruct.fieldNames();
		while(iter.hasNext()) {
			String key = iter.next();
			try {
				ArrayNode array = (ArrayNode) dataStruct.get(key);
				rightVal.set(key, array.get(0));
			} catch (Exception ex) {
				JsonNode values = dataStruct.get(key);
				
				ObjectNode newCase = new ObjectMapper().createObjectNode();
				
				Iterator<String> names = values.fieldNames();
				while (names.hasNext()) {
					String subKey = names.next();
					newCase.set(subKey, values.get(subKey).get(0));
				}
				
				rightVal.set(key, newCase);
			}
		}
		
		rightCase.set(RuleBase.urlToReqType.get(url).toLowerCase() + "_" 
						+ url.substring(url.lastIndexOf("/") + 1) + "_valid_all", rightVal);
		
		return rightCase;
	}
	
	/**
	 * @param url      url
	 * @param dataStruct  ds
	 * @return list
	 */
	protected abstract ArrayNode wrongParameterValueData(String url, ObjectNode dataStruct);
	
	/**
	 * @param url      url
	 * @param dataStruct  ds
	 * @return list
	 */
	protected abstract ArrayNode nullParameterValueData(String url, ObjectNode dataStruct);

	/**
	 * @return package name
	 */
	public String getPkgName() {
		return pkgName;
	}
}
