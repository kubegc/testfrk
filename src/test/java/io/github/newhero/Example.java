/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.newhero;

import io.github.newhero.http.TestServer;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Example {

    public static void main(String[] args) throws Exception {
    	Analyzer analyzer = new Analyzer("io.github.newhero.http");
    	analyzer.start();
    	Generator generator = new Generator(analyzer, TestServer.class, "com.newhero");
    	generator.start();
    }

}
