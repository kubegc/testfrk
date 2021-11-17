/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.testfrk.Extractor.Label;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * Just constants
 */
public class Constants {

	/*******************************************************
	 * 
	 *   Request type
	 *
	 *******************************************************/
	
	public static String POST_REQUEST_TYPE            = "POST";
	
	public static String GET_REQUEST_TYPE             = "GET";
	
	public static String PUT_REQUEST_TYPE             = "PUT";
	
	public static String DELETE_REQUEST_TYPE          = "DELETE";
	
	/*******************************************************
	 * 
	 *    Tag: RequestMapping
	 *
	 *******************************************************/
	public static String SPRINGBOOT_REQUESTMAPPING    = "org.springframework.web.bind.annotation.RequestMapping";
	
	public static String DEFAULT_REQUESTMAPPING       = SPRINGBOOT_REQUESTMAPPING;
	
	
	/*******************************************************
	 * 
	 *    Tag: Request type
	 *
	 *******************************************************/
	public static Label SPRINGBOOT_POST = new Label(RequestMapping.class, "method", RequestMethod.POST); 

	public static Label SPRINGBOOT_GET  = new Label(RequestMapping.class, "method", RequestMethod.GET);
	
	public static Label SPRINGBOOT_PUT  = new Label(RequestMapping.class, "method", RequestMethod.PUT);
	
	public static Label SPRINGBOOT_DEL  = new Label(RequestMapping.class, "method", RequestMethod.DELETE);
	
	public static Label SPRINGBOOT_ALL  = new Label(RequestMapping.class, "method", "ALL");
	

	public static Label DEFAULT_ALL     = SPRINGBOOT_ALL;
	
	public static Label DEFAULT_POST    = SPRINGBOOT_POST;
	
	public static Label DEFAULT_GET     = SPRINGBOOT_GET;
	
	public static Label DEFAULT_PUT     = SPRINGBOOT_PUT;
	
	public static Label DEFAULT_DEL     = SPRINGBOOT_DEL;
	
	/*******************************************************
	 * 
	 *      Tag: Request url
	 *
	 *******************************************************/
	
	public static String SPRINGBOOT_URL               = "value"; 
	
	public static String DEFAULT_URL                  = SPRINGBOOT_URL;
	
}
