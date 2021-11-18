/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet
 */
public class AnalyzerTest {

	public static void main(String[] args) throws Exception {
		System.out.println(new Analyzer("io.github.testfrk.springboot").analyse().toPrettyString());
	}

}
