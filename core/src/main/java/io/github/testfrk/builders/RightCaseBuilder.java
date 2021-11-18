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
public class RightCaseBuilder extends CaseBuilder {

	@Override
	public JsonNode build(String url, JsonNode dataSet) {
		
		ObjectNode rightCase = new ObjectMapper().createObjectNode();

		ObjectNode rightVal = new ObjectMapper().createObjectNode();

		Iterator<String> iter = dataSet.fieldNames();
		while (iter.hasNext()) {
			String key = iter.next();
			
			try {
			
				ArrayNode array = (ArrayNode) dataSet.get(key);
				rightVal.set(key, array.get(0));
			
			} catch (Exception ex) {
				
				JsonNode values = dataSet.get(key);

				ObjectNode newCase = new ObjectMapper().createObjectNode();

				Iterator<String> names = values.fieldNames();
				while (names.hasNext()) {
					String subKey = names.next();
					newCase.set(subKey, values.get(subKey).get(0));
				}

				rightVal.set(key, newCase);
			}
		}

		rightCase.set(RuleBase.urlToReqType.get(url).toLowerCase() + "_" + url.substring(url.lastIndexOf("/") + 1)
				+ "_valid_all", rightVal);

		return rightCase;
	}

}
