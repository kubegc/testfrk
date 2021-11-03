/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk.values;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Parameter;
import java.util.Properties;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
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
	
	public abstract ObjectNode getObjectValues(String clsName, Class<?>[] tags) throws Exception;
	
	public abstract ArrayNode getPrimitiveValues(String cls, Parameter p);
	
}
