/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.values;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.testfrk.RuleBase;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since  0.6
 */
public abstract class AbstractValue {

	protected static Properties props = new Properties();
	
	static {
		File file = new File("config/defvalue.conf");
		if (file.exists()) {
			try {
				props.load(new FileInputStream(file));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void addValue(ArrayNode list, String key) {
		String val = props.getProperty(key);
		if (val != null) {
			list.add(val);
		} else {
			System.out.println("config " + key + " in conf/defvalue.conf");
		}
	}
	
	/**
	 * @param url               url
	 * @param dataStruct        dataStruct           
	 * @param dataset           dataset
	 * @return   node
	 */
	public ObjectNode rightParameterValueData(String url, JsonNode dataStruct) {
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
	public abstract ArrayNode wrongParameterValueData(String url, JsonNode dataStruct);
	
	/**
	 * @param url      url
	 * @param dataStruct  ds
	 * @return list
	 */
	public abstract ArrayNode nullParameterValueData(String url, ObjectNode dataStruct);
	
	public abstract ObjectNode getObjectValues(String key, String genericName, Class<?>[] tags) throws Exception;
	
	public abstract ArrayNode  getPrimitiveValues(String key, Parameter p) throws Exception;
	
	public abstract ArrayNode  getPrimitiveValues(String key, Field f, String tag) throws Exception;
	
	public abstract String checkPrimitiveParameter(String url, Method m, int i) throws Exception;
	
	public abstract Class<?>[] checkObjectParameter(String url, Method m, int i) throws Exception;
}
