/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet
 */
public class ExtractorTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Map<String, Object> filter = new HashMap<>();
		filter.put("method", RequestMethod.POST);
		
		System.out.println(Extractor.extract(
				Scanner.scan("io.github.testfrk", Component.class), 
				RequestMapping.class));
		
		System.out.println(Extractor.extract(
				Scanner.scan("io.github.testfrk", Component.class), 
				RequestMapping.class, filter));
	}

}
