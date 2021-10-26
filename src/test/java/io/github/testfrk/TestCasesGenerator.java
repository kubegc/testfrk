/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;

import io.github.testfrk.Analyzer;
import io.github.testfrk.Generator;
import io.github.testfrk.Scanner;
import io.github.testfrk.springboot.TestServer;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class TestCasesGenerator {

	public static String pkgName = "io.github.newhero.springboot";

	public static Class<? extends Annotation> clsName = RequestMapping.class;
	
    public static void main(String[] args) throws Exception {
    	Set<Class<?>> clses =  Scanner.scan(pkgName, clsName);
    	Analyzer analyzer = new Analyzer("io.github.newhero.springboot");
    	analyzer.start();
    	
    	Generator generator = new Generator(analyzer, TestServer.class, "io.github.newhero.tests");
    	generator.start();
    }

}
