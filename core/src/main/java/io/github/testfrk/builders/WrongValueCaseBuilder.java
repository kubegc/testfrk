/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.builders;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.CaseBuilder;
import io.github.testfrk.RuleBase;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 0.6
 */
public class WrongValueCaseBuilder extends CaseBuilder {

	@Override
	public JsonNode build(String url, JsonNode dataSet) {
		
		ArrayNode wrongCaseList = new ObjectMapper().createArrayNode();
		for (int i = 0; i < dataSet.size(); i++) {
			ObjectNode wrongCase = new ObjectMapper().createObjectNode();
			ObjectNode wrongVal  = new ObjectMapper().createObjectNode();
			Iterator<String> iter = dataSet.fieldNames();
			
			int wrongValuePos = 0;
			
			String postfix = "";
			
			while(iter.hasNext()) {
				String key = iter.next();
				JsonNode v = dataSet.get(key);
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
		return dataSet;
	}

}
