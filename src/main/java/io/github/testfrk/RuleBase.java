/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * It is used for storing intermediate results
 */
public class RuleBase {

	public static Map<String, List<String>> nameToUrls         = new HashMap<>();

	public static Map<String, Method>       urlToMethod        = new HashMap<>();
	
	public static Map<String, String>       urlToReqType       = new HashMap<>();

}
