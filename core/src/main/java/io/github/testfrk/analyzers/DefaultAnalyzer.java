/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.analyzers;


import java.util.Iterator;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.Analyzer;
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

}
