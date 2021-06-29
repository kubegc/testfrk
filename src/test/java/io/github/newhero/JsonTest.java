/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 * 
 */

public class JsonTest {

	static String JSON = "{\r\n"
			+ "  \"name\" : \"henry\",\r\n"
			+ "  \"age\" : 1\r\n"
			+ "}";
	
	public static void main(String[] args) throws Exception {
		ObjectNode json = new ObjectMapper().createObjectNode();
		json.put("name", "henry");
		json.put("age", 1);
		System.out.println(json.toPrettyString().replaceAll("\"", "\\\\\""));
	}
	
}
