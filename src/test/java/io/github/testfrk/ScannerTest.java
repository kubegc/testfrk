/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import org.springframework.stereotype.Component;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.10.26
 * 
 * find all classes with a specified annotation.
 * Note that the core algorithm comes from Internet
 */
public class ScannerTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		System.out.println(Scanner.scan("io.github.testfrk", Component.class));
	}

}
