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
public class RecorderTest {

	public static void main(String[] args) throws Exception {
		Recorder.record(Extractor.extract(
				Scanner.scan("io.github.testfrk")));
		System.out.println(RuleBase.nameToUrls);
		System.out.println(RuleBase.urlToReqType);
		System.out.println(RuleBase.urlToMethod);
	}

}
