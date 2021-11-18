/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.values;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Properties;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	
	public abstract ObjectNode getObjectValues(String key, String genericName, Class<?>[] tags) throws Exception;
	
	public abstract ArrayNode  getPrimitiveValues(String key, Parameter p) throws Exception;
	
	public abstract ArrayNode  getPrimitiveValues(String key, Field f, String tag) throws Exception;
	
	public abstract String checkPrimitiveParameter(String url, Method m, int i) throws Exception;
	
	public abstract Class<?>[] checkObjectParameter(String url, Method m, int i) throws Exception;
}
