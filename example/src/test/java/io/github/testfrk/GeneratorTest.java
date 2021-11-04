/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import io.github.testfrk.analyzers.DefaultAnalyzer;
import io.github.testfrk.springboot.TestServer;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet
 */
public class GeneratorTest {

	public static void main(String[] args) throws Exception {
		new Generator("io.github.frks", 
				TestServer.class, 
				new DefaultAnalyzer("io.github.testfrk.springboot"))
		.generate();
	}

}
