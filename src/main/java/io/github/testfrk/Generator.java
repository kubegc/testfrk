/**
 * Copyrigt (2021, ) Institute of Software, Chinese Academy of Sciences
 */
package io.github.testfrk;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.testfrk.utils.FileUtil;

/**
 * 
 * @author wuheng@iscas.ac.cn
 * @since 2021.6.29
 */
public class Generator {

	protected final String pkgName;
	
	protected final Class<?> bootstrap;
	
	protected final JsonNode testcases;

	public Generator(String pkgName, Class<?> bootstrap, JsonNode testcases) {
		super();
		this.pkgName = pkgName;
		this.bootstrap = bootstrap;
		this.testcases = testcases;
	}

	public void generate() throws Exception {
		for (String name : RuleBase.nameToUrls.keySet()) {
			String cls = Class.forName(name).getSimpleName();
			StringBuilder sb = new StringBuilder();
//			sb.append(FileUtil.read("templates/classtmp").replace("#TESTCASE_PACKAGE#", pkgName)
//					.replace("#BOOTSTRAP#", bootstrap.getName()).replace("#SOURCE_PACKAGE#", analyzer.pkgName)
//					.replace("#CLASSNAME#", clz.getSimpleName() + "Test"));
		}
	}
	
}
