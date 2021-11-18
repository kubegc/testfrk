/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;


import java.lang.reflect.Method;
import java.lang.reflect.Type;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.kubesys.httpfrk.utils.JavaUtil;

import io.github.testfrk.values.AbstractValue;
import io.github.testfrk.values.DefaultValue;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 */
public class Analyzer {

	/**
	 * value
	 */
	protected final AbstractValue valueUtil;
	
	/**
	 * pkgName
	 */
	protected final String pkgName;

	/**
	 * @param pkgName     package
	 * @throws Exception
	 */
	public Analyzer(String pkgName) throws Exception {
		this(new DefaultValue(), pkgName);
	}
	
	/**
	 * @param valueGenerator   value
	 * @param pkgName
	 * @throws Exception
	 */
	public Analyzer(AbstractValue valueGenerator, String pkgName) throws Exception {
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
	protected ObjectNode analyseData(String url, Method m) throws Exception {
		
		// no data
		if (m.getParameterCount() == 0) {
			return new ObjectMapper().createObjectNode();
		}
		
		// get data structure
		return dataStruct(url, m);
		
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
						DefaultValue.getOuterClassName(t.getTypeName()), 
						DefaultValue.getInnerClassName(t.getTypeName()),
						valueUtil.checkObjectParameter(url, m, i));
			}
		}
		
		return dataStruct;
	}

	public String getPkgName() {
		return pkgName;
	}
	
}
