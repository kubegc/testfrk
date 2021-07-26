/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import io.github.newhero.springboot.TestServer;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class TestCasesGenerator {

    public static void main(String[] args) throws Exception {
    	Analyzer analyzer = new Analyzer("io.github.newhero.springboot");
    	analyzer.start();
    	
    	Generator generator = new Generator(analyzer, TestServer.class, "io.github.newhero.tests");
    	generator.start();
    }

}
