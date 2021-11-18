/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.olds;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.RuleBase;
import io.github.testfrk.values.AbstractValueGenerator;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.30
 */
public class DefaultAnalyzer extends Analyzer {

	public DefaultAnalyzer(String pkgName) throws Exception {
		super(pkgName);
	}

	public DefaultAnalyzer(AbstractValueGenerator valueGenerator, String pkgName) throws Exception {
		super(valueGenerator, pkgName);
	}

	/**
	 * @param url      url
	 * @param dataStruct  ds
	 * @return list
	 */
	protected ArrayNode wrongParameterValueData(String url, ObjectNode dataStruct) {
		ArrayNode wrongCaseList = new ObjectMapper().createArrayNode();
		for (int i = 0; i < dataStruct.size(); i++) {
			ObjectNode wrongCase = new ObjectMapper().createObjectNode();
			ObjectNode wrongVal  = new ObjectMapper().createObjectNode();
			Iterator<String> iter = dataStruct.fieldNames();
			
			int wrongValuePos = 0;
			
			String postfix = "";
			
			while(iter.hasNext()) {
				String key = iter.next();
				JsonNode v = dataStruct.get(key);
				if (wrongValuePos == i) {
					postfix = key;
					if (!v.isObject()) {
						wrongVal.set(key, v.get(1));
					} 
				} else {
					if (!v.isObject()) {
						wrongVal.set(key, v.get(1));
					}
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

	@Override
	protected ArrayNode nullParameterValueData(String url, ObjectNode dataStruct) {
		ArrayNode nullCaseList = new ObjectMapper().createArrayNode();
		for (int i = 0; i < dataStruct.size(); i++) {
			ObjectNode nullCase = new ObjectMapper().createObjectNode();
			ObjectNode nullVal  = new ObjectMapper().createObjectNode();
			Iterator<String> iter = dataStruct.fieldNames();
			
			int nullValuePos = 0;
			
			String postfix = "";
			
			while(iter.hasNext()) {
				String key = iter.next();
				if (nullValuePos == i) {
					postfix = key;
					nullVal.set(key, new ObjectMapper().nullNode());
				} else {
					nullVal.set(key, dataStruct.get(key).get(0));
				}
				++nullValuePos;
			}
			
			try {
				if (!dataStruct.get(postfix).get(0).isNull()) {
					nullCase.set(RuleBase.urlToReqType.get(url).toLowerCase() 
							+ "_" + url.substring(url.lastIndexOf("/") + 1) 
							+ "_null_" + postfix, nullVal);
					nullCaseList.add(nullCase);
				}
			} catch (Exception ex) {
				
			}
		}
		return nullCaseList;
	}

}
