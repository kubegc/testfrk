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
public class NullValueCaseBuilder extends CaseBuilder {

	@Override
	public JsonNode build(String url, JsonNode dataSet) {
		
		ArrayNode nullCaseList = new ObjectMapper().createArrayNode();
		for (int i = 0; i < dataSet.size(); i++) {
			ObjectNode nullCase = new ObjectMapper().createObjectNode();
			ObjectNode nullVal  = new ObjectMapper().createObjectNode();
			Iterator<String> iter = dataSet.fieldNames();
			
			int nullValuePos = 0;
			
			String postfix = "";
			
			while(iter.hasNext()) {
				String key = iter.next();
				if (nullValuePos == i) {
					postfix = key;
					nullVal.set(key, new ObjectMapper().nullNode());
				} else {
					nullVal.set(key, dataSet.get(key).get(0));
				}
				++nullValuePos;
			}
			
			try {
				if (!dataSet.get(postfix).get(0).isNull()) {
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
