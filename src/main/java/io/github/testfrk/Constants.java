/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet.
 * I do not known why. 
 * 
 * Do not modify.
 */
public class Constants {

	/*******************************************************
	 * 
	 *
	 *
	 *******************************************************/
	public static String SPRINGBOOT_REQUESTMAPPING    = "org.springframework.web.bind.annotation.RequestMapping";
	
	public static String DEFAULT_REQUESTMAPPING       = SPRINGBOOT_REQUESTMAPPING;
	
	
	/*******************************************************
	 * 
	 *
	 *
	 *******************************************************/
	public static Map<String, Object> SPRINGBOOT_POST = new HashMap<>(); 

	public static Map<String, Object> SPRINGBOOT_GET  = new HashMap<>();
	
	public static Map<String, Object> SPRINGBOOT_PUT  = new HashMap<>();
	
	public static Map<String, Object> SPRINGBOOT_DEL  = new HashMap<>();
	
	static {
		SPRINGBOOT_POST.put("method", RequestMethod.POST);
		SPRINGBOOT_GET.put("method", RequestMethod.GET);
		SPRINGBOOT_PUT.put("method", RequestMethod.PUT);
		SPRINGBOOT_DEL.put("method", RequestMethod.DELETE);
	}
	
	public static Map<String, Object> DEFAULT_POST    = SPRINGBOOT_POST;
	
	public static Map<String, Object> DEFAULT_GET     = SPRINGBOOT_GET;
	
	public static Map<String, Object> DEFAULT_PUT     = SPRINGBOOT_PUT;
	
	public static Map<String, Object> DEFAULT_DEL     = SPRINGBOOT_DEL;
	
	/*******************************************************
	 * 
	 *
	 *
	 *******************************************************/
	
	public static String SPRINGBOOT_URL               = "value"; 
	
	public static String DEFAULT_URL                  = SPRINGBOOT_URL;
}
